package frc.robot.subsystems;

// import edu.wpi.first.math.geometry.Pose3d;
// import edu.wpi.first.math.geometry.Rotation3d;
// import edu.wpi.first.networktables.NetworkTable;
// import edu.wpi.first.networktables.NetworkTableEntry;
// import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Limelight extends SubsystemBase {
    // private final NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
    // private final NetworkTableEntry botpose = limelight.getEntry("botpose");

    // private Pose3d pose = null;

    public Limelight() {
        register();
    }

    @Override
    public void periodic() {
        // double[] p = botpose.getDoubleArray(new double[6]);
        // pose = new Pose3d(p[0], p[1], p[2], new Rotation3d(p[3], p[4], p[5]));
    }
}
