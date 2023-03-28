package frc.robot.data;

import java.util.function.Consumer;
import java.util.function.Supplier;

import frc.robot.Cat5Utils;

public class Cat5Datapoint<T> {
    private final Supplier<T> supplier;
    private final Consumer<T> updateShuffleboard;
    private final Consumer<T> updateLog;
    private final double shuffleboardPeriod;
    private final double logPeriod;

    private double shuffleboardTime;
    private double logTime;

    public Cat5Datapoint(Supplier<T> supplier, Consumer<T> updateShuffleboard, Consumer<T> updateLog, double shuffleboardHz, double logHz) {
        this.supplier = supplier;
        this.updateShuffleboard = updateShuffleboard;
        this.updateLog = updateLog;
        shuffleboardPeriod = 1.0 / shuffleboardHz;
        logPeriod = 1.0 / logHz;

        shuffleboardTime = Cat5Utils.Rng.nextDouble() * shuffleboardPeriod;
        logTime = Cat5Utils.Rng.nextDouble() * logPeriod;
    }

    public void update(double time) {
        if (time > shuffleboardTime) {
            shuffleboardTime += shuffleboardPeriod;
            updateShuffleboard.accept(supplier.get());
        }

        if (time > logTime) {
            logTime += logPeriod;
            updateLog.accept(supplier.get());
        }
    }
}
