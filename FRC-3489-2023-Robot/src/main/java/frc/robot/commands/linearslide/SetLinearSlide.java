// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.linearslide;

import frc.robot.subsystems.LinearSlide;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class SetLinearSlide extends CommandBase {
    private final LinearSlide linearSlide;

    public SetLinearSlide(LinearSlide linearSlide, double percentExtended) {
        this.linearSlide = linearSlide;

        addRequirements(linearSlide);
    }
}
