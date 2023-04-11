package frc.robot.data.shuffleboard;

import java.util.HashMap;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class Cat5ShuffleboardLayouts {
    private final HashMap<Cat5ShuffleboardLayout, ShuffleboardLayout> layouts = new HashMap<Cat5ShuffleboardLayout, ShuffleboardLayout>();

    public ShuffleboardLayout get(Cat5ShuffleboardLayout layout) {
        if (layouts.containsKey(layout)) {
            return layouts.get(layout);
        }
        ShuffleboardLayout shuffleboardLayout = layout.create();
        layouts.put(layout, shuffleboardLayout);
        return shuffleboardLayout;
    }
}
