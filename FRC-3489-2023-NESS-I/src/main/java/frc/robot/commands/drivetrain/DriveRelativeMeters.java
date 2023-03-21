// package frc.robot.commands.drivetrain;

// import edu.wpi.first.math.filter.SlewRateLimiter;
// import edu.wpi.first.wpilibj2.command.CommandBase;
// import frc.robot.subsystems.Drivetrain;

// public class DriveRelativeMeters extends CommandBase {
//     // State
//     private final double xMeters;
//     private final double yMeters;
//     private final double metersPerSecond;
//     private final double metersPerSecondPerSecond;
//     private final double headingDegrees;
//     private final SlewRateLimiter rateLimiter;
//     private final double[] startingEncoderPositions;

//     public DriveRelativeMeters(double xMeters, double yMeters, double metersPerSecond, double metersPerSecondPerSecond, double headingDegrees) {
//         addRequirements(Drivetrain.get());

//         this.xMeters = xMeters;
//         this.yMeters = yMeters;
//         this.metersPerSecond = metersPerSecond;
//         this.metersPerSecondPerSecond = metersPerSecondPerSecond;
//         this.headingDegrees = headingDegrees;

//         rateLimiter = new SlewRateLimiter(metersPerSecondPerSecond);
//     }

//     @Override
//     public void execute() {
        
//     }
// }
