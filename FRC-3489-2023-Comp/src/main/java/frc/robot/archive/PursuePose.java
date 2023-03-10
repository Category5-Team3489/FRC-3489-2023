package frc.robot.archive;
// package frc.robot.commands;

// import edu.wpi.first.math.MathUtil;
// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.math.geometry.Pose2d;
// import edu.wpi.first.wpilibj2.command.CommandBase;
// import frc.robot.subsystems.PoseEstimator;

// public class PursuePose extends CommandBase {
//     private static final double MaxSpeedMetersPerSecond = 1.0;

//     private final Pose2d desiredPose;
//     private final PIDController x = new PIDController(0.1, 0, 0);
//     private final PIDController y = new PIDController(0.1, 0, 0);

//     public PursuePose(Pose2d desiredPose) {
//         this.desiredPose = desiredPose;
//     }

//     @Override
//     public void execute() {
//         Pose2d pose = PoseEstimator.get().getPoseMeters();

//         double xVelocity = x.calculate(pose.getX(), desiredPose.getX());
//         xVelocity = MathUtil.clamp(xVelocity, -MaxSpeedMetersPerSecond, MaxSpeedMetersPerSecond);
//         // Drivetrain.get().driveCommand.setAutoX(xVelocity);

//         double yVelocity = y.calculate(pose.getY(), desiredPose.getY());
//         yVelocity = MathUtil.clamp(yVelocity, -MaxSpeedMetersPerSecond, MaxSpeedMetersPerSecond);

//         // Drivetrain.get().driveCommand.setAutoY(yVelocity);
//     }
// }
