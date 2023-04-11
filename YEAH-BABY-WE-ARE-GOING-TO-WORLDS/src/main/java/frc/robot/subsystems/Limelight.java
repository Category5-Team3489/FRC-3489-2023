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
import frc.robot.Cat5;
import frc.robot.RobotContainer;
import frc.robot.enums.LimelightPipeline;

public class Limelight extends Cat5Subsystem {
    // Constants
    private final static LimelightPipeline DefaultPipeline = LimelightPipeline.Fiducial;
    private static final double CamposeValidActivePipelineSeconds = 0.5;
    private static final double CamposeValidTargetArea = 0.005;
    private static final double CamposeValidAverageDriveVelocityLimitMetersPerSecond = 0.25;

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
    private LimelightPipeline desiredPipeline = DefaultPipeline;
    private long activePipeline = -1;
    
    public Limelight(RobotContainer robotContainer) {
        super(robotContainer);

        activePipelineTimer.restart();

        // TODO robotContainer.layouts.vitals.addBoolean("Limelight Updating", () -> isActivePipeline(desiredPipeline));
    }
    
    @Override
    public void periodic() {
        if (DriverStation.isDisabled()) {
            setDesiredPipeline(DefaultPipeline);
        }

        long getpipe = getpipeEntry.getInteger(-1);
        if (getpipe != activePipeline) {
            activePipeline = getpipe;
            activePipelineTimer.restart();

            Cat5.print("Limelight pipeline updated: " + activePipeline);
        }

        if (!isActivePipeline(desiredPipeline)) {
            pipelineEntry.setNumber(desiredPipeline.getIndex());
        }
    }

    public boolean isCamposeValid() {
        return isActivePipeline(LimelightPipeline.Fiducial) &&
            activePipelineTimer.get() > CamposeValidActivePipelineSeconds &&
            getTargetArea() > CamposeValidTargetArea &&
            robotContainer.getAverageDriveVelocityMetersPerSecond() < CamposeValidAverageDriveVelocityLimitMetersPerSecond;
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
}
