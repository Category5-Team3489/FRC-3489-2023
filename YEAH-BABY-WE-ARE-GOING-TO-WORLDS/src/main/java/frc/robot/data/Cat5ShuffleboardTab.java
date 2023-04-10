package frc.robot.data;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public enum Cat5ShuffleboardTab {
    Main,
    Auto;

    public ShuffleboardTab get() {
        return Shuffleboard.getTab(toString());
    }
}
