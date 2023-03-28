package frc.robot.data;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Cat5DoubleDatapoint extends Cat5Datapoint<Double> {

    public Cat5DoubleDatapoint(Supplier<Double> supplier) {
        super(supplier, );
    }
}
