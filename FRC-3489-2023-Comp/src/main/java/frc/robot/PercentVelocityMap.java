package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.math.interpolation.TimeInterpolatableBuffer;

public class PercentVelocityMap {
    private final TimeInterpolatableBuffer<Double> percentToVelocity = TimeInterpolatableBuffer.createDoubleBuffer(1000);
    private final TimeInterpolatableBuffer<Double> velocityToPercent = TimeInterpolatableBuffer.createDoubleBuffer(1000);
    private final ArrayList<Double> percentages = new ArrayList<Double>();
    private final ArrayList<Double> velocities = new ArrayList<Double>();

    private boolean isValid = false;

    public boolean isValid() {
        return isValid;
    }

    public void clear() {
        isValid = false;

        percentToVelocity.clear();
        velocityToPercent.clear();

        percentages.clear();
        velocities.clear();
    }

    public void addSample(double percent, double velocity) {
        percentToVelocity.addSample(percent, velocity);
        velocityToPercent.addSample(velocity, percent);

        isValid = true;

        percentages.add(percent);
        velocities.add(velocity);
    }

    public double getVelocity(double percent) {
        return percentToVelocity.getSample(percent).get();
    }
    public double getPercent(double velocity) {
        return velocityToPercent.getSample(velocity).get();
    }

    public String getPercentagesString() {
        StringBuilder sb = new StringBuilder();
        
        return sb.toString();
    }

    public String getVelocitiesString() {
        StringBuilder sb = new StringBuilder();
        
        return sb.toString();
    }
}