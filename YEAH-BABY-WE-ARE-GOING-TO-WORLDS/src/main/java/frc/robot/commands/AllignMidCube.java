// package frc.robot.commands;

// import edu.wpi.first.math.MathUtil;
// import edu.wpi.first.math.controller.PIDController;
// import edu.wpi.first.math.filter.SlewRateLimiter;
// import edu.wpi.first.math.geometry.Pose3d;
// import edu.wpi.first.math.geometry.Rotation2d;
// import edu.wpi.first.wpilibj2.command.CommandBase;
// import frc.robot.enums.LimelightPipeline;
// import frc.robot.subsystems.Drivetrain;
// import frc.robot.subsystems.Limelight;

// public class AllignMidCube extends CommandBase {
//     // Constants
//     private static final double TargetXMeters = 0;
//     private static final double TargetYMeters = 0;
//     private static final double ToleranceMeters = 0.1;
//     private static final double MaxXMetersPerSecond = 2;
//     private static final double MaxYMetersPerSecond = 2;
//     private static final Rotation2d TargetHeading = Rotation2d.fromDegrees(180);
//     private static double SpeedLimiterPercent = 0.5;
//     private static double MaxOmegaDegreesPerSecond = 90;

//     // State
//     private final Limelight limelight;
//     private final Drivetrain drivetrain;
//     private final PIDController xController = new PIDController(0.25, 0, 0); // m/s per m of error
//     private final PIDController yController = new PIDController(0.25, 0, 0); // m/s per m of error
//     private final SlewRateLimiter xRateLimiter = new SlewRateLimiter(1); // m/s per s
//     private final SlewRateLimiter yRateLimiter = new SlewRateLimiter(1); // m/s per s

//     public AllignMidCube(Limelight limelight, Drivetrain drivetrain) {
//         this.limelight = limelight;
//         this.drivetrain = drivetrain;

//         addRequirements(drivetrain);
//     }

//     @Override
//     public void initialize() {
//         limelight.setDesiredPipeline(LimelightPipeline.Fiducial);

//         xController.setTolerance(ToleranceMeters);
//         yController.setTolerance(ToleranceMeters);
//     }

//     @Override
//     public void execute() {
//         Pose3d pose = limelight.getCampose();
//         if (pose != null && limelight.isActivePipeline(LimelightPipeline.Fiducial)) {
//             // April tag visible and limelight pipeline is correct
//             double xMeters = pose.getX();
//             double xMetersPerSecond = xController.calculate(xMeters, TargetXMeters);
//             xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -MaxXMetersPerSecond, MaxXMetersPerSecond);
//             xMetersPerSecond = xRateLimiter.calculate(xMetersPerSecond);

//             double yMeters = pose.getY();
//             double yMetersPerSecond = yController.calculate(yMeters, TargetYMeters);
            
//         }
//         else {
//             // April tag not visible or limelight pipeline is incorrect
//             xRateLimiter.reset(0);
//             yRateLimiter.reset(0);
//         }
//     }

//     @Override
//     public boolean isFinished() {
//         return xController.atSetpoint() && yController.atSetpoint();
//     }

//     @Override
//     public void end(boolean interrupted) {
//         drivetrain.brakeTranslation();

//         limelight.printTargetData();
//     }
// }
