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
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.Cat5Math;
import frc.robot.constants.DrivetrainConstants;
import frc.robot.shuffleboard.Cat5Shuffleboard;

import static frc.robot.constants.DrivetrainConstants.*;

public class Drivetrain extends SubsystemBase {
    private DrivetrainMode mode = DrivetrainMode.ChassisSpeeds;

    public final SwerveModule frontLeftModule;
    public final SwerveModule frontRightModule;
    public final SwerveModule backLeftModule;
    public final SwerveModule backRightModule;

    public Drivetrain() {
        register();

        ShuffleboardLayout mainLayout = Cat5Shuffleboard.createMainLayout("Drivetrain")
            .withSize(2, 2);
        
        mainLayout.addString("Drivetrain Mode", () -> mode.toString());
        mainLayout.addString("Center of Rotation", () -> centerOfRotation.toString());
        mainLayout.addDouble("COR Multiplier", () -> centerOfRotationMultiplier);

        ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");

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
    }

    //#region Public Interface
    // Mode
    public DrivetrainMode getMode() {
        return mode;
    }
    public void setMode(DrivetrainMode newMode) {
        mode = newMode;
    }

    public SwerveModulePosition[] getSwerveModulePositions() {
        // Distance Meters
        TalonFX frontLeftMotor = DrivetrainConstants.getDriveMotor(ModulePosition.FrontLeft);
        TalonFX frontRightMotor = DrivetrainConstants.getDriveMotor(ModulePosition.FrontRight);
        TalonFX backLeftMotor = DrivetrainConstants.getDriveMotor(ModulePosition.BackLeft);
        TalonFX backRightMotor = DrivetrainConstants.getDriveMotor(ModulePosition.BackRight);

        double frontLeftDistanceMeters = (frontLeftMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double frontRightDistanceMeters = (frontRightMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double backLeftDistanceMeters = (backLeftMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;
        double backRightDistanceMeters = (backRightMotor.getSelectedSensorPosition() / 2048.0) * DrivetrainConstants.MetersPerRotation;

        // Rotation
        Rotation2d frontLeftRotation = Rotation2d.fromRadians(Cat5Math.offsetAngle(frontLeftModule.getSteerAngle(), -DrivetrainConstants.GetFrontLeftOffsetRadians.getAsDouble()));
        Rotation2d frontRightRotation = Rotation2d.fromRadians(Cat5Math.offsetAngle(frontRightModule.getSteerAngle(), -DrivetrainConstants.GetFrontRightOffsetRadians.getAsDouble()));
        Rotation2d backLeftRotation = Rotation2d.fromRadians(Cat5Math.offsetAngle(backLeftModule.getSteerAngle(), -DrivetrainConstants.GetBackLeftOffsetRadians.getAsDouble()));
        Rotation2d backRightRotation = Rotation2d.fromRadians(Cat5Math.offsetAngle(backRightModule.getSteerAngle(), -DrivetrainConstants.GetBackRightOffsetRadians.getAsDouble()));

        return new SwerveModulePosition[] {
            new SwerveModulePosition(frontLeftDistanceMeters, frontLeftRotation),
            new SwerveModulePosition(frontRightDistanceMeters, frontRightRotation),
            new SwerveModulePosition(backLeftDistanceMeters, backLeftRotation),
            new SwerveModulePosition(backRightDistanceMeters, backRightRotation)
        };
    }

    public void setPercentAngle(double percent, double radians) {
        setFrontLeftPercentAngle(percent, radians);
        setFrontRightPercentAngle(percent, radians);
        setBackLeftPercentAngle(percent, radians);
        setBackRightPercentAngle(percent, radians);
    }
    //#endregion

    @Override
    public void periodic() {
        switch (mode) {
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

    //#region Center of Rotation
    private CenterOfRotation centerOfRotation = CenterOfRotation.Center;
    private double centerOfRotationMultiplier = 0;

    public void setCenterOfRotation(CenterOfRotation centerOfRotation, double centerOfRotationMultiplier) {
        this.centerOfRotation = centerOfRotation;
        this.centerOfRotationMultiplier = centerOfRotationMultiplier;
    }

    private Translation2d getCenterOfRotation() {
        return centerOfRotation.times(centerOfRotationMultiplier);
    }
    //#endregion

    //#region Chassis Speeds
    private ChassisSpeeds chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);

    // [0, 2pi) radians
    private double setChassisSpeedsFrontLeftSteerAngleRadians = 0;
    private double setChassisSpeedsFrontRightSteerAngleRadians = 0;
    private double setChassisSpeedsBackLeftSteerAngleRadians = 0;
    private double setChassisSpeedsBackRightSteerAngleRadians = 0;

    public void supplyChassisSpeeds(ChassisSpeeds chassisSpeeds) {
        this.chassisSpeeds = chassisSpeeds;
    }

    private void chassisSpeeds() {
        SwerveModuleState[] states = Kinematics.toSwerveModuleStates(chassisSpeeds, getCenterOfRotation());
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
    //#endregion

    //#region Brake
    private void brake() {
        setFrontLeftPercentAngle(0, Math.toRadians(45 + 90));
        setFrontRightPercentAngle(0, Math.toRadians(45));
        setBackLeftPercentAngle(0, Math.toRadians(45));
        setBackRightPercentAngle(0, Math.toRadians(45 + 90));
    }
    //#endregion Brake

    //#region Set
    // Front Left
    private void setFrontLeftPercentAngle(double percent, double radians) {
        setFrontLeftVoltageAngle(percent * MaxVoltage, radians);
    }
    private void setFrontLeftSpeedAngle(double speedMetersPerSecond, double radians) {
        setFrontLeftVoltageAngle((speedMetersPerSecond / GetMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, radians);
    }
    private void setFrontLeftVoltageAngle(double voltage, double radians) {
        frontLeftModule.set(voltage, Cat5Math.offsetAngle(radians, DrivetrainConstants.GetFrontLeftOffsetRadians.getAsDouble()));
    }
    // Front Right
    private void setFrontRightPercentAngle(double percent, double radians) {
        setFrontRightVoltageAngle(percent * MaxVoltage, radians);
    }
    private void setFrontRightSpeedAngle(double speedMetersPerSecond, double radians) {
        setFrontRightVoltageAngle((speedMetersPerSecond / GetMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, radians);
    }
    private void setFrontRightVoltageAngle(double voltage, double radians) {
        frontRightModule.set(voltage, Cat5Math.offsetAngle(radians, DrivetrainConstants.GetFrontRightOffsetRadians.getAsDouble()));
    }
    // Back Left
    private void setBackLeftPercentAngle(double percent, double radians) {
        setBackLeftVoltageAngle(percent * MaxVoltage, radians);
    }
    private void setBackLeftSpeedAngle(double speedMetersPerSecond, double radians) {
        setBackLeftVoltageAngle((speedMetersPerSecond / GetMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, radians);
    }
    private void setBackLeftVoltageAngle(double voltage, double radians) {
        backLeftModule.set(voltage, Cat5Math.offsetAngle(radians, DrivetrainConstants.GetBackLeftOffsetRadians.getAsDouble()));
    }
    // Back Right
    private void setBackRightPercentAngle(double percent, double radians) {
        setBackRightVoltageAngle(percent * MaxVoltage, radians);
    }
    private void setBackRightSpeedAngle(double speedMetersPerSecond, double radians) {
        setBackRightVoltageAngle((speedMetersPerSecond / GetMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, radians);
    }
    private void setBackRightVoltageAngle(double voltage, double radians) {
        backRightModule.set(voltage, Cat5Math.offsetAngle(radians, DrivetrainConstants.GetBackRightOffsetRadians.getAsDouble()));
    }
    //#endregion

    //#region Enums
    public enum DrivetrainMode {
        ChassisSpeeds,
        Brake,
        External
    }
    public enum ModulePosition {
        FrontLeft(0),
        FrontRight(1),
        BackLeft(2),
        BackRight(3);

        public final int index;

        private ModulePosition(int index) {
            this.index = index;
        }
    }
    public enum CenterOfRotation {
        Center(new Translation2d()),
        FrontLeft(DrivetrainConstants.FrontLeftMeters),
        FrontRight(DrivetrainConstants.FrontRightMeters);

        public final Translation2d offset;

        private CenterOfRotation(Translation2d offset) {
            this.offset = offset;
        }

        public Translation2d times(double multiplier) {
            return offset.times(multiplier);
        }
    }
    //#endregion
}
