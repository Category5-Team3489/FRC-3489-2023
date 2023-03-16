package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Cat5Utils;
import frc.robot.commands.drivetrain.BrakeRotation;
import frc.robot.commands.drivetrain.BrakeTranslation;
import frc.robot.commands.drivetrain.Drive;
import frc.robot.configs.drivetrain.DriveMotorConfig;
import frc.robot.configs.drivetrain.MaxVelocityConfig;
import frc.robot.configs.drivetrain.OffsetsConfig;
import frc.robot.enums.ModulePosition;

import static frc.robot.Constants.DrivetrainConstants.*;

public class Drivetrain extends Cat5Subsystem<Drivetrain> {
    //#region Singleton
    private static Drivetrain instance = new Drivetrain();

    public static Drivetrain get() {
        return instance;
    }
    //#endregion

    // Configs
    public final OffsetsConfig offsetConfig = new OffsetsConfig();
    public final MaxVelocityConfig maxVelocityConfig = new MaxVelocityConfig();

    // Devices
    public final SwerveModule frontLeftModule;
    public final SwerveModule frontRightModule;
    public final SwerveModule backLeftModule;
    public final SwerveModule backRightModule;

    // Commands
    public final Drive driveCommand;
    public final BrakeTranslation brakeTranslationCommand;
    public final BrakeRotation brakeRotationCommand;

    // State
    private Rotation2d targetHeading = null;
    private PIDController omegaController = new PIDController(OmegaProportionalGainDegreesPerSecondPerDegreeOfError, OmegaIntegralGainDegreesPerSecondPerDegreeSecondOfError, OmegaDerivativeGainDegreesPerSecondPerDegreePerSecondOfError);

    private Drivetrain() {
        super(i -> instance = i);

        driveCommand = new Drive();
        brakeTranslationCommand = new BrakeTranslation();
        brakeRotationCommand = new BrakeRotation();

        setDefaultCommand(driveCommand);

        omegaController.enableContinuousInput(-180, 180);
        omegaController.setTolerance(OmegaToleranceDegrees / 2.0);

        //#region Devices
        ShuffleboardTab tab = Shuffleboard.getTab("SDS Debug");

        frontLeftModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Front Left Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(0, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            FrontLeftDriveDeviceId,
            FrontLeftSteerDeviceId,
            FrontLeftEncoderDeviceId,
            0
        );

        frontRightModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Front Right Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(2, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            FrontRightDriveDeviceId,
            FrontRightSteerDeviceId,
            FrontRightEncoderDeviceId,
            0
        );

        backLeftModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Back Left Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(4, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            BackLeftDriveDeviceId,
            BackLeftSteerDeviceId,
            BackLeftEncoderDeviceId,
            0
        );

        backRightModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Back Right Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(6, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            BackRightDriveDeviceId,
            BackRightSteerDeviceId,
            BackRightEncoderDeviceId,
            0
        );
        //#endregion
    }

    //#region Public
    public void driveFieldRelative(double xMetersPerSecond, double yMetersPerSecond, double speedLimiter) {

    }

    public void driveFieldRelative(double xMetersPerSecond, double yMetersPerSecond, double omegaRadiansPerSecond, double speedLimiter) {
        double maxVelocityMetersPerSecond = maxVelocityConfig.getMaxVelocityMetersPerSecond();
        
        Rotation2d theta = NavX2.get().getRotation();

        ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xMetersPerSecond, yMetersPerSecond, omegaRadiansPerSecond, theta);
    
        SwerveModuleState[] states = Kinematics.toSwerveModuleStates(chassisSpeeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxVelocityMetersPerSecond * speedLimiter);

        double frontLeftSteerAngleRadians = states[0].angle.getRadians();
        double frontRightSteerAngleRadians = states[1].angle.getRadians();
        double backLeftSteerAngleRadians = states[2].angle.getRadians();
        double backRightSteerAngleRadians = states[3].angle.getRadians();
        
        setFrontLeftSpeedAngle(states[0].speedMetersPerSecond, frontLeftSteerAngleRadians);
        setFrontRightSpeedAngle(states[1].speedMetersPerSecond, frontRightSteerAngleRadians);
        setBackLeftSpeedAngle(states[2].speedMetersPerSecond, backLeftSteerAngleRadians);
        setBackRightSpeedAngle(states[3].speedMetersPerSecond, backRightSteerAngleRadians);
    }

    public void adjustTargetHeading(Rotation2d adjustment) {
        if (targetHeading == null) {
            targetHeading = NavX2.get().getRotation();
        }

        targetHeading = targetHeading.plus(adjustment);
    }

    public void resetTargetHeading() {
        targetHeading = null;
        omegaController.reset();
    }

    // Front Left
    public void setFrontLeftPercentAngle(double percent, double radians) {
        setFrontLeftVoltageAngle(percent * MaxVoltage, radians);
    }
    public void setFrontLeftSpeedAngle(double speedMetersPerSecond, double radians) {
        setFrontLeftVoltageAngle((speedMetersPerSecond / maxVelocityConfig.getMaxVelocityMetersPerSecond()) * MaxVoltage, radians);
    }
    public void setFrontLeftVoltageAngle(double voltage, double radians) {
        frontLeftModule.set(voltage, Cat5Utils.wrapAngle(radians + offsetConfig.getFrontLeftOffsetRadians()));
    }

    // Front Right
    public void setFrontRightPercentAngle(double percent, double radians) {
        setFrontRightVoltageAngle(percent * MaxVoltage, radians);
    }
    public void setFrontRightSpeedAngle(double speedMetersPerSecond, double radians) {
        setFrontRightVoltageAngle((speedMetersPerSecond / maxVelocityConfig.getMaxVelocityMetersPerSecond()) * MaxVoltage, radians);
    }
    public void setFrontRightVoltageAngle(double voltage, double radians) {
        frontRightModule.set(voltage, Cat5Utils.wrapAngle(radians + offsetConfig.getFrontRightOffsetRadians()));
    }

    // Back Left
    public void setBackLeftPercentAngle(double percent, double radians) {
        setBackLeftVoltageAngle(percent * MaxVoltage, radians);
    }
    public void setBackLeftSpeedAngle(double speedMetersPerSecond, double radians) {
        setBackLeftVoltageAngle((speedMetersPerSecond / maxVelocityConfig.getMaxVelocityMetersPerSecond()) * MaxVoltage, radians);
    }
    public void setBackLeftVoltageAngle(double voltage, double radians) {
        backLeftModule.set(voltage, Cat5Utils.wrapAngle(radians + offsetConfig.getBackLeftOffsetRadians()));
    }

    // Back Right
    public void setBackRightPercentAngle(double percent, double radians) {
        setBackRightVoltageAngle(percent * MaxVoltage, radians);
    }
    public void setBackRightSpeedAngle(double speedMetersPerSecond, double radians) {
        setBackRightVoltageAngle((speedMetersPerSecond / maxVelocityConfig.getMaxVelocityMetersPerSecond()) * MaxVoltage, radians);
    }
    public void setBackRightVoltageAngle(double voltage, double radians) {
        backRightModule.set(voltage, Cat5Utils.wrapAngle(radians + offsetConfig.getBackRightOffsetRadians()));
    }

    public SwerveModulePosition[] getModulePositions() {
        // Distance Meters
        TalonFX frontLeftMotor = DriveMotorConfig.getDriveMotor(ModulePosition.FrontLeft);
        TalonFX frontRightMotor = DriveMotorConfig.getDriveMotor(ModulePosition.FrontRight);
        TalonFX backLeftMotor = DriveMotorConfig.getDriveMotor(ModulePosition.BackLeft);
        TalonFX backRightMotor = DriveMotorConfig.getDriveMotor(ModulePosition.BackRight);

        double frontLeftDistanceMeters = (frontLeftMotor.getSelectedSensorPosition() / 2048.0) * MetersPerRotation;
        double frontRightDistanceMeters = (frontRightMotor.getSelectedSensorPosition() / 2048.0) * MetersPerRotation;
        double backLeftDistanceMeters = (backLeftMotor.getSelectedSensorPosition() / 2048.0) * MetersPerRotation;
        double backRightDistanceMeters = (backRightMotor.getSelectedSensorPosition() / 2048.0) * MetersPerRotation;

        // Rotation
        Rotation2d frontLeftRotation = Rotation2d.fromRadians(Cat5Utils.wrapAngle(frontLeftModule.getSteerAngle() - offsetConfig.getFrontLeftOffsetRadians()));
        Rotation2d frontRightRotation = Rotation2d.fromRadians(Cat5Utils.wrapAngle(frontRightModule.getSteerAngle() - offsetConfig.getFrontRightOffsetRadians()));
        Rotation2d backLeftRotation = Rotation2d.fromRadians(Cat5Utils.wrapAngle(backLeftModule.getSteerAngle() - offsetConfig.getBackLeftOffsetRadians()));
        Rotation2d backRightRotation = Rotation2d.fromRadians(Cat5Utils.wrapAngle(backRightModule.getSteerAngle() - offsetConfig.getBackRightOffsetRadians()));

        return new SwerveModulePosition[] {
            new SwerveModulePosition(frontLeftDistanceMeters, frontLeftRotation),
            new SwerveModulePosition(frontRightDistanceMeters, frontRightRotation),
            new SwerveModulePosition(backLeftDistanceMeters, backLeftRotation),
            new SwerveModulePosition(backRightDistanceMeters, backRightRotation)
        };
    }

    public double getAverageDriveVelocityMetersPerSecond() {
        return (Math.abs(frontLeftModule.getDriveVelocity()) + Math.abs(frontRightModule.getDriveVelocity()) + Math.abs(backLeftModule.getDriveVelocity()) + Math.abs(backRightModule.getDriveVelocity())) / 4.0;
    }
    //#endregion
}