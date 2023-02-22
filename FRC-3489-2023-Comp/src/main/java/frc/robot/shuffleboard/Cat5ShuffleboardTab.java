package frc.robot.shuffleboard;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public enum Cat5ShuffleboardTab {
    Main("Main"),
    Config("Config"),
    Tests("Tests");

    private final String name;
    
    private Cat5ShuffleboardTab(String name) {
        this.name = name;
    }

    public ShuffleboardTab get() {
        return Shuffleboard.getTab(name);
    }
}
