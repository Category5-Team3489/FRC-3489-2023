// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide
 * numerical or boolean
 * constants. This class should not be used for any other purpose. All constants
 * should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>
 * It is advised to statically import this class (or one of its inner classes)
 * wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static class OperatorConstants {
        public static final int XboxPort = 0;
        public static final int ManPort = 1;
    }

    public static class PowerDistributionHubConstants {
        public static final int Module = 35;
    }

    public static class LedConstants {
        public static final int RightPort = 0;
        public static final int LeftPort = 1;
        public static final int Length = 162;

        public static final int CubeLEDButton = 7;
        public static final int ConeLEDButton = 8;
    }

    public static class PoseEstimatorConstants {
        public static final double TimeUntilReadySeconds = 1;
        public static final double HistorySizeSeconds = 10;
    }

    public static class GripperConstants {
        public static final int LeftMotor = 11;
        public static final int RightMotor = 10;
        public static final int SensorChannel = 12;

        public static final double IntakeSpeed = 0.5;
        public static final double SlowPlaceSpeed = 0.3;

        public static final int IntakeButton = 2;
        public static final int PlacePieceButton = 5;
        public static final int SlowPlaceButton = 6;
    }

    public static class DriverCameraConstants {
        public static final int IndexServoPositionXboxButton = 7;

        public static final int ServoChannel = 1;

        public static final double[] ServoPositions = { 70, 140 };
        public static final int ServoStartingPositionIndex = 0;
    }
}
