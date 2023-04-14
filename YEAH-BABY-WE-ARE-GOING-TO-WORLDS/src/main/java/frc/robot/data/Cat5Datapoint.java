package frc.robot.data;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Timer;
import frc.robot.Cat5;
import frc.robot.interfaces.Cat5Updatable;

public class Cat5Datapoint<T> implements Cat5Updatable {
    private final Supplier<T> supplier;
    private Consumer<T> updateShuffleboard;
    private Consumer<T> updateLog;
    private BooleanSupplier enableShuffleboard = () -> true;
    private BooleanSupplier enableLog = () -> true;
    private double shuffleboardPeriod = 1.0;
    private double logPeriod = 1.0;

    private double shuffleboardTime;
    private double logTime;

    public Cat5Datapoint(Supplier<T> supplier) {
        this.supplier = supplier;

        shuffleboardTime = Timer.getFPGATimestamp();
        logTime = Timer.getFPGATimestamp();

        // TODO Optimization: cache last set shuffleboard or log and only update if changed, use Comparable interface
    }

    public Cat5Datapoint<T> withShuffleboard(Consumer<T> updateShuffleboard, double shuffleboardHz) {
        this.updateShuffleboard = updateShuffleboard;
        shuffleboardPeriod = 1.0 / shuffleboardHz;
        shuffleboardTime = Timer.getFPGATimestamp() + (Cat5.Rng.nextDouble() * shuffleboardPeriod);
        return this;
    }
    public Cat5Datapoint<T> withLog(Consumer<T> updateLog, double logHz) {
        this.updateLog = updateLog;
        logPeriod = 1.0 / logHz;
        logTime = Timer.getFPGATimestamp() + (Cat5.Rng.nextDouble() * logPeriod);
        return this;
    }
    public Cat5Datapoint<T> withShuffleboardEnabler(BooleanSupplier enableShuffleboard) {
        this.enableShuffleboard = enableShuffleboard;
        return this;
    }
    public Cat5Datapoint<T> withLogEnabler(BooleanSupplier enableLog) {
        this.enableLog = enableLog;
        return this;
    }

    @Override
    public void update(double time) {
        if (time > shuffleboardTime) {
            shuffleboardTime += shuffleboardPeriod;
            if (updateShuffleboard != null) {
                if (enableShuffleboard == null || enableShuffleboard.getAsBoolean()) {
                    updateShuffleboard.accept(supplier.get());
                }
            }
        }

        if (time > logTime) {
            logTime += logPeriod;
            if (updateLog != null) {
                if (enableLog == null || enableLog.getAsBoolean()) {
                    updateLog.accept(supplier.get());
                }
            }
        }
    }
}
