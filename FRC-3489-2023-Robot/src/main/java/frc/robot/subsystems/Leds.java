// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import frc.robot.Constants.LedConstants;
import frc.robot.commands.leds.FlashLeds;
import frc.robot.general.LedColor;
import frc.robot.shuffleboard.Cat5Shuffleboard;

public class Leds extends SubsystemBase {
    private final ShuffleboardTab tab = Cat5Shuffleboard.getMainTab();
    private final GenericEntry colorEntry = tab.add("LED Color", "").getEntry();

    private final AddressableLED led = new AddressableLED(LedConstants.Port);
    private final AddressableLEDBuffer buffer = new AddressableLEDBuffer(LedConstants.Length);

    private boolean haveTeleopLedsFlashedThisEnable = false;

    // TODO Limit led setting bandwidth, compare led buffer with applied and unapplied on update then only set if they are different

    public Leds() {
        register();
        
        led.setLength(buffer.getLength());
        led.setData(buffer);
        led.start();

        ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("LEDs");
        diagnosticLayout.withSize(2, 1);
        diagnosticLayout.add("LEDs", LedDiognostic());
    }

    public void setSolidColor(LedColor color) {
        for (var i = 0; i < buffer.getLength(); i++) {
            color.apply(i, buffer);
        }
        led.setData(buffer);
        colorEntry.setString(color.toString());
    }

    public void stopLeds() {
        setSolidColor(LedColor.Off);
    }
    
    @Override
    public void periodic() {     
        tryFlashTeleopLeds();
    }

    public void tryFlashTeleopLeds() {
        if (DriverStation.isDisabled()) {
            haveTeleopLedsFlashedThisEnable = false;
            return;
        }

        if (!DriverStation.isTeleop() || haveTeleopLedsFlashedThisEnable) {
            return;
        }
        
        Command command = new FlashLeds(this, LedColor.White, 10, 0.1, 0.1)
            .withInterruptBehavior(InterruptionBehavior.kCancelIncoming);

        command.schedule();
        haveTeleopLedsFlashedThisEnable = true;
    }

    public Command getSolidColorForSecondsCommand(LedColor color, double seconds, boolean isInterruptible) {
        Runnable start = () -> {
            setSolidColor(color);
        };
        Runnable end = () -> {
            stopLeds();
        };

        InterruptionBehavior interruptBehavior = isInterruptible ?
            InterruptionBehavior.kCancelSelf : InterruptionBehavior.kCancelIncoming;

        return Commands.startEnd(start, end, this)
            .withTimeout(seconds)
            .withInterruptBehavior(interruptBehavior);
    }

    public CommandBase LedDiognostic() {
        return getSolidColorForSecondsCommand(LedColor.White, 5, true)
            .withName("Set Solid Color");
    }
}
