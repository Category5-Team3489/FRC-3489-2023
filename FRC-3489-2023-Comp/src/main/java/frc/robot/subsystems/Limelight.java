package frc.robot.subsystems;

import java.util.function.LongConsumer;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.networktables.DoubleArraySubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import frc.robot.Constants.LimelightConstants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class Limelight extends Cat5Subsystem<Limelight> {
    //#region Singleton
    private static Limelight instance = new Limelight();

    public static Limelight get() {
        return instance;
    }
    //#endregion

    // Devices
    private final NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
    private final DoubleArraySubscriber onPoseUpdated = limelight.getDoubleArrayTopic("botpose").subscribe(null);
    private final NetworkTableEntry targetAreaEntry = limelight.getEntry("ta");
    private final NetworkTableEntry activePipelineEntry = limelight.getEntry("getpipe");
    private final NetworkTableEntry pipelineEntry = limelight.getEntry("pipeline");

    // Consumers
    private final LongConsumer activePipelineConsumer;

    private Limelight() {
        super((i) -> instance = i);

        // https://www.andymark.com/products/limelight-2-plus
        // Field of View: 59.6 x 49.7 degrees

        // botpose	Robot transform in field-space. Translation (X,Y,Z) Rotation(Roll,Pitch,Yaw), total latency (cl+tl)

        // use getpipe before reading data from pipeline, ex: was on april tag pipeline, need to center cone nodes, set cone node pipeline, then wait until getpipe == expected
        // then start centering, you can do pursue point until pipeline gives good data

        // TODO Can you check for noise somehow, just use SwerveDrivePoseEstimator?, Only accept new values when robot vel has been low for certain amt of time and ta high

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);
        
        var activePipelineEntry = layout.add("Active Pipeline", -1).getEntry();
        activePipelineConsumer = (activePipeline) -> activePipelineEntry.setInteger(activePipeline);
        //#endregion
    }

    @Override
    public void periodic() {
        int activePipeline = (int)activePipelineEntry.getInteger(-1);
        activePipelineConsumer.accept(activePipeline);
        if (activePipeline == LimelightConstants.FiducialPipelineIndex) {
            fiducialPeriodic();
        }
        else if (activePipeline == LimelightConstants.MidRetroreflectivePipelineIndex) {
            retroreflectivePeriodic();
        }
    }

    private void fiducialPeriodic() {
        double targetArea = targetAreaEntry.getDouble(0);
        if (targetArea < LimelightConstants.FiducialTargetAreaThresholdPercent) {
            return;
        }

        var poseUpdate = onPoseUpdated.getAtomic();
        if (poseUpdate.value != null) {
            double time = Timer.getFPGATimestamp();
            // double networkLatency = (poseUpdate.timestamp / 1000000.0) - time;
            double limelightLatency = poseUpdate.value[6] / 1000.0;
            // double timestamp = time - (networkLatency + limelightLatency);
            double timestamp = time - limelightLatency;
            Pose3d pose = new Pose3d(poseUpdate.value[0], poseUpdate.value[1], poseUpdate.value[2], new Rotation3d(poseUpdate.value[3], poseUpdate.value[4], poseUpdate.value[5]));
            PoseEstimator.get().onLimelightPoseUpdate(timestamp, pose);
        }
    }

    private void retroreflectivePeriodic() {

    }

    //#region Public
    public void setPipeline(Number pipeline) {
        pipelineEntry.setNumber(pipeline);
    }
    //#endregion
}
