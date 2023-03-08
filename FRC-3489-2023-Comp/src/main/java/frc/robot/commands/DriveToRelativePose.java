package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.PoseEstimator;

public class DriveToRelativePose extends CommandBase {
    private Pose2d relativePoseMeters;
    private double maxAxialSpeedMetersPerSecond;
    
    private SwerveDriveOdometry odometry;
    private PIDController xController = new PIDController(6, 0, 0);
    private PIDController yController = new PIDController(6, 0, 0);
    private SlewRateLimiter xLimiter = new SlewRateLimiter(10);
    private SlewRateLimiter yLimiter = new SlewRateLimiter(10);
    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;

    public DriveToRelativePose(Pose2d relativePoseMeters, double maxAxialSpeedMetersPerSecond) {
        this.relativePoseMeters = relativePoseMeters;
        this.maxAxialSpeedMetersPerSecond = maxAxialSpeedMetersPerSecond;
    }

    @Override
    public void initialize() {
        odometry = PoseEstimator.get().createOdometry(new Pose2d());
        
        xController.setTolerance(0.1);
        yController.setTolerance(0.1);

        Drivetrain.get().driveCommand.setAutomationXSupplier(() -> xMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationYSupplier(() -> yMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(() -> 1.0);

        Drivetrain.get().driveCommand.setTargetAngle(relativePoseMeters.getRotation());
    }

    @Override
    public void execute() {
        if (!Drivetrain.get().driveCommand.isAutomationAllowed()) {
            cancel();
            return;
        }

        Pose2d poseMeters = odometry.getPoseMeters();

        xMetersPerSecond = xController.calculate(poseMeters.getX(), relativePoseMeters.getY());
        xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -maxAxialSpeedMetersPerSecond, maxAxialSpeedMetersPerSecond);
        xMetersPerSecond = xLimiter.calculate(xMetersPerSecond);

        yMetersPerSecond = yController.calculate(poseMeters.getY(), -relativePoseMeters.getX());
        yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -maxAxialSpeedMetersPerSecond, maxAxialSpeedMetersPerSecond);
        yMetersPerSecond = yLimiter.calculate(yMetersPerSecond);

        System.out.println(odometry.getPoseMeters());
    }

    @Override
    public boolean isFinished() {
        return xController.atSetpoint() && yController.atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println(odometry.getPoseMeters());

        PoseEstimator.get().deleteOdometry(odometry);

        xMetersPerSecond = 0;
        yMetersPerSecond = 0;
    }
}
