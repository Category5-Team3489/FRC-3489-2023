// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.Leds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TeleopLeds extends CommandBase {
    private final Leds leds;

    public TeleopLeds(Leds leds) {
        this.leds = leds;
        addRequirements(leds);
    }

    @Override
    public void initialize() {
        leds.startTimer();
        leds.setSolidRGB(0, 255, 0);
    }

    @Override
    public void execute() {}

    @Override
    public void end(boolean interrupted) {
        leds.stopLeds();
    }

    @Override
    public boolean isFinished() {
        if (leds.hasTimeEnded(2)) {
            return true;
        }
        return false;
    }
}
