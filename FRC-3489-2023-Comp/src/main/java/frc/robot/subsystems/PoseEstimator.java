package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import frc.robot.Constants.DrivetrainConstants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class PoseEstimator extends Cat5Subsystem<PoseEstimator> {
    //#region Singleton
    private static PoseEstimator instance = new PoseEstimator();

    public static PoseEstimator get() {
        return instance;
    }
    //#endregion

    // State
    private SwerveDriveOdometry odometry;
    private Pose2d pose = new Pose2d();

    private PoseEstimator() {
        super((i) -> instance = i);
        // Seconds since last vision update widget
        // April tag area, and tag count, current encoder-based robot velocity, can be used to determine trustworthiness of vision measurement
        // ^^^ Use this to adjust for limelight pipeline latency and reckon from past point
        // for latency, add the amount of latency + how much limelight docs say, they say to add extra
        // tl + cl!!!!!!!!!!!!! https://docs.limelightvision.io/en/latest/networktables_api.html
        // any way to get timestamp of update to figure out how old it is?
        // yes, use ts from json https://docs.limelightvision.io/en/latest/json_dump.html
        // maybe just dont worry about network latency
        // +Y = right
        // +X = forward

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.PoseEstimator, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.addDouble("X", () -> pose.getX());
        layout.addDouble("Y", () -> pose.getY());
        layout.addDouble("Angle", () -> pose.getRotation().getDegrees());
        //#endregion
    }

    @Override
    public void periodic() {
        if (DriverStation.isEnabled() && odometry == null) {
            odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, NavX2.get().getRotation(), Drivetrain.get().getSwerveModulePositions(), new Pose2d());
        }
        
        if (odometry != null) {
            pose = odometry.update(NavX2.get().getRotation(), Drivetrain.get().getSwerveModulePositions());
        }
    }

    //#region Public
    public Pose2d getPose() {
        return pose;
    }
    //#endregion
}
