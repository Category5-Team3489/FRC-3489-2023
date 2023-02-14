package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Cat5Math;
import frc.robot.constants.DrivetrainConstants;
import frc.robot.diagnostics.DrivetrainDiagnostics;

import static frc.robot.constants.DrivetrainConstants.*;

public class Drivetrain extends SubsystemBase {
    public final SwerveModule frontLeftModule;
    public final SwerveModule frontRightModule;
    public final SwerveModule backLeftModule;
    public final SwerveModule backRightModule;

    // [0, 2pi) radians
    private double frontLeftSteerAngleRadians = 0;
    private double frontRightSteerAngleRadians = 0;
    private double backLeftSteerAngleRadians = 0;
    private double backRightSteerAngleRadians = 0;

    // [0, 2pi) radians
    private double frontLeftSteerAngleOffsetRadians = 0;
    private double frontRightSteerAngleOffsetRadians = 0;
    private double backLeftSteerAngleOffsetRadians = 0;
    private double backRightSteerAngleOffsetRadians = 0;

    private ChassisSpeeds chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);

    public Drivetrain() {
        ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");

        frontLeftModule = Mk4SwerveModuleHelper.createFalcon500(
        tab.getLayout("Front Left Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(0, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            FrontLeftModuleDriveMotorDeviceId,
            FrontLeftModuleSteerMotorDeviceId,
            FrontLeftModuleSteerEncoderDeviceId,
            0
        );

        frontRightModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Front Right Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(2, 0),
                Mk4SwerveModuleHelper.GearRatio.L2,
            FrontRightModuleDriveMotorDeviceId,
            FrontRightModuleSteerMotorDeviceId,
            FrontRightModuleSteerEncoderDeviceId,
            0
        );

        backLeftModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Back Left Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(4, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            BackLeftModuleDriveMotorDeviceId,
            BackLeftModuleSteerMotorDeviceId,
            BackLeftModuleSteerEncoderDeviceId,
            0
        );

        backRightModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Back Right Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(6, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            BackRightModuleDriveMotorDeviceId,
            BackRightModuleSteerMotorDeviceId,
            BackRightModuleSteerEncoderDeviceId,
            0
        );
    }

    public void drive(ChassisSpeeds chassisSpeeds) {
        this.chassisSpeeds = chassisSpeeds;
    }

    @Override
    public void periodic() {
        
    }

    public void getChargingAngle() {
            frontLeftModule.set(0, 45);
            frontRightModule.set(0, 225);
            backLeftModule.set(0, 135);
            backRightModule.set(0, 315);
    }

    public CommandBase setChargingAngle() {
        return Commands.run(() -> getChargingAngle(), this);
    }

    private void driveChassisSpeeds() {
        SwerveModuleState[] states = Kinematics.toSwerveModuleStates(chassisSpeeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, GetMaxVelocityMetersPerSecond.getAsDouble());

        if (chassisSpeeds.vxMetersPerSecond != 0 ||
            chassisSpeeds.vyMetersPerSecond != 0 ||
            chassisSpeeds.omegaRadiansPerSecond != 0) {
            frontLeftSteerAngleRadians = states[0].angle.getRadians();
            frontRightSteerAngleRadians = states[1].angle.getRadians();
            backLeftSteerAngleRadians = states[2].angle.getRadians();
            backRightSteerAngleRadians = states[3].angle.getRadians();
        }

        frontLeftModule.set(states[0].speedMetersPerSecond / maxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(frontLeftSteerAngleRadians, frontLeftSteerAngleOffsetRadians));
        frontRightModule.set(states[1].speedMetersPerSecond / maxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(frontRightSteerAngleRadians, frontRightSteerAngleOffsetRadians));
        backLeftModule.set(states[2].speedMetersPerSecond / maxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(backLeftSteerAngleRadians, backLeftSteerAngleOffsetRadians));
        backRightModule.set(states[3].speedMetersPerSecond / maxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(backRightSteerAngleRadians, backRightSteerAngleOffsetRadians));
    }

    public void drivePercentAngle(double speedPercent, double angleRadians) {
        driveSpeedAngle(speedPercent * maxVelocityMetersPerSecond, angleRadians);
    }

    private void driveSpeedAngle(double speedMetersPerSecond, double angleRadians) {
        frontLeftModule.set(speedMetersPerSecond / maxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(angleRadians, frontLeftSteerAngleOffsetRadians));
        frontRightModule.set(speedMetersPerSecond / maxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(angleRadians, frontRightSteerAngleOffsetRadians));
        backLeftModule.set(speedMetersPerSecond / maxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(angleRadians, backLeftSteerAngleOffsetRadians));
        backRightModule.set(speedMetersPerSecond / maxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(angleRadians, backRightSteerAngleOffsetRadians));
    }
    
    public SwerveModulePosition[] getSwerveModulePositions() {
        TalonFX frontLeftMotor = DrivetrainConstants.getDriveMotor(DriveMotorPosition.FrontLeft);
        TalonFX frontRightMotor = DrivetrainConstants.getDriveMotor(DriveMotorPosition.FrontRight);
        TalonFX backLeftMotor = DrivetrainConstants.getDriveMotor(DriveMotorPosition.BackLeft);
        TalonFX backRightMotor = DrivetrainConstants.getDriveMotor(DriveMotorPosition.BackRight);

        double frontLeftDistanceMeters = (frontLeftMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double frontRightDistanceMeters = (frontRightMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double backLeftDistanceMeters = (backLeftMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double backRightDistanceMeters = (backRightMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;

        Rotation2d frontLeftRotation = Rotation2d.fromRadians(Cat5Math.offsetAngle(frontLeftModule.getSteerAngle(), -frontLeftSteerAngleOffsetRadians));
        Rotation2d frontRightRotation = Rotation2d.fromRadians(Cat5Math.offsetAngle(frontRightModule.getSteerAngle(), -frontRightSteerAngleOffsetRadians));
        Rotation2d backLeftRotation = Rotation2d.fromRadians(Cat5Math.offsetAngle(backLeftModule.getSteerAngle(), -backLeftSteerAngleOffsetRadians));
        Rotation2d backRightRotation = Rotation2d.fromRadians(Cat5Math.offsetAngle(backRightModule.getSteerAngle(), -backRightSteerAngleOffsetRadians));

        return new SwerveModulePosition[] {
            new SwerveModulePosition(frontLeftDistanceMeters, frontLeftRotation),
            new SwerveModulePosition(frontRightDistanceMeters, frontRightRotation),
            new SwerveModulePosition(backLeftDistanceMeters, backLeftRotation),
            new SwerveModulePosition(backRightDistanceMeters, backRightRotation)
        };
    }

    public enum DrivetrainMode {
        Teleop
    }
}
