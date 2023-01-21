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

public class Leds extends SubsystemBase {

    public final AddressableLED led = new AddressableLED(LedConstants.LedPort);
    public final AddressableLEDBuffer buffer = new AddressableLEDBuffer(LedConstants.Buffer);

    public final Timer timer = new Timer();

    public Leds() {
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

    public void startTimer() {
        timer.start();
    }
    
    public void teleopLed() {
        setSolidRGB(0, 255, 0);
    }

    
    @Override
    public void periodic() {
        if (DriverStation.isTeleop()) {
            teleopLed();
        }
    }

}
