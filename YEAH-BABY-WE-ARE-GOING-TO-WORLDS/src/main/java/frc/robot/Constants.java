package frc.robot;

public class Constants {
    public static boolean IsDebugShuffleboardEnabled = false;
    public static boolean IsSDSDebugEnabled = false;

    public static class WristConstants {
        public enum WristState {
            Min(-13),
            Max(5),
            HighCone(-18.05), // -20.05
            MinAtHigh(-30),
            DoubleSubstation(-6.29),
            HighCube(-8.65),
            Start(0),
            Pickup(-1),
            Carry(-13);

            private final double rotations;

            private WristState(double rotations) {
                this.rotations = rotations;
            }

            public double getRotations() {
                return rotations;
            }
        }

        public static final double MotorRevolutionsPerRevolution = (100.0 / 1.0) * (2.0 / 1.0);
        public static final double MotorRevolutionsPerDegree = MotorRevolutionsPerRevolution / 360.0;
        public static final double DegreesPerMotorRevolution = 1.0 / MotorRevolutionsPerDegree;

        public static final int StallSmartCurrentLimitAmps = 20;
        public static final double ProportionalGainPercentPerRevolutionOfError = 0.5;
        public static final double MinOutputPercent = -0.30; // 15
        public static final double MaxOutputPercent = 0.30; // 15

        public static final int MotorDeviceId = 12;
    }
}
