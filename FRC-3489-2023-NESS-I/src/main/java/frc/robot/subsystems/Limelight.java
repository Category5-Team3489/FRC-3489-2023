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
import frc.robot.Cat5Utils;
import frc.robot.Constants;
import frc.robot.Constants.LimelightConstants;
import frc.robot.enums.LimelightPipeline;
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
    
    private final NetworkTableEntry getpipeEntry = limelight.getEntry("getpipe");
    private final NetworkTableEntry pipelineEntry = limelight.getEntry("pipeline");

    private final NetworkTableEntry tagIdEntry = limelight.getEntry("tid");
    private final NetworkTableEntry targetXEntry = limelight.getEntry("tx");
    private final NetworkTableEntry targetYEntry = limelight.getEntry("ty");
    private final NetworkTableEntry targetAreaEntry = limelight.getEntry("ta");

    // State
    private Timer activePipelineTimer = new Timer();
    private LimelightPipeline desiredPipeline = LimelightConstants.DefaultPipeline;
    private long activePipeline = -1;

    private Limelight() {
        super(i -> instance = i);

        activePipelineTimer.restart();

        //#region Shuffleboard
        if (Constants.IsDebugShuffleboardEnabled) {
            var subsystemLayout = getLayout(Cat5ShuffleboardTab.Limelight, BuiltInLayouts.kList)
                .withSize(2, 1);

            subsystemLayout.addBoolean("Is Campose Valid", () -> isCamposeValid());
            subsystemLayout.addString("Campose", () -> {
                var campose = getCampose();
                if (campose != null) {
                    return campose.toString();
                }
                return "null";
            });
            
            subsystemLayout.addDouble("Active Pipeline Timer", () -> activePipelineTimer.get());
            subsystemLayout.addString("Desired Pipeline", () -> desiredPipeline.toString());
            subsystemLayout.addInteger("Active Pipeline", () -> activePipeline);

            subsystemLayout.addInteger("Tag Id", () -> getTagId());
            subsystemLayout.addDouble("Target X", () -> getTargetX());
            subsystemLayout.addDouble("Target Y", () -> getTargetY());
            subsystemLayout.addDouble("Target Area", () -> getTargetArea());
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

            Cat5Utils.time();
            System.out.println("Limelight pipeline updated: " + activePipeline);
        }

        if (!isActivePipeline(desiredPipeline)) {
            pipelineEntry.setNumber(desiredPipeline.getIndex());
        }
    }

    //#region Public
    public boolean isCamposeValid() {
        return isActivePipeline(LimelightPipeline.Fiducial) &&
            activePipelineTimer.get() > LimelightConstants.CamposeValidActivePipelineSeconds &&
            getTargetArea() > LimelightConstants.CamposeValidTargetArea &&
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

    public long getActivePipeline() {
        return activePipeline;
    }
    public boolean isActivePipeline(LimelightPipeline pipeline) {
        return activePipeline == pipeline.getIndex();
    }
    public void setDesiredPipeline(LimelightPipeline pipeline) {
        desiredPipeline = pipeline;
    }

    public long getTagId() {
        return tagIdEntry.getInteger(-1);
    }
    public double getTargetX() {
        return targetXEntry.getDouble(Double.NaN);
    }
    public double getTargetY() {
        return targetYEntry.getDouble(Double.NaN);
    }
    public double getTargetArea() {
        return targetAreaEntry.getDouble(Double.NaN);
    }
    //#endregion
}