// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static class OperatorConstants {
        public static final int XboxPort = 0;
        public static final int ManPort = 1;
    }

    public static class PowerDistributionConstants{
        public static final int PDHCanID = 35;
    }

    public static class ArmConstants {
        public static final int MotorDeviceId = 1;
        public static final int LimitSwitchChannel = 1;
    }

    public static class LedConstants {
        public static final int Port = 0;
        public static final int Length = 162;
        public static final int TeleopLedLength = 2;
    }

    public static class IntakeConstants {
        public static final int RightIntakeMotor = 10;
        public static final int LeftIntakeMotor = 11;
        public static final int GripperSensor = 12;

        public static final double IntakeSpeed = 0.5;
        public static final double SlowPlaceSpeed = 0.3;

        public static final int IntakeButton = 2;
        public static final int PlacePieceButton = 5;
        public static final int SlowPlaceButton = 6;
    }

    public static class CameraConstants {
        public static final double[] ServoPositions = { 70, 140 };
        public static final int ServoStartingPositionIndex = 0;

        public static final int CameraServoButton = 7;
    }

    public static class DrivetrainConstants {
        public static final int NavXZeroYawButton = 9;
        
        // TODO THIS LIMITS ROBOT SPEED
        public static final double TranslationModifier = 1.0 / 3.0;
        public static final double RotationModifier = 1.0 / 3.0;

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

        // TODO Get better labeling convention for encoder CAN ids
        public static final int FrontLeftModuleDriveMotorDeviceId = 1;
        public static final int FrontLeftModuleSteerMotorDeviceId = 2;
        public static final int FrontLeftModuleSteerEncoderDeviceId = 12;
        public static final double FrontLeftModuleSteerOffset = -Math.toRadians(301.46484375 - 360 - 17.5621);
        
        public static final int FrontRightModuleDriveMotorDeviceId = 3;
        public static final int FrontRightModuleSteerMotorDeviceId = 4;
        public static final int FrontRightModuleSteerEncoderDeviceId = 34;
        public static final double FrontRightModuleSteerOffset = -Math.toRadians(180);

        public static final int BackLeftModuleDriveMotorDeviceId = 7;
        public static final int BackLeftModuleSteerMotorDeviceId = 8;
        public static final int BackLeftModuleSteerEncoderDeviceId = 18;
        public static final double BackLeftModuleSteerOffset = -Math.toRadians(65.21484375 + 18.0861);

        public static final int BackRightModuleDriveMotorDeviceId = 5;
        public static final int BackRightModuleSteerMotorDeviceId = 6;
        public static final int BackRightModuleSteerEncoderDeviceId = 56;
        public static final double BackRightModuleSteerOffset = -Math.toRadians(230.97656250000003 - 180);
    }
}
