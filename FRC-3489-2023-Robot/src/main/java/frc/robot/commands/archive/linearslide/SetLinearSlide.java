// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.archive.linearslide;

import frc.robot.Constants.LinearSlideConstants;
import frc.robot.subsystems.archive.LinearSlideOld;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class SetLinearSlide extends CommandBase {
    private final LinearSlideOld linearSlide;

    private final Double percentExtended;
    private final Double totalPercent;

    public SetLinearSlide(LinearSlideOld linearSlide, Double percentExtended, Double totalPercent) {
        this.linearSlide = linearSlide;
        this.percentExtended = percentExtended;
        this.totalPercent = totalPercent;
        

        addRequirements(linearSlide);
    }

    @Override
    public void initialize() {}

    @Override
    public void execute() {
        double percentNeeded = totalPercent - percentExtended;
        //if percent needed is positive, extend, if negative retract
        if (percentNeeded >= 0) {
            linearSlide.extend();
        }
        if (percentNeeded <= 0) {
            linearSlide.retract();
        }
    }

    @Override
    public void end(boolean interrupted) {
        linearSlide.stop();
        //set LEDs
        if (totalPercent == LinearSlideConstants.FullExtendEncoder) {
            
        }
    }

    @Override
    public boolean isFinished() {
        double percentNeeded = totalPercent - percentExtended;

        //if linear slide is extending and limit switch = true
        if (percentNeeded >= 0 && linearSlide.isExtended()) {
                return true;
        }

        //if linear slide is retracting and limit switch = true
        if (percentNeeded <= 0 && linearSlide.isRetracted()) {
            return true;
        }

        //if linear slide is setting half and position is < 10
        if (totalPercent == LinearSlideConstants.HalfExtendEncoder && linearSlide.getPosition() <= 10) {
                return true;
        }
        return false;
    }
}
