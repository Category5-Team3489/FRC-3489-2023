package frc.robot.tests;

import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public abstract class Cat5Test {
    protected ShuffleboardLayout getLayout(Cat5ShuffleboardTab tab, LayoutType type) {
        String testName = this.getClass().getSimpleName();
        return tab
            .get()
            .getLayout(testName, type);
    }
}
