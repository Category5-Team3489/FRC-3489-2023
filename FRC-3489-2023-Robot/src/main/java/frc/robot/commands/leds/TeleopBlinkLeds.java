// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.leds;

import frc.robot.Constants.LedConstants;
import frc.robot.subsystems.Leds;
import edu.wpi.first.wpilibj.Timer;

public class TeleopBlinkLeds extends LedsCommandBase {
    private final Timer timer = new Timer();

    public TeleopBlinkLeds(Leds leds) {
        super(leds);
    }

    @Override
    public void initialize() {
        timer.start();

        leds.setSolidRGB(0, 255, 0);
    }

    @Override
    public void end(boolean interrupted) {
        leds.stopLeds();
    }

    @Override
    public boolean isFinished() {
        double currentTime = timer.get();
        if (currentTime >= LedConstants.TeleopLedLength) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOverridable() {
        return false;
    }
}
