package frc.robot;

public final class Constants {
    public static boolean IsDebugShuffleboardEnabled = true;
    
    public static class OperatorConstants {
        public static final int XboxPort = 0;
        public static final int ManPort = 1;
    }

    public static class CameraConstants {
        public static final int PixelWidth = 160;
        public static final int PixelHeight = 120;

        public static final int FPS = 10;
    }

    public static class ColorSensorConstants {
        public static final double WarningIntervalSeconds = 4.0;
    }

    public static class LedsConstants {
        public static final int LeftChannel = 1;
        public static final int RightChannel = 0;
    }
}