package frc.robot.commands.drivetrain;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DrivetrainConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.NavX2;

public class DriveRelativeMeters extends CommandBase {
    // State
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
    public DriveRelativeMeters(double xMeters, double yMeters, double targetHeadingDegrees, double maxAxialSpeedMetersPerSecond, double axialToleranceMeters, double startingHeadingDegrees) {
        addRequirements(Drivetrain.get());

        this.xMeters = yMeters;
        this.yMeters = -xMeters;
        this.targetHeadingDegrees = targetHeadingDegrees;
        this.maxAxialSpeedMetersPerSecond = maxAxialSpeedMetersPerSecond;
        this.axialToleranceMeters = axialToleranceMeters;
        this.startingHeadingDegrees = startingHeadingDegrees;
    }

    @Override
    public void initialize() {
        odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, NavX2.get().getRotation(), Drivetrain.get().getModulePositions(), new Pose2d(0, 0, Rotation2d.fromDegrees(startingHeadingDegrees)));
    
        xController.setTolerance(axialToleranceMeters);
        yController.setTolerance(axialToleranceMeters);
    }

    @Override
    public void execute() {
        Pose2d poseMeters = odometry.update(NavX2.get().getRotation(), Drivetrain.get().getModulePositions());

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
