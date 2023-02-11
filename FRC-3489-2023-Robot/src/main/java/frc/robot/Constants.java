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

    public static class ArmConstants {
        public static final int MotorDeviceId = 1;
        public static final int LimitSwitchChannel = 1;

        public static final double V4BarGearRatio = 45 / 1;
        public static final double RadiansPerEncoderCount = 1 / (V4BarGearRatio * 2 * Math.PI);
        public static final double LimitSwitchAngleRadians = Math.toRadians(-80);
        public static final double MaxAngleRadians = Math.toRadians(90);

        public static final double HorizontalResistGravityVolts = 0.05 * 12;
        public static final double ResistStaticFrictionVolts = 0.05 * 12;
        public static final double HomingVolts = -0.15 * 12;

        public static final double ProportionalGainPercentPer = 0.1;
    }

    public static class NavX2Constants {
        public static final int ZeroYawXboxButton = 9;
    }

    public static class LedConstants {
        public static final int Port = 0;
        public static final int Length = 162;
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

    public static class DrivetrainConstants {
        /**
         * The left-to-right distance between the drivetrain wheels
         * Should be measured from center to center.
         */
        public static final double DrivetrainTrackwidthMeters = 0.54;
        /**
         * The front-to-back distance between the drivetrain wheels.
         * Should be measured from center to center.
         */
        public static final double DrivetrainWheelbaseMeters = 0.54;

        public static final int FrontLeftModuleDriveMotorDeviceId = 1;
        public static final int FrontLeftModuleSteerMotorDeviceId = 2;
        public static final int FrontLeftModuleSteerEncoderDeviceId = 12;

        public static final int FrontRightModuleDriveMotorDeviceId = 3;
        public static final int FrontRightModuleSteerMotorDeviceId = 4;
        public static final int FrontRightModuleSteerEncoderDeviceId = 34;

        public static final int BackLeftModuleDriveMotorDeviceId = 7;
        public static final int BackLeftModuleSteerMotorDeviceId = 8;
        public static final int BackLeftModuleSteerEncoderDeviceId = 18;

        public static final int BackRightModuleDriveMotorDeviceId = 5;
        public static final int BackRightModuleSteerMotorDeviceId = 6;
        public static final int BackRightModuleSteerEncoderDeviceId = 56;
    }

    public static class PreferencesKeys {
        public static final String DrivetrainMaxVelocityMetersPerSecond = "DrivetrainMaxVelocityMetersPerSecond";
    }
}
