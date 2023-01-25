// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean
 * constants. This class should not be used for any other purpose. All constants should be declared
 * globally (i.e. public static). Do not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants {
    public static ShuffleboardTab getMainTab() {
        return Shuffleboard.getTab("2023");
    }

    public static class OperatorConstants {
        public static final int XboxPort = 0;
        public static final int ManPort = 1;
        public static final double TranslationModifier = 0.3;
        public static final double RotationModifier = 0.3;
    }

    public static class LinearSlideConstants {
        public static final int Motor = 9;
        public static final int BottomLimitSwitch = 24;
        public static final int TopLimitSwitch = 25;
        
        public static final int GotoBottomButton = 11;
        public static final int GotoMiddleButton = 8;
        public static final int GotoTopButton = 7;
        public static final int StopButton = 12;
        
        public static final double ExtendSpeed = 0.3;
        public static final double RetractSpeed = -0.3;

        public static final double EncoderCountLength = 1000;
        public static final double FullExtendEncoder = 100;
        public static final double HalfExtendEncoder = 100;
        public static final double FullretractEncoder = -100;
    }

    public static class LedConstants {
        public static final int Port = 0;
        public static final int Length = 162;
        public static final int TeleopLedLength = 2;
    }

    public static class IntakeConstants {
        public static final int RightIntakeMotor = 10;
        public static final int LeftIntakeMotor = 11;

        public static final double IntakeSpeed = 0.5;

        public static final int IntakeButton = 2;
        public static final int PlacePieceButton = 5;
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

        public static final int FrontLeftModuleDriveMotor = 1;
        public static final int FrontLeftModuleSteerMotor = 2;
        public static final int FrontLeftModuleSteerEncoder = 12;
        public static final double FrontLeftModuleSteerOffset = -Math.toRadians(301.46484375 - 360 - 17.5621);
        
        public static final int FrontRightModuleDriveMotor = 3;
        public static final int FrontRightModuleSteerMotor = 4;
        public static final int FrontRightModuleSteerEncoder = 34;
        public static final double FrontRightModuleSteerOffset = -Math.toRadians(180);

        public static final int BackLeftModuleDriveMotor = 7;
        public static final int BackLeftModuleSteerMotor = 8;
        public static final int BackLeftModuleSteerEncoder = 18;
        public static final double BackLeftModuleSteerOffset = -Math.toRadians(65.21484375 + 18.0861);

        public static final int BackRightModuleDriveMotor = 5;
        public static final int BackRightModuleSteerMotor = 6;
        public static final int BackRightModuleSteerEncoder = 56;
        public static final double BackRightModuleSteerOffset = -Math.toRadians(230.97656250000003 - 180);
    }
}
