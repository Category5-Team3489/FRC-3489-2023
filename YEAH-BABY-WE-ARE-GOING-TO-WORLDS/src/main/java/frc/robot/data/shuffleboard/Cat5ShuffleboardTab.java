package frc.robot.data.shuffleboard;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public enum Cat5ShuffleboardTab {
    Main,
    Drivetrain,
    Auto,
    Swerve_Debug,
    Debug,
    Limelight,
    Gripper;

    public ShuffleboardTab get() {
        return Shuffleboard.getTab(toString());
    }
}
