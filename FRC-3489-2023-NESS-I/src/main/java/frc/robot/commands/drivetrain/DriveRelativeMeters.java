package frc.robot.commands.drivetrain;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DrivetrainConstants;
import frc.robot.subsystems.Drivetrain;

public class DriveRelativeMeters extends CommandBase {
    // State
    private final double xMeters;
    private final double yMeters;
    private final double targetHeadingDegrees;
    private final double maxAxialSpeedMetersPerSecond;
    private final double axialToleranceMeters;

    private SwerveDriveOdometry odometry;
    private PIDController xController = new PIDController(6, 0, 0);
    private PIDController yController = new PIDController(6, 0, 0);

    public DriveRelativeMeters(double xMeters, double yMeters, double targetHeadingDegrees, double maxAxialSpeedMetersPerSecond, double axialToleranceMeters) {
        addRequirements(Drivetrain.get());

        this.xMeters = xMeters;
        this.yMeters = yMeters;
        this.targetHeadingDegrees = targetHeadingDegrees;
        this.maxAxialSpeedMetersPerSecond = maxAxialSpeedMetersPerSecond;
        this.axialToleranceMeters = axialToleranceMeters;
    }

    @Override
    public void initialize() {
        odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, new Rotation2d(), Drivetrain.get().getModulePositions());
    
        xController.setTolerance(axialToleranceMeters);
        yController.setTolerance(axialToleranceMeters);
    }

    @Override
    public void execute() {
        Pose2d poseMeters = odometry.update(new Rotation2d(), Drivetrain.get().getModulePositions());

        double xMetersPerSecond = xController.calculate(poseMeters.getX(), xMeters);
        xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -maxAxialSpeedMetersPerSecond, maxAxialSpeedMetersPerSecond);

        double yMetersPerSecond = yController.calculate(poseMeters.getY(), yMeters);
        yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -maxAxialSpeedMetersPerSecond, maxAxialSpeedMetersPerSecond);
    
        Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, 1.0, Rotation2d.fromDegrees(targetHeadingDegrees), 0, null);
    }

    @Override
    public boolean isFinished() {
        return xController.atSetpoint() && yController.atSetpoint();
    }
}
