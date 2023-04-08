package frc.robot;

import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import frc.robot.enums.LimelightPipeline;

public final class Constants {
    public static boolean IsDebugShuffleboardEnabled = false;

    public static class CameraConstants {
        public static final int PixelWidth = 160;
        public static final int PixelHeight = 120;

        public static final int FPS = 10;
    }

    public static class LimelightConstants {
        public static final LimelightPipeline DefaultPipeline = LimelightPipeline.Fiducial;

        public static final long FiducialPipeline = 0;

        public static final double CamposeValidActivePipelineSeconds = 0.5;
        public static final double CamposeValidTargetArea = 0.005;
        public static final double CamposeValidAverageDriveVelocityLimitMetersPerSecond = 0.25;
    }

    public static class DrivetrainConstants {
        public static final double PovSpeedMetersPerSecond = 0.4;

        public static final double OmegaFeedforwardDegreesPerSecond = 35; // 30, 45, 40 fast TODO DANGER

        public static final double XYRateLimiterPercentPerSecond = 3;

        public static final double HeadingAdjustmentMaxDegreesPerSecond = 45;

        public static final double OmegaProportionalGainDegreesPerSecondPerDegreeOfError = 8.0; // 6.0, 7
        public static final double OmegaIntegralGainDegreesPerSecondPerDegreeSecondOfError = 0;
        public static final double OmegaDerivativeGainDegreesPerSecondPerDegreePerSecondOfError = 0.2; // 0.6
        public static final double OmegaToleranceDegrees = 2.5; // 2 bad?
        public static final double OmegaMaxDegreesPerSecond = 720; // FIXME This is around 2 times the max rotation speed of the robot

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

    public static class GripperConstants {
        public static final boolean IsConeReintakingEnabled = true;
        public static final boolean IsCubeReintakingEnabled = true;
        public static final double ReintakeAntiConeEatTimeout = 1.75; // 0.75
        public static final double ReintakeAntiCubeEatTimeout = 1.75; // 0.75

        public static final double IntakePercent = -0.5;

        public static final double LowOuttakeConePercent = 0.2; // 1
        public static final double LowOuttakeCubePercent = 0.2; // 0.3
        public static final double LowOuttakeUnknownPercent = (LowOuttakeConePercent + LowOuttakeCubePercent) / 2.0;
        public static final double MidOuttakeConePercent = 0.1; // good
        public static final double MidOuttakeCubePercent = 0.3;
        public static final double MidOuttakeUnknownPercent = (MidOuttakeConePercent + MidOuttakeCubePercent) / 2.0;
        public static final double HighOuttakeConePercent = 0.5; // 0.6
        public static final double HighOuttakeCubePercent = 1; // good, 0.3
        public static final double HighOuttakeUnknownPercent = (HighOuttakeConePercent + HighOuttakeCubePercent) / 2.0;

        public static final double LowOuttakeConeSeconds = 1; // good, 0.5
        public static final double LowOuttakeCubeSeconds = 0.5;
        public static final double LowOuttakeUnknownSeconds = (LowOuttakeConeSeconds + LowOuttakeCubeSeconds) / 2.0;
        public static final double MidOuttakeConeSeconds = 2; // good, 0.5
        public static final double MidOuttakeCubeSeconds = 0.5;
        public static final double MidOuttakeUnknownSeconds = (MidOuttakeConeSeconds + MidOuttakeCubeSeconds) / 2.0;
        public static final double HighOuttakeConeSeconds = 0.6; // 1
        public static final double HighOuttakeCubeSeconds = 0.6; // 1
        public static final double HighOuttakeUnknownSeconds = (HighOuttakeConeSeconds + HighOuttakeCubeSeconds) / 2.0;

        public static final int LeftMotorDeviceId = 9;
        public static final int RightMotorDeviceId = 10;
        public static final int LimitSwitchChannel = 2;
    }

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

    public static class ArmConstants {
        public static final double CorrectionMaxDegreesPerSecond = 25; // 0.5
    
        public static final double MotorRevolutionsPerRevolution = 64.0 * (64.0 / 12.0); // (100.0 / 1.0) * (44.0 / 12.0)
        public static final double MotorRevolutionsPerDegree = MotorRevolutionsPerRevolution / 360.0;
        public static final double DegreesPerMotorRevolution = 1.0 / MotorRevolutionsPerDegree;

        public static final double MinAngleDegrees = -114;
        public static final double MaxAngleDegrees = 37.0;

        public static final double ManualControlMinAngleDegrees = -100;
        public static final double ManualControlMaxDownPercent = -0.3;
        public static final double ManualControlMaxUpPercent = 0.3;

        public static final double DoubleSubstationDegrees = 12.76; // 1.21

        public static final double FloorAngleDegrees = -70;

        public static final double LowConeAngleDegrees = -70;
        public static final double LowCubeAngleDegrees = -70;
        public static final double LowUnknownAngleDegrees = (LowConeAngleDegrees + LowCubeAngleDegrees) / 2.0;

        public static final double MidConeAngleDegrees = 10.77; // 7.32
        public static final double ScoreMidConeAngleDegrees = -13.33; // -11
        public static final double MidCubeAngleDegrees = -4.08; // 7
        public static final double MidUnknownAngleDegrees = (MidConeAngleDegrees + MidCubeAngleDegrees) / 2.0;

        public static final double HighConeAngleDegrees = 22.86; // 26.13
        public static final double HighCubeAngleDegrees = 21.12; // 28.5
        public static final double HighUnknownAngleDegrees = (HighConeAngleDegrees + HighCubeAngleDegrees) / 2.0;

        public static final double HomingPercent = -0.40; // -0.15
        public static final double HorizontalResistGravityPercent = 0.025;
        public static final double ResistStaticFrictionPercent = 0;

        public static final int StallSmartCurrentLimitAmps = 30;
        public static final double ClosedLoopSecondsToFull = 0.2; // 0.1
        public static final double ProportionalGainPercentPerRevolutionOfError = 0.1; // 0.025
        public static final double MinOutputPercent = -0.8;
        public static final double MaxOutputPercent = 0.8; // 0.75
        
        public static final int MotorDeviceId = 11;
        public static final int LimitSwitchChannel = 1;
    }

    public static class LedsConstants {
        public static final int GamePieceIndicatorManAxis = 3;

        public static final double GamePieceIndicatorThreshold = 0.8;

        public static final int LeftChannel = 1;
        public static final int RightChannel = 0;
    }
}