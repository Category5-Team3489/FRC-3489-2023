package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Cat5Math;
import frc.robot.constants.DrivetrainConstants;

import static frc.robot.constants.DrivetrainConstants.*;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Drivetrain extends SubsystemBase {
    public final SwerveModule frontLeftModule;
    public final SwerveModule frontRightModule;
    public final SwerveModule backLeftModule;
    public final SwerveModule backRightModule;

    private DrivetrainMode mode = DrivetrainMode.ChassisSpeeds;
    public final Supplier<DrivetrainMode> getMode = () -> mode;
    public final Consumer<DrivetrainMode> setMode = (newMode) -> mode = newMode;

    // [0, 2pi) radians
    private double frontLeftSteerAngleOffsetRadians = 0;
    private double frontRightSteerAngleOffsetRadians = 0;
    private double backLeftSteerAngleOffsetRadians = 0;
    private double backRightSteerAngleOffsetRadians = 0;
    public final Supplier<Double[]> getOffsets = () -> {
        return new Double[] {
            frontLeftSteerAngleOffsetRadians,
            frontRightSteerAngleOffsetRadians,
            backLeftSteerAngleOffsetRadians,
            backRightSteerAngleOffsetRadians
        };
    };
    public final Consumer<Double[]> setOffsets = (newOffsets) -> {
        frontLeftSteerAngleOffsetRadians = newOffsets[0];
        frontRightSteerAngleOffsetRadians = newOffsets[1];
        backLeftSteerAngleOffsetRadians = newOffsets[2];
        backRightSteerAngleOffsetRadians = newOffsets[3];
    };

    private ChassisSpeeds chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);

    // [0, 2pi) radians
    private double setChassisSpeedsFrontLeftSteerAngleRadians = 0;
    private double setChassisSpeedsFrontRightSteerAngleRadians = 0;
    private double setChassisSpeedsBackLeftSteerAngleRadians = 0;
    private double setChassisSpeedsBackRightSteerAngleRadians = 0;

    public Drivetrain() {
        register();

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

    @Override
    public void periodic() {
        switch (mode) {
            case ConfigOffsets:
                configOffsets();
                break;
            case ChassisSpeeds:
                chassisSpeeds();
                break;
            case Brake:
                brake();
                break;
            case External:
                break;
        }
    }

    public void supplyChassisSpeeds(ChassisSpeeds chassisSpeeds) {
        this.chassisSpeeds = chassisSpeeds;
    }

    private void configOffsets() {

    }

    private void chassisSpeeds() {
        SwerveModuleState[] states = Kinematics.toSwerveModuleStates(chassisSpeeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, GetMaxVelocityMetersPerSecond.getAsDouble());

        if (chassisSpeeds.vxMetersPerSecond != 0 ||
            chassisSpeeds.vyMetersPerSecond != 0 ||
            chassisSpeeds.omegaRadiansPerSecond != 0) {
            setChassisSpeedsFrontLeftSteerAngleRadians = states[0].angle.getRadians();
            setChassisSpeedsFrontRightSteerAngleRadians = states[1].angle.getRadians();
            setChassisSpeedsBackLeftSteerAngleRadians = states[2].angle.getRadians();
            setChassisSpeedsBackRightSteerAngleRadians = states[3].angle.getRadians();

            setFrontLeftSpeedAngle(states[0].speedMetersPerSecond, setChassisSpeedsFrontLeftSteerAngleRadians);
            setFrontRightSpeedAngle(states[1].speedMetersPerSecond, setChassisSpeedsFrontRightSteerAngleRadians);
            setBackLeftSpeedAngle(states[2].speedMetersPerSecond, setChassisSpeedsBackLeftSteerAngleRadians);
            setBackRightSpeedAngle(states[3].speedMetersPerSecond, setChassisSpeedsBackRightSteerAngleRadians);
        }
        else {
            setFrontLeftPercentAngle(0, setChassisSpeedsFrontLeftSteerAngleRadians);
            setFrontRightPercentAngle(0, setChassisSpeedsFrontRightSteerAngleRadians);
            setBackLeftPercentAngle(0, setChassisSpeedsBackLeftSteerAngleRadians);
            setBackRightPercentAngle(0, setChassisSpeedsBackRightSteerAngleRadians);
        }
    }

    private void brake() {
        setFrontLeftPercentAngle(0, Math.toRadians(45));
        setFrontRightPercentAngle(0, Math.toRadians(45 + 90));
        setBackLeftPercentAngle(0, Math.toRadians(45 + 270));
        setBackRightPercentAngle(0, Math.toRadians(45 + 180));
    }
    
    public SwerveModulePosition[] getSwerveModulePositions() {
        // Distance Meters
        TalonFX frontLeftMotor = DrivetrainConstants.getDriveMotor(DriveMotorPosition.FrontLeft);
        TalonFX frontRightMotor = DrivetrainConstants.getDriveMotor(DriveMotorPosition.FrontRight);
        TalonFX backLeftMotor = DrivetrainConstants.getDriveMotor(DriveMotorPosition.BackLeft);
        TalonFX backRightMotor = DrivetrainConstants.getDriveMotor(DriveMotorPosition.BackRight);

        double frontLeftDistanceMeters = (frontLeftMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double frontRightDistanceMeters = (frontRightMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double backLeftDistanceMeters = (backLeftMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double backRightDistanceMeters = (backRightMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;

        // Rotation
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
        ConfigOffsets,
        ChassisSpeeds,
        Brake,
        External
    }

    // Front Left
    private void setFrontLeftPercentAngle(double percent, double angleRadians) {
        setFrontLeftVoltageAngle(percent * MaxVoltage, angleRadians);
    }
    private void setFrontLeftSpeedAngle(double speedMetersPerSecond, double angleRadians) {
        setFrontLeftVoltageAngle((speedMetersPerSecond / GetMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, angleRadians);
    }
    private void setFrontLeftVoltageAngle(double voltage, double angleRadians) {
        frontLeftModule.set(voltage, Cat5Math.offsetAngle(angleRadians, frontLeftSteerAngleOffsetRadians));
    }
    // Front Right
    private void setFrontRightPercentAngle(double percent, double angleRadians) {
        setFrontRightVoltageAngle(percent * MaxVoltage, angleRadians);
    }
    private void setFrontRightSpeedAngle(double speedMetersPerSecond, double angleRadians) {
        setFrontRightVoltageAngle((speedMetersPerSecond / GetMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, angleRadians);
    }
    private void setFrontRightVoltageAngle(double voltage, double angleRadians) {
        frontRightModule.set(voltage, Cat5Math.offsetAngle(angleRadians, frontRightSteerAngleOffsetRadians));
    }
    // Back Left
    private void setBackLeftPercentAngle(double percent, double angleRadians) {
        setBackLeftVoltageAngle(percent * MaxVoltage, angleRadians);
    }
    private void setBackLeftSpeedAngle(double speedMetersPerSecond, double angleRadians) {
        setBackLeftVoltageAngle((speedMetersPerSecond / GetMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, angleRadians);
    }
    private void setBackLeftVoltageAngle(double voltage, double angleRadians) {
        backLeftModule.set(voltage, Cat5Math.offsetAngle(angleRadians, backLeftSteerAngleOffsetRadians));
    }
    // Back Right
    private void setBackRightPercentAngle(double percent, double angleRadians) {
        setBackRightVoltageAngle(percent * MaxVoltage, angleRadians);
    }
    private void setBackRightSpeedAngle(double speedMetersPerSecond, double angleRadians) {
        setBackRightVoltageAngle((speedMetersPerSecond / GetMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, angleRadians);
    }
    private void setBackRightVoltageAngle(double voltage, double angleRadians) {
        backRightModule.set(voltage, Cat5Math.offsetAngle(angleRadians, backRightSteerAngleOffsetRadians));
    }
}
