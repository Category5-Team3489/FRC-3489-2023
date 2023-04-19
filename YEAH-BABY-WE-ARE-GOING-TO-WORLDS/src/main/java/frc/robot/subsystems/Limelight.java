package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.DoubleArraySubscriber;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Cat5;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;
import frc.robot.enums.LimelightPipeline;

public class Limelight extends Cat5Subsystem {
    // Constants
    private final static LimelightPipeline DefaultPipeline = LimelightPipeline.Camera;
    // private static final double CamposeValidActivePipelineSeconds = 0.5;
    // private static final double CamposeValidTargetArea = 0.005;
    // private static final double CamposeValidAverageDriveVelocityLimitMetersPerSecond = 0.25;

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

        GenericEntry responsiveEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Driver)
            .add("Limelight Responsive", false)
            .getEntry();
        BooleanLogEntry responsiveLogEntry = new BooleanLogEntry(robotContainer.dataLog, "/limelight/responsive");
        robotContainer.data.createDatapoint(() -> isActivePipeline(desiredPipeline))
            .withShuffleboard(data -> {
                responsiveEntry.setBoolean(data);
            }, 5)
            .withLog(data -> {
                responsiveLogEntry.append(data);
            }, 5);

        if (Constants.IsShuffleboardDebugEnabled) {
            {
                var layout = robotContainer.layouts.get(Cat5ShuffleboardLayout.Debug_Target_Data);
                layout.addInteger("Tag Id", () -> getTagId());
                layout.addDouble("Target X", () -> getTargetX());
                layout.addDouble("Target Y", () -> getTargetY());
                layout.addDouble("Target Area", () -> getTargetArea());
            }
            {
                var layout = robotContainer.layouts.get(Cat5ShuffleboardLayout.Debug_Campose);
                // layout.addBoolean("Is Campose Valid", () -> isCamposeValid());
                layout.addString("Campose", () -> {
                    var campose = getCampose();
                    if (campose != null) {
                        return campose.toString();
                    }
                    return "null";
                });
            }
            {
                var layout = robotContainer.layouts.get(Cat5ShuffleboardLayout.Debug_Pipeline);
                layout.addDouble("Active Pipeline Timer", () -> activePipelineTimer.get());
                layout.addString("Desired Pipeline", () -> desiredPipeline.toString());
                layout.addInteger("Active Pipeline", () -> activePipeline);
            }
            {
                // TODO Will get changed, just display x, y and mode
                var layout = robotContainer.layouts.get(Cat5ShuffleboardLayout.Debug_Distance_Estimation);
                // layout.addDouble("Mid Cone Distance (m)", () -> 0.254 / Math.tan(Math.toRadians(10.0 - getTargetY())));
                layout.addDouble("Mid Cube Distance (m)", () -> 0.403098 / Math.tan(Math.toRadians(10.0 - getTargetY())));
                layout.addDouble("Double Substation Distance (m)", () -> 0.695452 / Math.tan(Math.toRadians(10.0 - getTargetY())));
                layout.addDouble("Mid Retroreflective X (m)", () -> getMidRetroreflectivePoseMeters().getX());
                layout.addDouble("Mid Retroreflective Y (m)", () -> getMidRetroreflectivePoseMeters().getY());
            }
        }
    }
    
    @Override
    public void periodic() {
        if (DriverStation.isDisabled()) {
            setDesiredPipeline(DefaultPipeline);
        }

        long getpipe = getpipeEntry.getInteger(-1);
        if (getpipe != activePipeline) {
            Cat5.print("Limelight pipeline: " + activePipeline + " -> " + getpipe);
            activePipeline = getpipe;
            activePipelineTimer.restart();
        }

        if (!isActivePipeline(desiredPipeline)) {
            pipelineEntry.setNumber(desiredPipeline.getIndex());
        }
    }

    // public boolean isCamposeValid() {
    //     return isActivePipeline(LimelightPipeline.Fiducial) &&
    //         activePipelineTimer.get() > CamposeValidActivePipelineSeconds &&
    //         getTargetArea() > CamposeValidTargetArea &&
    //         robotContainer.getAverageDriveVelocityMetersPerSecond() < CamposeValidAverageDriveVelocityLimitMetersPerSecond;
    // }
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

    public Translation2d getMidRetroreflectivePoseMeters() {
        double yMeters = 0.254 / Math.tan(Math.toRadians(10.0 - getTargetY()));
        double xMeters = Math.tan(Math.toRadians(getTargetX())) * yMeters;
        return new Translation2d(xMeters, yMeters);
    }

    // public Translation2d getHighRetroreflectivePoseMeters() {
    //     double yMeters = 0.254 / Math.tan(Math.toRadians(10.0 - getTargetY()));
    //     double xMeters = Math.tan(Math.toRadians(getTargetX())) * yMeters;
    //     return new Translation2d(xMeters, yMeters);
    // }

    public void printTargetData() {
        StringBuilder builder = new StringBuilder();
        builder.append("Limelight target data:\n");
        builder.append("\ttid: " + getTagId() + "\n");
        builder.append("\ttx: " + getTargetX() + "\n");
        builder.append("\tty: " + getTargetY() + "\n");
        builder.append("\tta: " + getTargetArea());
        Cat5.print(builder.toString());
    }
}
