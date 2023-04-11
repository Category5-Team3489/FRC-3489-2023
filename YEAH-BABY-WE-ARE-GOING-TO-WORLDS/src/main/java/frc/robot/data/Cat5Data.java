package frc.robot.data;

import java.util.ArrayList;
import java.util.function.Supplier;

import frc.robot.interfaces.Cat5Updatable;

public class Cat5Data implements Cat5Updatable {
    private final ArrayList<Cat5Updatable> datapoints = new ArrayList<Cat5Updatable>();

    public <T> Cat5Datapoint<T> createDatapoint(Supplier<T> supplier) {
        var datapoint = new Cat5Datapoint<T>(supplier);
        datapoints.add(datapoint);
        return datapoint;
    }

    @Override
    public void update(double time) {
        for (Cat5Updatable datapoint : datapoints) {
            datapoint.update(time);
        }
    }
}
