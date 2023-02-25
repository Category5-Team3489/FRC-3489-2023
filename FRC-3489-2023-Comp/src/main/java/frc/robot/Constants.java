package frc.robot;

import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;

public final class Constants {
    public static class OperatorConstants {
        public static final int XboxPort = 0;
        public static final int ManPort = 1;
        
        public static final double XboxStickDeadband = 0.05;
    }

    public static class ArmConstants {
        public static final int MotorDeviceId = 11;
        public static final int LimitSwitchChannel = 0;
    
        public static final double MotorRevolutionsPerRevolution = (100.0 / 1.0) * (44.0 / 12.0); // FIXME DOUBLE CHECK THIS CONSTANT
        public static final double MotorRevolutionsPerDegree = MotorRevolutionsPerRevolution / 360.0;
        public static final double DegreesPerMotorRevolution = 1.0 / MotorRevolutionsPerDegree;
        public static final double LimitSwitchAngleDegrees = -120.0; // FIXME
        public static final double MaxAngleDegrees = 0.0; // FIXME
    
        public static final int StallSmartCurrentLimitAmps = 30;

        public static final double MaxResistGravityPercent = 0.025; // FIXME
        public static final double ResistStaticFrictionPercent = 0; // FIXME
        public static final double HomingPercent = -0.15; // FIXME
    
        // TODO IMPORTANT CONVERT EVERY ARM CONSTANT TO DEGREES, NOWHERE REQUIRES RADIANS, NO NEED TO MAKE IT MORE CONFUSING
        // TODO GET RID OF VOLTS ALSO, USE PERCENT
        private static final double ProportionalGainVoltsPerRadianOfError = (0.2 * 12.0) / Math.toRadians(90); // FIXME
        // private static final double IntegralGainVoltsPerRadianSecondOfError = (0.0 * 12.0) / (Math.toRadians(90) * 1.0);
        private static final double DerivativeGainVoltsPerRadianPerSecondOfError = (0.0 * 12.0) / (Math.toRadians(90) / 1.0); // FIXME
        // private static final double FeedforwardGainVoltsPerRadianOfError = (0 * 12.0) / Math.toRadians(90);
        // private static final double IntegrationZoneRadiansOfError = Math.toRadians(45);
    
        public static final double ProportionalGainPercentPerRevolutionOfError = ProportionalGainVoltsPerRadianOfError * ((1.0 / 12.0) / (2.0 * Math.PI));
        // public static final double IntegralGainPercentPerRevolutionMillisecondOfError = IntegralGainVoltsPerRadianSecondOfError * ((1.0 / 12.0)) / ((1.0 / (2.0 * Math.PI)) * (1.0 / 1000.0));
        public static final double DerivativeGainPercentPerRevolutionPerMillisecondOfError = DerivativeGainVoltsPerRadianPerSecondOfError * ((1.0 / 12.0)) / ((1.0 / (2.0 * Math.PI)) / (1.0 / 1000.0));
        // public static final double FeedforwardGainPercentPerRevolutionOfError = FeedforwardGainVoltsPerRadianOfError * ((1.0 / 12.0) / (2.0 * Math.PI));
        // public static final double IntegrationZoneRevolutionsOfError = IntegrationZoneRadiansOfError * (1.0 / (2.0 * Math.PI));
        public static final double MinOutputPercent = -0.4; // FIXME
        public static final double MaxOutputPercent = 0.4; // FIXME
    }
    public static class GripperConstants {
        public static final int LeftMotor = 9;
        public static final int RightMotor = 10;
        public static final int SensorChannel = 0;

        public static final double IntakeSpeed = 0.5;
        public static final double SlowPlaceSpeed = 0.3;

        public static final int IntakeButton = 2;
        public static final int PlacePieceButton = 5;
        public static final int SlowPlaceButton = 6;
    }

    public static class DrivetrainConstants {
        public static final double MaxVoltage = 12.0 * 0.5; // TODO THIS IS A BEN LIMIT

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
