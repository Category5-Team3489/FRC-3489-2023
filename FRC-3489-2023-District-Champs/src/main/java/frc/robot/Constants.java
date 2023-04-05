package frc.robot;

public final class Constants {
    public static boolean IsDebugShuffleboardEnabled = true;

    public static class GripperConstants {
        public static final boolean IsConeReintakingEnabled = true;
        public static final boolean IsCubeReintakingEnabled = true;
        public static final double ReintakeAntiConeEatTimeout = 1.75;
        public static final double ReintakeAntiCubeEatTimeout = 1.75;

        public static final double IntakePercent = -0.5;

        public static final double LowOuttakeConePercent = 0.2;
        public static final double LowOuttakeCubePercent = 0.2;
        public static final double LowOuttakeUnknownPercent = (LowOuttakeConePercent + LowOuttakeCubePercent) / 2.0;
        public static final double MidOuttakeConePercent = 0.1;
        public static final double MidOuttakeCubePercent = 0.3;
        public static final double MidOuttakeUnknownPercent = (MidOuttakeConePercent + MidOuttakeCubePercent) / 2.0;
        public static final double HighOuttakeConePercent = 0.5;
        public static final double HighOuttakeCubePercent = 1;
        public static final double HighOuttakeUnknownPercent = (HighOuttakeConePercent + HighOuttakeCubePercent) / 2.0;
        public static final double UnstowPiecePercent = -0.2;

        public static final double LowOuttakeConeSeconds = 1;
        public static final double LowOuttakeCubeSeconds = 0.5;
        public static final double LowOuttakeUnknownSeconds = (LowOuttakeConeSeconds + LowOuttakeCubeSeconds) / 2.0;
        public static final double MidOuttakeConeSeconds = 2;
        public static final double MidOuttakeCubeSeconds = 0.5;
        public static final double MidOuttakeUnknownSeconds = (MidOuttakeConeSeconds + MidOuttakeCubeSeconds) / 2.0;
        public static final double HighOuttakeConeSeconds = 0.6;
        public static final double HighOuttakeCubeSeconds = 0.6;
        public static final double HighOuttakeUnknownSeconds = (HighOuttakeConeSeconds + HighOuttakeCubeSeconds) / 2.0;
        public static final double UnstowPieceSeconds = 0.5;

        public static final int LeftMotorDeviceId = 9;
        public static final int RightMotorDeviceId = 10;
        public static final int LimitSwitchChannel = 2;
    }
}
