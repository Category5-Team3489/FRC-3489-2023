package frc.robot.configs;

import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public abstract class Cat5Config {
    protected ShuffleboardLayout getLayout(Cat5ShuffleboardTab tab, LayoutType type) {
        String configName = this.getClass().getSimpleName();
        return tab
            .get()
            .getLayout(configName, type);
    }
}
