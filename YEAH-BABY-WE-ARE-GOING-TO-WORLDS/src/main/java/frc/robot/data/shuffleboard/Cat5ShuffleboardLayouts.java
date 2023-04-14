package frc.robot.data.shuffleboard;

import java.util.HashMap;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.RobotContainer;

public class Cat5ShuffleboardLayouts {
    // State
    private final RobotContainer robotContainer;
    private final HashMap<Cat5ShuffleboardLayout, ShuffleboardLayout> layouts = new HashMap<Cat5ShuffleboardLayout, ShuffleboardLayout>();

    public Cat5ShuffleboardLayouts(RobotContainer robotContainer) {
        this.robotContainer = robotContainer;
    }

    public ShuffleboardLayout get(Cat5ShuffleboardLayout layout) {
        ShuffleboardLayout shuffleboardLayout;
        if (layouts.containsKey(layout)) {
            shuffleboardLayout = layouts.get(layout);
        }
        else {
            shuffleboardLayout = layout.create();
            layouts.put(layout, shuffleboardLayout);
        }

        robotContainer.configureShuffleboardLayout(layout, shuffleboardLayout);

        return shuffleboardLayout;
    }
}
