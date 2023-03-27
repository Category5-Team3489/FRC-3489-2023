package frc.robot;

public class Constants {

     public static class GripperConstants {
        public enum GripperState {
            Off(0, Double.MAX_VALUE),
            LowCone(0.2, 1),
            LowCube(0.2, 0.5),
            LowUnknown(0.2, 0.75), // (cone + cube) / 2
            MidCone(0.1, 2),
            MidCube(1, 0.5),
            MidUnknown(0.4, 1.25),
            HighCone(0.35, 0.6),
            HighCube(1, 0.6),
            HighUnknown(0.675, 0.6),
            Intake(-0.5, Double.MAX_VALUE);

            private final double speed;
            private final double seconds;

            private GripperState(double speed, double seconds) {
                this.speed = speed;
                this.seconds = seconds;
            }

            public double getSpeed() {
                return speed;
            }

            public double getSeconds() {
                return seconds;
            }
        }

        public static final boolean IsConeReintakingEnabled = true;
        public static final boolean IsCubeReintakingEnabled = true;
        public static final double ReintakeAntiConeEatTimeout = 0.75;
        public static final double ReintakeAntiCubeEatTimeout = 0.75;

        public static final int LeftMotorDeviceId = 9;
        public static final int RightMotorDeviceId = 10;
     }
    
}
