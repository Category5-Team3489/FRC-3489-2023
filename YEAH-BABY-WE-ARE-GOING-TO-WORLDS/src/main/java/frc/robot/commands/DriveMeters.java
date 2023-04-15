package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Odometry;

public class DriveMeters extends CommandBase {
    // State
    private final Drivetrain drivetrain;
    private final Odometry odometry;
    private double xMeters;
    private double yMeters;
    private final double targetHeadingDegrees;
    private final double metersPerSecond;
    private final double toleranceMeters;
    private final double degreesPerSecond;
    private final PIDController xController = new PIDController(6, 0, 0);
    private final PIDController yController = new PIDController(6, 0, 0);
    
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
    public void initialize() {
        Pose2d poseMeters = odometry.getPose();
        xMeters += poseMeters.getX();
        yMeters += poseMeters.getY();

        xController.setTolerance(toleranceMeters);
        yController.setTolerance(toleranceMeters);
    }

    @Override
    public void execute() {
        Pose2d poseMeters = odometry.getPose();

        double xMetersPerSecond = xController.calculate(poseMeters.getX(), xMeters);
        xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -metersPerSecond, metersPerSecond);

        double yMetersPerSecond = yController.calculate(poseMeters.getY(), yMeters);
        yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -metersPerSecond, metersPerSecond);

        drivetrain.driveFieldRelative(xMetersPerSecond, yMetersPerSecond, 1.0, Rotation2d.fromDegrees(targetHeadingDegrees), degreesPerSecond);
    }

    @Override
    public boolean isFinished() {
        return xController.atSetpoint() && yController.atSetpoint();
    }
}
