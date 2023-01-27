// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LedColor;
import frc.robot.Constants.LedConstants;
import frc.robot.commands.leds.FlashLeds;
import frc.robot.commands.leds.LedsCommandBase;
import frc.robot.commands.leds.TeleopBlinkLeds;

public class Leds extends SubsystemBase {

    public final AddressableLED led = new AddressableLED(LedConstants.Port);
    public final AddressableLEDBuffer buffer = new AddressableLEDBuffer(LedConstants.Length);

    private LedsCommandBase current;

    private boolean haveTeleopLedsBlinkedThisEnable = false;

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
    }

    public void stopLeds() {
        setSolidColor(LedColor.Off);
    }
    
    @Override
    public void periodic() {     
        tryBlinkTeleopLeds();
    }

    public void tryBlinkTeleopLeds() {
        if (DriverStation.isDisabled()) {
            haveTeleopLedsBlinkedThisEnable = false;
            return;
        }

        if (!DriverStation.isTeleop() || haveTeleopLedsBlinkedThisEnable) {
            return;
        }
        
        //TeleopBlinkLeds teleopLeds = new TeleopBlinkLeds(this);
        setCurrentCommand(new FlashLeds(this, LedColor.White, 3, 1, 1));
        haveTeleopLedsBlinkedThisEnable = true;
    }

    private void setCurrentCommand(LedsCommandBase command) {
        if (current != null && !current.isFinished()) {
            current.cancel();
        }
        current = command;
        current.schedule();
    }
    private boolean hasCurrentCommand() {
        if (current == null || current.isFinished()) {
            return false;
        }
        return true;
    }
    private boolean isCurrentCommandOverridable() {
        if (current == null || current.isFinished() || current.isOverridable()) {
            return true;
        }
        return false;
    }
}
