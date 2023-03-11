package frc.robot.subsystems;

import java.util.ArrayList;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import frc.robot.Constants.DrivetrainConstants;

public class PoseEstimator extends Cat5Subsystem<PoseEstimator> {
    //#region Singleton
    private static PoseEstimator instance = new PoseEstimator();

    public static PoseEstimator get() {
        return instance;
    }
    //#endregion

    // State
    // private SwerveDriveOdometry odometry = null;
    // private TimeInterpolatableBuffer<Pose2d> poseBuffer = TimeInterpolatableBuffer.createBuffer(Cat5Utils::lerpUnclamped, 4);
    private ArrayList<SwerveDriveOdometry> odometries = new ArrayList<SwerveDriveOdometry>();

    private PoseEstimator() {
        super((i) -> instance = i);

        //#region Shuffleboard
        // var subsystemLayout = getLayout(Cat5ShuffleboardTab.PoseEstimator, BuiltInLayouts.kList)
        //     .withSize(2, 1);

        // subsystemLayout.addDouble("X (m)", () -> getXMeters());
        // subsystemLayout.addDouble("Y (m)", () -> getYMeters());
        // subsystemLayout.addDouble("Angle (deg)", () -> getDegrees());
        // subsystemLayout.addBoolean("Is Init", () -> odometry != null);
        //#endregion
    }

    @Override
    public void periodic() {
        // if (odometry == null &&
        //     !NavX2.get().isCalibrating() &&
        //     Limelight.get().isCamposeValid() &&
        //     (DriverStation.isAutonomousEnabled() || DriverStation.isTeleopEnabled())) {
        //     Pose3d campose = Limelight.get().getCampose();
        //     if (campose != null) {
        //         boolean isValid = false;
        //         Alliance alliance = DriverStation.getAlliance();
        //         long tagId = Limelight.get().getTagId();
        //         if (alliance == Alliance.Blue) {
        //             if (tagId == 6 || tagId == 7 || tagId == 8) {
        //                 isValid = true;
        //             }
        //         }
        //         else if (alliance == Alliance.Red) {
        //             if (tagId == 1 || tagId == 2 || tagId == 3) {
        //                 isValid = true;
        //             }
        //         }
        //         if (isValid) {
        //             double yaw = campose.getRotation().getZ();
        //             NavX2.get().setOffset(Rotation2d.fromRadians(yaw));
        //             Rotation2d rotation = NavX2.get().getRotation();
        //             odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, rotation, Drivetrain.get().getModulePositions(), new Pose2d(0, 0, rotation));
        //         }
        //     }
        // }

        Rotation2d rotation = NavX2.get().getRotation();
        var modulePositions = Drivetrain.get().getModulePositions();

        // if (odometry != null) {
        //     Pose2d poseMeters = odometry.update(rotation, modulePositions);
        //     poseBuffer.addSample(Timer.getFPGATimestamp(), poseMeters);
        // }

        for (var odometry : odometries) {
            odometry.update(rotation, modulePositions);
        }
    }

    //#region Pose
    // private double getXMeters() {
    //     if (odometry == null) {
    //         return Double.NaN;
    //     }

    //     Pose2d poseMeters = odometry.getPoseMeters();
    //     if (poseMeters == null) {
    //         return Double.NaN;
    //     }
    //     return poseMeters.getX();
    // }
    // private double getYMeters() {
    //     if (odometry == null) {
    //         return Double.NaN;
    //     }

    //     Pose2d poseMeters = odometry.getPoseMeters();
    //     if (poseMeters == null) {
    //         return Double.NaN;
    //     }
    //     return poseMeters.getY();
    // }
    // private double getDegrees() {
    //     if (odometry == null) {
    //         return Double.NaN;
    //     }

    //     Pose2d poseMeters = odometry.getPoseMeters();
    //     if (poseMeters == null) {
    //         return Double.NaN;
    //     }
    //     return poseMeters.getRotation().getDegrees();
    // }
    //#endregion

    //#region Public
    public SwerveDriveOdometry createOdometry() {
        Rotation2d rotation = NavX2.get().getRotation();
        var odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, rotation, Drivetrain.get().getModulePositions(), new Pose2d(0, 0, rotation));
        odometries.add(odometry);
        return odometry;
    }
    public void deleteOdometry(SwerveDriveOdometry odometry) {
        odometries.remove(odometry);
    }

    // public Optional<Pose2d> getPoseWithLatency(double latencySeconds) {
    //     if (odometry == null) {
    //         return Optional.empty();
    //     }
    //     return poseBuffer.getSample(Timer.getFPGATimestamp() - latencySeconds);
    // }

    // public void notifyNavxJump(Rotation2d rotation) {
    //     var modulePositions = Drivetrain.get().getModulePositions();
    //     odometry = new SwerveDriveOdometry(DrivetrainConstants.Kinematics, rotation, modulePositions, new Pose2d(0, 0, rotation));
    //     poseBuffer.clear();
    // }
    //#endregion
}
