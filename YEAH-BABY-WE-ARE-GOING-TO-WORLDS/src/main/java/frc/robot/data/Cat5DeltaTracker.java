package frc.robot.data;

import java.util.function.Function;

import frc.robot.RobotContainer;
import frc.robot.interfaces.Cat5Updatable;

public class Cat5DeltaTracker<T> implements Cat5Updatable {
    private T current;
    private final Function<T, Boolean> hasChanged;
    private final Function<T, T> onChange;

    public Cat5DeltaTracker(RobotContainer robotContainer, T inital, Function<T, Boolean> hasChanged, Function<T, T> onChange) {
        current = inital;
        this.hasChanged = hasChanged;
        this.onChange = onChange;

        robotContainer.registerUpdatable(this);
    }

    @Override
    public void update(double time) {
        if (hasChanged.apply(current)) {
            current = onChange.apply(current);
        }
    }
}
