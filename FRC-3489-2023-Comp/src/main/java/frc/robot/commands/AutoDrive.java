package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.DrivetrainConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.NavX2;

public class AutoDrive extends CommandBase {
    private Pose2d relativePoseMeters;
    private double maxAxialSpeedMetersPerSecond;
    private double axialToleranceMeters;
    
    private SwerveDriveOdometry odometry;
    private PIDController xController = new PIDController(6, 0, 0);
    private PIDController yController = new PIDController(6, 0, 0);
    private SlewRateLimiter xRateLimiter = new SlewRateLimiter(10);
    private SlewRateLimiter yRateLimiter = new SlewRateLimiter(10);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    private double speedLimiter = 0;
    private double maxOmegaDegreesPerSecond = 0;

    public AutoDrive(double xMeters, double yMeters, double targetAngleDegrees, double maxAxialMetersPerSecond, double axialToleranceMeters, double maxOmegaDegreesPerSecond) {
        this.relativePoseMeters = new Pose2d(xMeters, yMeters, Rotation2d.fromDegrees(targetAngleDegrees));
        this.maxAxialSpeedMetersPerSecond = maxAxialMetersPerSecond;
        this.axialToleranceMeters = axialToleranceMeters;

        this.maxOmegaDegreesPerSecond = maxOmegaDegreesPerSecond;
    }

    @Override
    public void initialize() {
        Rotation2d rotation = NavX2.get().getRotation();
        odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, rotation, Drivetrain.get().getModulePositions(), new Pose2d(0, 0, rotation));
        
        xController.setTolerance(axialToleranceMeters);
        yController.setTolerance(axialToleranceMeters);

        Drivetrain.get().driveCommand.setAutomationXSupplier(() -> xMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationYSupplier(() -> yMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(() -> speedLimiter);
        Drivetrain.get().driveCommand.setAutomationMaxOmegaSupplier(() -> maxOmegaDegreesPerSecond);

        Drivetrain.get().driveCommand.setTargetAngle(relativePoseMeters.getRotation());
    }

    @Override
    public void execute() {
        if (!Drivetrain.get().driveCommand.isAutomationAllowed()) {
            cancel();
            return;
        }

        Rotation2d rotation = NavX2.get().getRotation();
        var modulePositions = Drivetrain.get().getModulePositions();
        odometry.update(rotation, modulePositions);

        Pose2d poseMeters = odometry.getPoseMeters();

        xMetersPerSecond = xController.calculate(poseMeters.getX(), relativePoseMeters.getY());
        xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -maxAxialSpeedMetersPerSecond, maxAxialSpeedMetersPerSecond);
        xMetersPerSecond = xRateLimiter.calculate(xMetersPerSecond);

        yMetersPerSecond = yController.calculate(poseMeters.getY(), -relativePoseMeters.getX());
        yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -maxAxialSpeedMetersPerSecond, maxAxialSpeedMetersPerSecond);
        yMetersPerSecond = yRateLimiter.calculate(yMetersPerSecond);
    }

    @Override
    public boolean isFinished() {
        return xController.atSetpoint() && yController.atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        if (interrupted) {
            Drivetrain.get().driveCommand.stopAutomation();
        }

        Drivetrain.get().driveCommand.setAutomationXSupplier(null);
        Drivetrain.get().driveCommand.setAutomationYSupplier(null);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(null);
        Drivetrain.get().driveCommand.setAutomationMaxOmegaSupplier(null);
    }
}
