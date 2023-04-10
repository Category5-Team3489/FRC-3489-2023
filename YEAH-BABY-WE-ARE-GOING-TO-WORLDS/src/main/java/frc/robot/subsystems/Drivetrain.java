package frc.robot.subsystems;

import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.configs.drivetrain.OffsetsConfig;
import frc.robot.data.Cat5ShuffleboardTab;

public class Drivetrain extends Cat5Subsystem {
    // Constants
    private static final double WheelsLeftToRightMeters = 0.54;
    private static final double WheelsFrontToBackMeters = 0.54;
    private static final double MetersPerRotation = SdsModuleConfigurations.MK4_L2.getDriveReduction() * SdsModuleConfigurations.MK4_L2.getWheelDiameter() * Math.PI;
    private static final double TheoreticalMaxVelocityMetersPerSecond = 6380.0 / 60.0 * MetersPerRotation;
    private static final double TheoreticalMaxAngularVelocityMetersPerSecond = TheoreticalMaxVelocityMetersPerSecond / Math.hypot(WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0);
    
    private static final double OmegaProportionalGainDegreesPerSecondPerDegreeOfError = 8.0;
    private static final double OmegaIntegralGainDegreesPerSecondPerDegreeSecondOfError = 0;
    private static final double OmegaDerivativeGainDegreesPerSecondPerDegreePerSecondOfError = 0.2;
    private static final double OmegaToleranceDegrees = 2.5;
    private static final double OmegaTippyToleranceDegrees = 7.5;
    private static final double OmegaMaxDegreesPerSecond = 720;

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

    // Configs
    public final OffsetsConfig offsetConfig = new OffsetsConfig(this);

    // Devices
    public final SwerveModule frontLeftModule;
    public final SwerveModule frontRightModule;
    public final SwerveModule backLeftModule;
    public final SwerveModule backRightModule;

    // Commands
    
    // State
    private Rotation2d targetHeading = null;
    private PIDController omegaController = new PIDController(OmegaProportionalGainDegreesPerSecondPerDegreeOfError, OmegaIntegralGainDegreesPerSecondPerDegreeSecondOfError, OmegaDerivativeGainDegreesPerSecondPerDegreePerSecondOfError);

    public Drivetrain(RobotContainer robotContainer) {
        super(robotContainer);
        
        omegaController.enableContinuousInput(-180.0, 180.0);
        omegaController.setTolerance(OmegaToleranceDegrees);

        if (Constants.IsDebugShuffleboardEnabled) {
            ShuffleboardTab layout = Cat5ShuffleboardTab.SDS_Debug.get();

            frontLeftModule = Mk4SwerveModuleHelper.createFalcon500(
                layout.getLayout("Front Left Module", BuiltInLayouts.kList)
                    .withSize(2, 4)
                    .withPosition(0, 0),
                Mk4SwerveModuleHelper.GearRatio.L2,
                FrontLeftDriveDeviceId,
                FrontLeftSteerDeviceId,
                FrontLeftEncoderDeviceId,
                0.0
            );
    
            frontRightModule = Mk4SwerveModuleHelper.createFalcon500(
                layout.getLayout("Front Right Module", BuiltInLayouts.kList)
                    .withSize(2, 4)
                    .withPosition(2, 0),
                Mk4SwerveModuleHelper.GearRatio.L2,
                FrontRightDriveDeviceId,
                FrontRightSteerDeviceId,
                FrontRightEncoderDeviceId,
                0.0
            );
    
            backLeftModule = Mk4SwerveModuleHelper.createFalcon500(
                layout.getLayout("Back Left Module", BuiltInLayouts.kList)
                    .withSize(2, 4)
                    .withPosition(4, 0),
                Mk4SwerveModuleHelper.GearRatio.L2,
                BackLeftDriveDeviceId,
                BackLeftSteerDeviceId,
                BackLeftEncoderDeviceId,
                0.0
            );
    
            backRightModule = Mk4SwerveModuleHelper.createFalcon500(
                layout.getLayout("Back Right Module", BuiltInLayouts.kList)
                    .withSize(2, 4)
                    .withPosition(6, 0),
                Mk4SwerveModuleHelper.GearRatio.L2,
                BackRightDriveDeviceId,
                BackRightSteerDeviceId,
                BackRightEncoderDeviceId,
                0.0
            );
        }
        else {
            frontLeftModule = Mk4SwerveModuleHelper.createFalcon500(
                Mk4SwerveModuleHelper.GearRatio.L2,
                FrontLeftDriveDeviceId,
                FrontLeftSteerDeviceId,
                FrontLeftEncoderDeviceId,
                0
            );
    
            frontRightModule = Mk4SwerveModuleHelper.createFalcon500(
                Mk4SwerveModuleHelper.GearRatio.L2,
                FrontRightDriveDeviceId,
                FrontRightSteerDeviceId,
                FrontRightEncoderDeviceId,
                0
            );
    
            backLeftModule = Mk4SwerveModuleHelper.createFalcon500(
                Mk4SwerveModuleHelper.GearRatio.L2,
                BackLeftDriveDeviceId,
                BackLeftSteerDeviceId,
                BackLeftEncoderDeviceId,
                0
            );
    
            backRightModule = Mk4SwerveModuleHelper.createFalcon500(
                Mk4SwerveModuleHelper.GearRatio.L2,
                BackRightDriveDeviceId,
                BackRightSteerDeviceId,
                BackRightEncoderDeviceId,
                0
            );
        }
    }
}
