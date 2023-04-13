package frc.robot.enums;

import static frc.robot.subsystems.Wrist.*;

public enum WristState {
    Home(0 * DegreesPerMotorRevolution),
    Lowest(5 * DegreesPerMotorRevolution),
    Highest(-13 * DegreesPerMotorRevolution),
    HighestWithHighArm(-30 * DegreesPerMotorRevolution),

    Pickup(-1 * DegreesPerMotorRevolution),
    HighCone(-18.05 * DegreesPerMotorRevolution),
    HighCube(-8.65 * DegreesPerMotorRevolution),
    DoubleSubstation(-6.29 * DegreesPerMotorRevolution);

    private final double degrees;

    private WristState(double degrees) {
        this.degrees = degrees;
    }

    public double getDegrees() {
        return degrees;
    }
}
