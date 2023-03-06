package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.DoubleArraySubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
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

    private final DoubleArraySubscriber botposeSubscriber = limelight.getDoubleArrayTopic("botpose").subscribe(new double[] {});
    
    private final NetworkTableEntry getpipeEntry = limelight.getEntry("getpipe");
    private final NetworkTableEntry pipelineEntry = limelight.getEntry("pipeline");
    private final NetworkTableEntry targetXEntry = limelight.getEntry("tx");
    private final NetworkTableEntry targetYEntry = limelight.getEntry("ty");
    private final NetworkTableEntry targetAreaEntry = limelight.getEntry("ta");

    // State
    private Timer activePipelineTimer = new Timer();
    private Timer botposeTimer = new Timer();

    private long desiredPipeline = LimelightConstants.DefaultPipelineIndex;
    private long activePipeline = -1;
    private double targetX = Double.NaN;
    private double targetY = Double.NaN;
    private double targetArea = 0;
    private long lastBotposeTimestamp = 0;

    private Limelight() {
        super((i) -> instance = i);

        // https://www.andymark.com/products/limelight-2-plus
        // Field of View: 59.6 x 49.7 degrees
        // botpose	Robot transform in field-space. Translation (X,Y,Z) Rotation(Roll,Pitch,Yaw), total latency (cl+tl)
        // use getpipe before reading data from pipeline, ex: was on april tag pipeline, need to center cone nodes, set cone node pipeline, then wait until getpipe == expected
        // then start centering, you can do pursue point until pipeline gives good data
        // Can you check for noise somehow, just use SwerveDrivePoseEstimator?, Only accept new values when robot vel has been low for certain amt of time and ta high

        activePipelineTimer.restart();
        botposeTimer.restart();

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Limelight, BuiltInLayouts.kList)
            .withSize(2, 1);
        
        layout.addDouble("Active Pipeline Timer", () -> activePipelineTimer.get());
        layout.addDouble("Botpose Timer", () -> botposeTimer.get());
        layout.addInteger("Desired Pipeline", () -> desiredPipeline);
        layout.addInteger("Active Pipeline", () -> activePipeline);
        layout.addDouble("Target X", () -> targetX);
        layout.addDouble("Target Y", () -> targetY);
        layout.addDouble("Target Area", () -> targetArea);
        //#endregion
    }

    @Override
    public void periodic() {
        if (DriverStation.isDisabled()) {
            setDesiredPipeline(LimelightConstants.DefaultPipelineIndex);
        }

        long getpipe = getpipeEntry.getInteger(-1);
        if (getpipe != activePipeline) {
            activePipeline = getpipe;
            activePipelineTimer.restart();
        }
        targetX = targetXEntry.getDouble(Double.NaN);
        targetY = targetYEntry.getDouble(Double.NaN);
        targetArea = targetAreaEntry.getDouble(0);

        if (desiredPipeline != activePipeline) {
            setPipeline(desiredPipeline);
        }

        if (isBotposeValid()) {
            var botpose = botposeSubscriber.getAtomic();
            if (botpose.timestamp != lastBotposeTimestamp && botpose.value.length == 7) {
                lastBotposeTimestamp = botpose.timestamp;
                botposeTimer.restart();

                Translation3d translation = new Translation3d(botpose.value[0], botpose.value[1], botpose.value[2]);
                Rotation3d rotation = new Rotation3d(botpose.value[3], botpose.value[4], botpose.value[5]);
                Pose3d botposeMeters = new Pose3d(translation, rotation);
                double latencySeconds = botpose.value[6] / 1000.0;
                PoseEstimator.get().notifyLimelightBotpose(botposeMeters, latencySeconds);
            }
        }
    }

    private boolean isBotposeValid() {
        return activePipeline == LimelightConstants.FiducialPipelineIndex &&
            activePipelineTimer.get() > LimelightConstants.BotposeValidActivePipelineSeconds &&
            targetArea > LimelightConstants.BotposeValidTargetArea &&
            Drivetrain.get().getAverageDriveVelocityMetersPerSecond() < LimelightConstants.BotposeValidAverageDriveVelocityLimitMetersPerSecond;
    }

    //#region Public
    public long getActivePipeline() {
        return activePipeline;
    }
    public double getTargetX() {
        return targetX;
    }
    public double getTargetY() {
        return targetY;
    }
    public double getTargetArea() {
        return targetArea;
    }

    public void setDesiredPipeline(long pipeline) {
        desiredPipeline = pipeline;
    }
    public boolean isActivePipeline(long pipeline) {
        return activePipeline == pipeline;
    }
    private void setPipeline(long pipeline) {
        if (isActivePipeline(pipeline)) {
            return;
        }

        pipelineEntry.setNumber(pipeline);
    }
    //#endregion
}
