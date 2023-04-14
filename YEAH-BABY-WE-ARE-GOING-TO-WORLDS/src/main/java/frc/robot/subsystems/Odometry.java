package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;

public class Odometry extends Cat5Subsystem {
    // State
    private final Drivetrain drivetrain;
    private final NavX2 navx;
    private SwerveDriveOdometry odometry = null;
    private Pose2d pose = new Pose2d();
    // private TimeInterpolatableBuffer<Pose2d> poseBuffer = TimeInterpolatableBuffer.createBuffer(Cat5::lerpUnclamped, 4);

    public Odometry(RobotContainer robotContainer, Drivetrain drivetrain, NavX2 navx) {
        super(robotContainer);
        this.drivetrain = drivetrain;
        this.navx = navx;

        odometry = new SwerveDriveOdometry(Drivetrain.Kinematics, Rotation2d.fromDegrees(0), drivetrain.getModulePositions());
    
        if (Constants.IsDebugShuffleboardEnabled) {
            var layout = robotContainer.layouts.get(Cat5ShuffleboardLayout.Debug_Odometry);
            layout.addDouble("X (m)", () -> pose.getX());
            layout.addDouble("Y (m)", () -> pose.getY());
            layout.addDouble("Theta (m)", () -> pose.getRotation().getDegrees());
        }

        // TODO Reset odometry on gyro changes, offset and heading
    }

    @Override
    public void periodic() {
        pose = odometry.update(navx.getRotation(), drivetrain.getModulePositions());
    }
}
