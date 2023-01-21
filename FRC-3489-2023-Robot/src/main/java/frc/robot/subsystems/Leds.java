// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.LedConstants;
import frc.robot.commands.TeleopLeds;

public class Leds extends SubsystemBase {

    public final AddressableLED led = new AddressableLED(LedConstants.LedPort);
    public final AddressableLEDBuffer buffer = new AddressableLEDBuffer(LedConstants.Buffer);

    private TeleopLeds teleopLeds;

    public final Timer timer = new Timer();

    public boolean hasLightEnded = false;

    public Leds() {
        register();
        led.setLength(buffer.getLength());
        led.setData(buffer);
        led.start();
    }

    // write()  led.setData(buffer);

    public void setSolidRGB(int r, int g, int b) {
        for (var i = 0; i < buffer.getLength(); i++) {
                buffer.setRGB(i, r, g, b);
        }
    }

    public void stopLeds() {
        setSolidRGB(0, 0, 0);
    }

    public void startTimer() {
        timer.start();
    }

    public boolean hasTimeEnded(int time) {
        double currentTime = timer.get();
        if (currentTime >= time) {
            hasLightEnded = true;
            return true;
        }
        return false;
    }
    
    @Override
    public void periodic() {
        teleopLight();
    }

    public void teleopLight() {
        if (!DriverStation.isTeleop() || hasLightEnded) {
            return;
        }
        teleopLeds = new TeleopLeds(this);
        teleopLeds.schedule();
    }

}
