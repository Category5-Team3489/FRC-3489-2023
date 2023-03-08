package frc.robot.subsystems;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.interpolation.TimeInterpolatableBuffer;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.Timer;

import frc.robot.Cat5Utils;
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
    private SwerveDriveOdometry odometry = null;
    private Pose2d poseMeters = null;
    private TimeInterpolatableBuffer<Pose2d> poseMetersBuffer = TimeInterpolatableBuffer.createBuffer(Cat5Utils::lerpUnclamped, 4);
    private ArrayList<SwerveDriveOdometry> odometries = new ArrayList<SwerveDriveOdometry>();

    private PoseEstimator() {
        super((i) -> instance = i);

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.PoseEstimator, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.addDouble("X (m)", () -> getX());
        layout.addDouble("Y (m)", () -> getY());
        layout.addDouble("Angle (deg)", () -> getDegrees());
        //#endregion
    }

    @Override
    public void periodic() {
        // TODO remove when using limelight botpose
        if (odometry == null && !NavX2.get().isCalibrating()) {
            odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, NavX2.get().getRotation(), Drivetrain.get().getModulePositions(), new Pose2d());
        }

        Rotation2d rotation = NavX2.get().getRotation();
        var modulePositions = Drivetrain.get().getModulePositions();

        if (odometry != null) {
            poseMeters = odometry.update(rotation, modulePositions);
            poseMetersBuffer.addSample(Timer.getFPGATimestamp(), poseMeters);
        }

        for (var odometry : odometries) {
            odometry.update(rotation, modulePositions);
        }
    }

    //#region Pose
    private double getX() {
        if (poseMeters == null) {
            return Double.NaN;
        }
        return poseMeters.getX();
    }
    private double getY() {
        if (poseMeters == null) {
            return Double.NaN;
        }
        return poseMeters.getY();
    }
    private double getDegrees() {
        if (poseMeters == null) {
            return Double.NaN;
        }
        return poseMeters.getRotation().getDegrees();
    }
    //#endregion

    //#region Public
    public SwerveDriveOdometry createOdometry(Pose2d initialPose) {
        var odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, NavX2.get().getRotation(), Drivetrain.get().getModulePositions(), initialPose);
        odometries.add(odometry);
        return odometry;
    }

    public void deleteOdometry(SwerveDriveOdometry odometry) {
        odometries.remove(odometry);
    }

    public Pose2d getPoseMeters() {
        return poseMeters;
    }

    public void notifyNavxZeroYaw(Rotation2d rotation) {
        var modulePositions = Drivetrain.get().getModulePositions();

        if (odometry != null) {
            odometry.resetPosition(rotation, modulePositions, poseMeters);
            poseMeters = odometry.getPoseMeters();
        }

        for (var odometry : odometries) {
            odometry.resetPosition(rotation, modulePositions, odometry.getPoseMeters());
        }
    }

    public void notifyLimelightBotpose(Pose3d botposeMeters, double latencySeconds) {
        if (odometry == null) {
            if (NavX2.get().isCalibrating()) {
                return;
            }

            odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, NavX2.get().getRotation(), Drivetrain.get().getModulePositions(), botposeMeters.toPose2d());
        }
        else {
            var latentPoseMeters = poseMetersBuffer.getSample(Timer.getFPGATimestamp() - latencySeconds);

            if (latentPoseMeters.isEmpty()) {
                odometry.resetPosition(NavX2.get().getRotation(), Drivetrain.get().getModulePositions(), botposeMeters.toPose2d());
            }
            else {
                var offsetMeters = poseMeters.minus(latentPoseMeters.get());
                odometry.resetPosition(NavX2.get().getRotation(), Drivetrain.get().getModulePositions(), botposeMeters.toPose2d().plus(offsetMeters));
            }

            poseMeters = odometry.getPoseMeters();
            poseMetersBuffer.addSample(Timer.getFPGATimestamp(), poseMeters);
        }
    }
    //#endregion
}
