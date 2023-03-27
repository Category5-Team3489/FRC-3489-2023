package frc.robot.subsystems;

import java.util.function.Consumer;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.RobotContainer;

public abstract class Cat5Subsystem<T extends Cat5Subsystem<?>> extends SubsystemBase {
    @SuppressWarnings("unchecked")
    protected Cat5Subsystem(Consumer<T> initSingleton) {
        initSingleton.accept((T)this);
        RobotContainer.registerCat5Subsystem(this);
    }
}
