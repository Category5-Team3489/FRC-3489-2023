package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Odometry;

public class DriveMeters extends CommandBase {
    // Constants
    private static final double ProportionalGain100PercentPerMeter = 1.0;

    // State
    private final Drivetrain drivetrain;
    private final Odometry odometry;
    private final double xMeters;
    private final double yMeters;
    private final double targetHeadingDegrees;
    private final double metersPerSecond;
    private final double toleranceMeters;
    private final double degreesPerSecond;
    private double errorMeters;
    
    public DriveMeters(Drivetrain drivetrain, Odometry odometry, double xMeters, double yMeters, double targetHeadingDegrees, double metersPerSecond, double toleranceMeters, double degreesPerSecond) {
        this.drivetrain = drivetrain;
        this.odometry = odometry;
        this.xMeters = yMeters;
        this.yMeters = -xMeters;
        this.targetHeadingDegrees = targetHeadingDegrees;
        this.metersPerSecond = metersPerSecond;
        this.toleranceMeters = toleranceMeters;
        this.degreesPerSecond = degreesPerSecond;
        
        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        Pose2d poseMeters = odometry.getPoseMeters();
        
        double xErrorMeters = xMeters - poseMeters.getX();
        double yErrorMeters = yMeters - poseMeters.getY();
        errorMeters = Math.hypot(xErrorMeters, yErrorMeters);

        // Don't divide by zero
        if (errorMeters < toleranceMeters) {
            drivetrain.brakeTranslation();
            return;
        }

        double controllerPercent = errorMeters * ProportionalGain100PercentPerMeter;
        controllerPercent = MathUtil.clamp(controllerPercent, 0, 1.0);

        double xMetersPerSecond = (xErrorMeters / errorMeters) * metersPerSecond * controllerPercent;
        double yMetersPerSecond = (yErrorMeters / errorMeters) * metersPerSecond * controllerPercent;

        drivetrain.driveFieldRelative(xMetersPerSecond, yMetersPerSecond, 1.0, Rotation2d.fromDegrees(targetHeadingDegrees), degreesPerSecond);
    }

    @Override
    public boolean isFinished() {
        return errorMeters < toleranceMeters;
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.brakeTranslation();
    }
}
