package frc.robot.data;

import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.wpi.first.math.Pair;

public class Cat5DeltaTracker<T> {
    private T current;

    public Cat5DeltaTracker(T inital, Supplier<T> hasChanged, Consumer<Pair<T, T>> onChange) {
        current = inital;
    }
}
