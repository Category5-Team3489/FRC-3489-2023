// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.archive.linearslide;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.archive.LinearSlideOld;

public class HomeLinearSlide extends CommandBase {
    private final LinearSlideOld linearSlide;

    public HomeLinearSlide(LinearSlideOld linearSlide) {
        this.linearSlide = linearSlide;

        addRequirements(linearSlide);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        linearSlide.retract();
    }

    @Override
    public void end(boolean interrupted) {
        linearSlide.stop();
        if (!interrupted) {
            linearSlide.setPosition(0);
        }
    }

    @Override
    public boolean isFinished() {
        return linearSlide.isRetracted();
    }
}
