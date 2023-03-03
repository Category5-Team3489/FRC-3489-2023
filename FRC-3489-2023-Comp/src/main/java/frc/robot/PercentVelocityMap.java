package frc.robot;

import edu.wpi.first.math.interpolation.TimeInterpolatableBuffer;

public class PercentVelocityMap {
    private final TimeInterpolatableBuffer<Double> percentToVelocity = TimeInterpolatableBuffer.createDoubleBuffer(1000);
    private final TimeInterpolatableBuffer<Double> velocityToPercent = TimeInterpolatableBuffer.createDoubleBuffer(1000);

    private boolean isValid = false;

    public boolean isValid() {
        return isValid;
    }

    public void clear() {
        isValid = false;

        percentToVelocity.clear();
        velocityToPercent.clear();
    }

    public void addSample(double percent, double velocity) {
        percentToVelocity.addSample(percent, velocity);
        velocityToPercent.addSample(velocity, percent);

        isValid = true;
    }

    public double getVelocity(double percent) {
        return percentToVelocity.getSample(percent).get();
    }
    public double getPercent(double velocity) {
        return velocityToPercent.getSample(velocity).get();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // sb.append("")
        return sb.toString();
    }
}