package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public abstract class Cat5Subsystem<T extends Cat5Subsystem<?>> extends SubsystemBase {
    protected Cat5Subsystem(Object o) {
        RobotContainer.registerCat5Subsystem(this);
    }

    protected abstract void initShuffleboard();

    protected ShuffleboardLayout getLayout(Cat5ShuffleboardTab tab, LayoutType type) {
        String subsystemName = this.getClass().getSimpleName();
        return tab
            .get()
            .getLayout(subsystemName, type);
    }
}
