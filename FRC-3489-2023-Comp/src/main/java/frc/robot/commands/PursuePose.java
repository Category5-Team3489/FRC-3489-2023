package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.PoseEstimator;

public class PursuePose extends CommandBase {
    private static final double MaxSpeedMetersPerSecond = 1.0;

    private final Pose2d desiredPose;
    private final PIDController x = new PIDController(0.1, 0, 0);
    private final PIDController y = new PIDController(0.1, 0, 0);

    public PursuePose(Pose2d desiredPose) {
        this.desiredPose = desiredPose;
    }

    @Override
    public void initialize() {
        Drivetrain.get().driveCommand.setTargetAngle(desiredPose.getRotation());
        Drivetrain.get().driveCommand.setAutoXSupplier(() -> {
            Pose2d pose = PoseEstimator.get().getPose();
            double xVelocity = x.calculate(pose.getX(), desiredPose.getX());
            return MathUtil.clamp(xVelocity, -MaxSpeedMetersPerSecond, MaxSpeedMetersPerSecond);
        });
        Drivetrain.get().driveCommand.setAutoYSupplier(() -> {
            Pose2d pose = PoseEstimator.get().getPose();
            double yVelocity = y.calculate(pose.getY(), desiredPose.getY());
            return MathUtil.clamp(yVelocity, -MaxSpeedMetersPerSecond, MaxSpeedMetersPerSecond);
        });
    }
}
