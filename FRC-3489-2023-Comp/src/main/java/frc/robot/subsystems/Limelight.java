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
import frc.robot.Constants.OperatorConstants;
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

    private final DoubleArraySubscriber camerapose_targetspaceSubscriber = limelight.getDoubleArrayTopic("camerapose_targetspace").subscribe(new double[] {});

    // private final NetworkTableEntry pipelineLatencyEntry = limelight.getEntry("tl");
    // private final NetworkTableEntry captureLatencyEntry = limelight.getEntry("cl");

    private final NetworkTableEntry tagIdEntry = limelight.getEntry("tid");
    
    private final NetworkTableEntry getpipeEntry = limelight.getEntry("getpipe");
    private final NetworkTableEntry pipelineEntry = limelight.getEntry("pipeline");
    private final NetworkTableEntry targetXEntry = limelight.getEntry("tx");
    private final NetworkTableEntry targetYEntry = limelight.getEntry("ty");
    private final NetworkTableEntry targetAreaEntry = limelight.getEntry("ta");

    // State
    private Timer activePipelineTimer = new Timer();

    private long desiredPipeline = LimelightConstants.DefaultPipeline;
    private long activePipeline = -1;
    private double targetX = Double.NaN;
    private double targetY = Double.NaN;
    private double targetArea = 0;

    private Limelight() {
        super((i) -> instance = i);

        // https://www.andymark.com/products/limelight-2-plus
        // Field of View: 59.6 x 49.7 degrees
        // botpose	Robot transform in field-space. Translation (X,Y,Z) Rotation(Roll,Pitch,Yaw), total latency (cl+tl)
        // use getpipe before reading data from pipeline, ex: was on april tag pipeline, need to center cone nodes, set cone node pipeline, then wait until getpipe == expected
        // then start centering, you can do pursue point until pipeline gives good data
        // Can you check for noise somehow, just use SwerveDrivePoseEstimator?, Only accept new values when robot vel has been low for certain amt of time and ta high

        activePipelineTimer.restart();

        //#region Shuffleboard
        if (OperatorConstants.DebugShuffleboard) {
            var subsystemLayout = getLayout(Cat5ShuffleboardTab.Limelight, BuiltInLayouts.kList)
                .withSize(2, 1);
            
            subsystemLayout.addDouble("Active Pipeline Timer", () -> activePipelineTimer.get());
            subsystemLayout.addInteger("Desired Pipeline", () -> desiredPipeline);
            subsystemLayout.addInteger("Active Pipeline", () -> activePipeline);
            subsystemLayout.addDouble("Target X", () -> targetX);
            subsystemLayout.addDouble("Target Y", () -> targetY);
            subsystemLayout.addDouble("Target Area", () -> targetArea);
            subsystemLayout.addBoolean("Is Campose Valid", () -> isCamposeValid());
            
            subsystemLayout.addString("Campose", () -> getCamposeString());
            subsystemLayout.addInteger("Tag Id", () -> getTagId());
            // subsystemLayout.addDouble("Latency Seconds", () -> getLatencySeconds());
        }
        //#endregion
    }

    @Override
    public void periodic() {
        if (DriverStation.isDisabled()) {
            setDesiredPipeline(LimelightConstants.DefaultPipeline);
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
    }

    private void setPipeline(long pipeline) {
        if (isActivePipeline(pipeline)) {
            return;
        }

        pipelineEntry.setNumber(pipeline);
    }

    //#region Shuffleboard
    private String getCamposeString() {
        var campose = getCampose();
        if (campose != null) {
            return campose.toString();
        }
        return "null";
    }
    //#endregion

    //#region Public
    public boolean isCamposeValid() {
        return activePipeline == LimelightConstants.FiducialPipeline &&
            activePipelineTimer.get() > LimelightConstants.CamposeValidActivePipelineSeconds &&
            targetArea > LimelightConstants.BotposeValidTargetArea &&
            Drivetrain.get().getAverageDriveVelocityMetersPerSecond() < LimelightConstants.CamposeValidAverageDriveVelocityLimitMetersPerSecond;
    }

    public Pose3d getCampose() {
        double[] campose = camerapose_targetspaceSubscriber.get();
        if (campose.length != 0) {
            Translation3d translationMeters = new Translation3d(campose[0], campose[1], campose[2]);
            Rotation3d rotation = new Rotation3d(campose[3], campose[4], campose[5]);
            return new Pose3d(translationMeters, rotation);
        }
        else {
            return null;
        }
    }

    // public double getLatencySeconds() {
    //     return (pipelineLatencyEntry.getDouble(0) / 1000.0) + (captureLatencyEntry.getDouble(0) / 1000.0);
    // }

    public long getTagId() {
        return tagIdEntry.getInteger(0);
    }

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
    //#endregion
}
