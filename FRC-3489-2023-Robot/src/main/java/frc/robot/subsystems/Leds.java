// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import frc.robot.Constants;
import frc.robot.Constants.LedConstants;
import frc.robot.commands.leds.FlashLeds;
import frc.robot.general.LedColor;

public class Leds extends SubsystemBase {
    private final ShuffleboardTab tab = Constants.getMainTab();
    public final GenericEntry teleopLedsFlashEntry = tab.add("Are Teleop LEDs Flashing", false).getEntry();
    private final GenericEntry colorEntry = tab.add("LED Color", "").getEntry();

    private final AddressableLED led = new AddressableLED(LedConstants.Port);
    private final AddressableLEDBuffer buffer = new AddressableLEDBuffer(LedConstants.Length);

    private boolean haveTeleopLedsFlashedThisEnable = false;

    public Leds() {
        register();
        
        led.setLength(buffer.getLength());
        led.setData(buffer);
        led.start();
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
        
        Command command = new FlashLeds(this, LedColor.White, 3, 1, 1)
            .withInterruptBehavior(InterruptionBehavior.kCancelIncoming);

        command.schedule();
        haveTeleopLedsFlashedThisEnable = true;
    }

    public Command getSolidColorForSecondsCommand(LedColor color, double seconds, boolean doesRunWhenDisabled, boolean isInteruptable) {
        Runnable start = () -> {
            setSolidColor(color);
            teleopLedsFlashEntry.setBoolean(true);
        };
        Runnable end = () -> {
            stopLeds();
            teleopLedsFlashEntry.setBoolean(false);
        };

        InterruptionBehavior interruptBehavior = isInteruptable ?
            InterruptionBehavior.kCancelSelf : InterruptionBehavior.kCancelIncoming;

        return Commands.startEnd(start, end, this)
            .withTimeout(seconds)
            .ignoringDisable(doesRunWhenDisabled)
            .withInterruptBehavior(interruptBehavior);
    }
}
