package frc.robot.shuffleboard;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public enum Cat5ShuffleboardTab {
    Main,
    Auto,
    Leds;

    public ShuffleboardTab get() {
        return Shuffleboard.getTab(toString());
    }
}
