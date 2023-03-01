package frc.robot.commands;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class PursuePose extends CommandBase {
    private final Pose2d desiredPose;

    public PursuePose(Pose2d desiredPose) {
        addRequirements(Drivetrain.get());

        this.desiredPose = desiredPose;
    }

    @Override
    public void execute() {
        
    }
}
