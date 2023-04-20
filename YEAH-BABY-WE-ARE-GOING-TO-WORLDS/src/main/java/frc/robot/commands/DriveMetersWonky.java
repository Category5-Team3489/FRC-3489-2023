package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.NavX2;

public class DriveMetersWonky extends CommandBase {
    // State
    private final NavX2 navx;
    private final Drivetrain drivetrain;
    private final double xMeters;
    private final double yMeters;
    private final double targetHeadingDegrees;
    private final double maxAxialSpeedMetersPerSecond;
    private final double axialToleranceMeters;
    private final double startingHeadingDegrees;

    private SwerveDriveOdometry odometry;
    private PIDController xController = new PIDController(6, 0, 0);
    private PIDController yController = new PIDController(6, 0, 0);

    // xMeters: positive forward, yMeters: positive left
    public DriveMetersWonky(NavX2 navx, Drivetrain drivetrain, double xMeters, double yMeters, double targetHeadingDegrees, double maxAxialSpeedMetersPerSecond, double axialToleranceMeters, double startingHeadingDegrees) {
        this.navx = navx;
        this.drivetrain = drivetrain;
        this.xMeters = yMeters;
        this.yMeters = -xMeters;
        this.targetHeadingDegrees = targetHeadingDegrees;
        this.maxAxialSpeedMetersPerSecond = maxAxialSpeedMetersPerSecond;
        this.axialToleranceMeters = axialToleranceMeters;
        this.startingHeadingDegrees = startingHeadingDegrees;

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        odometry = new SwerveDriveOdometry(Drivetrain.Kinematics, navx.getRotation(), drivetrain.getModulePositions(), new Pose2d(0, 0, Rotation2d.fromDegrees(startingHeadingDegrees)));
    
        xController.setTolerance(axialToleranceMeters);
        yController.setTolerance(axialToleranceMeters);
    }

    @Override
    public void execute() {
        Pose2d poseMeters = odometry.update(navx.getRotation(), drivetrain.getModulePositions());

        double xMetersPerSecond = xController.calculate(poseMeters.getX(), xMeters);
        xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -maxAxialSpeedMetersPerSecond, maxAxialSpeedMetersPerSecond);

        double yMetersPerSecond = yController.calculate(poseMeters.getY(), yMeters);
        yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -maxAxialSpeedMetersPerSecond, maxAxialSpeedMetersPerSecond);
    
        drivetrain.driveFieldRelative(xMetersPerSecond, yMetersPerSecond, 1.0, Rotation2d.fromDegrees(targetHeadingDegrees), null);
    }

    @Override
    public boolean isFinished() {
        return xController.atSetpoint() && yController.atSetpoint();
    }
}
