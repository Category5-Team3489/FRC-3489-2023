package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;

public class Odometry extends Cat5Subsystem {
    // State
    private final NavX2 navx;
    private final Drivetrain drivetrain;
    private SwerveDriveOdometry odometry;
    private Pose2d pose = new Pose2d();

    public Odometry(RobotContainer robotContainer, NavX2 navx, Drivetrain drivetrain) {
        super(robotContainer);
        this.navx = navx;
        this.drivetrain = drivetrain;

        Rotation2d gyroAngle = navx.getRotation();
        odometry = new SwerveDriveOdometry(Drivetrain.Kinematics, gyroAngle, drivetrain.getModulePositions(), new Pose2d(0, 0, gyroAngle));
    
        if (Constants.IsShuffleboardDebugEnabled) {
            var layout = robotContainer.layouts.get(Cat5ShuffleboardLayout.Debug_Odometry);
            layout.addDouble("X (m)", () -> pose.getX());
            layout.addDouble("Y (m)", () -> pose.getY());
            layout.addDouble("Theta (m)", () -> pose.getRotation().getDegrees());
        }
    }

    @Override
    public void periodic() {
        pose = odometry.update(navx.getRotation(), drivetrain.getModulePositions());
    }

    public void reset() {
        resetWithPosition(0, 0);
    }

    public void resetWithPosition(double xMeters, double yMeters) {
        Rotation2d gyroAngle = navx.getRotation();
        odometry = new SwerveDriveOdometry(Drivetrain.Kinematics, gyroAngle, drivetrain.getModulePositions(), new Pose2d(xMeters, yMeters, gyroAngle));
    }

    public Pose2d getPoseMeters() {
        return pose;
    }
}
