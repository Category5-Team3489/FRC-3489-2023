package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.PoseEstimatorConstants;
import frc.robot.shuffleboard.Cat5Shuffleboard;

import static frc.robot.constants.DrivetrainConstants.*;

public class PoseEstimator extends SubsystemBase {
    private final Drivetrain drivetrain;
    private final NavX2 navx;
    private final Limelight limelight;

    // private final TimeInterpolatableBuffer<Pose2d> buffer = TimeInterpolatableBuffer.createBuffer(Cat5Math::lerp, PoseEstimatorConstants.HistorySizeSeconds);

    private SwerveDriveOdometry odometry;

    private Pose2d pose = new Pose2d();

    // ^^^ Use this to adjust for limelight pipeline latency and reckon from past point
    // for latency, add the amount of latency + how much limelight docs say, they say to add extra
    // tl + cl!!!!!!!!!!!!! https://docs.limelightvision.io/en/latest/networktables_api.html
    // any way to get timestamp of update to figure out how old it is?
    // yes, use ts from json https://docs.limelightvision.io/en/latest/json_dump.html
    // maybe just dont worry about network latency

    public PoseEstimator(Drivetrain drivetrain, NavX2 navx, Limelight limelight) {
        register();

        this.drivetrain = drivetrain;
        this.navx = navx;
        this.limelight = limelight;

        new Trigger(() -> Timer.getFPGATimestamp() >= PoseEstimatorConstants.TimeUntilReadySeconds)
            .and(() -> odometry == null)
            .and(navx.isCalibrated)
            .onTrue(Commands.runOnce(() -> {
                odometry = new SwerveDriveOdometry(Kinematics, navx.getRotation(), drivetrain.getSwerveModulePositions(), limelight.getPose());
            }));

        ShuffleboardLayout mainLayout = Cat5Shuffleboard.createMainLayout("Pose Estimator")
            .withSize(2, 2);
        
        mainLayout.addDouble("X", () -> pose.getX());
        mainLayout.addDouble("Y", () -> pose.getY());
        mainLayout.addDouble("Angle", () -> pose.getRotation().getDegrees());
    }

    @Override
    public void periodic() {
        if (odometry == null) {
            return;
        }

        pose = odometry.update(navx.getRotation(), drivetrain.getSwerveModulePositions());
    }
}
