package frc.robot;

import java.util.function.Consumer;

import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public abstract class Cat5Subsystem<T extends Cat5Subsystem<?>> extends SubsystemBase {
    @SuppressWarnings("unchecked")
    protected Cat5Subsystem(Consumer<T> initSingleton) {
        initSingleton.accept((T)this);
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
