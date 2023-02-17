package frc.robot.subsystems;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Limelight extends SubsystemBase {
    // private final NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
    // private final NetworkTableEntry botpose = limelight.getEntry("botpose");

    public Limelight() {
        register();
    }

    @Override
    public void periodic() {
        // double[] p = botpose.getDoubleArray(new double[6]);
        // pose = new Pose3d(p[0], p[1], p[2], new Rotation3d(p[3], p[4], p[5]));

        // may need to refesh pose when limelight knows it gets a good one
        // pose estimator doesn't ask limelight if ready, it just goes
        // show some stuff in shuffleboard for that
        // tell pose estimator from here to make new pose
    }

    public Pose2d getPose() {
        return new Pose2d();
    }
}
