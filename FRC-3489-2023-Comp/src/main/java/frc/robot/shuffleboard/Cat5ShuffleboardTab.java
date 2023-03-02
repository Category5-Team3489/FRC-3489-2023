package frc.robot.shuffleboard;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public enum Cat5ShuffleboardTab {
    Main,
    Drivetrain,
    Arm,
    ColorSensor,
    Gripper,
    PoseEstimator,
    Limelight;

    public ShuffleboardTab get() {
        return Shuffleboard.getTab(toString());
    }
}
