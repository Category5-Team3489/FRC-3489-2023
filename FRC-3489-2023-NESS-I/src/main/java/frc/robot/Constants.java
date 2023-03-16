package frc.robot;

import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

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
        public static final int GamePieceIndicatorManAxis = 3;
        public static final double GamePieceIndicatorThreshold = 0.8;

        public static final int LeftChannel = 1;
        public static final int RightChannel = 0;
    }

    public static class DrivetrainConstants {
        public static final int AutomateManButton = 5;
        public static final int StopAutomationManButton = 4;

        public static final double PovSpeedMetersPerSecond = 0.4;

        public static final double XYRateLimiterPercentPerSecond = 3;

        public static final double OmegaProportionalGainDegreesPerSecondPerDegreeOfError = 180.0 / 30.0;
        public static final double OmegaIntegralGainDegreesPerSecondPerDegreeSecondOfError = 0;
        public static final double OmegaDerivativeGainDegreesPerSecondPerDegreePerSecondOfError = 0;
        public static final double OmegaToleranceDegrees = 0.0;
        public static final double OmegaMaxDegreesPerSecond = 720; // FIXME This is around 2 times the max rotation speed of the robot

        public static final double CenterOfRotationMaxScale = 5;

        public static final double MaxVoltage = 12;

        public static final double MetersPerRotation = SdsModuleConfigurations.MK4_L2.getDriveReduction() * SdsModuleConfigurations.MK4_L2.getWheelDiameter() * Math.PI;
        public static final double TheoreticalMaxVelocityMetersPerSecond = 6380.0 / 60.0 * MetersPerRotation;
    
        public static final double WheelsLeftToRightMeters = 0.54;
        public static final double WheelsFrontToBackMeters = 0.54;
    
        public static final Translation2d FrontLeftMeters = new Translation2d(WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0);
        public static final Translation2d FrontRightMeters = new Translation2d(WheelsLeftToRightMeters / 2.0, -WheelsFrontToBackMeters / 2.0);
        public static final Translation2d BackLeftMeters = new Translation2d(-WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0);
        public static final Translation2d BackRightMeters = new Translation2d(-WheelsLeftToRightMeters / 2.0, -WheelsFrontToBackMeters / 2.0);
    
        public static final SwerveDriveKinematics Kinematics = new SwerveDriveKinematics(
            FrontLeftMeters,
            FrontRightMeters,
            BackLeftMeters,
            BackRightMeters
        );

        public static final int FrontLeftDriveDeviceId = 3;
        public static final int FrontLeftSteerDeviceId = 4;
        public static final int FrontLeftEncoderDeviceId = 20;

        public static final int FrontRightDriveDeviceId = 5;
        public static final int FrontRightSteerDeviceId = 6;
        public static final int FrontRightEncoderDeviceId = 21;
    
        public static final int BackLeftDriveDeviceId = 7;
        public static final int BackLeftSteerDeviceId = 8;
        public static final int BackLeftEncoderDeviceId = 22;
    
        public static final int BackRightDriveDeviceId = 1;
        public static final int BackRightSteerDeviceId = 2;
        public static final int BackRightEncoderDeviceId = 23;
    }
}