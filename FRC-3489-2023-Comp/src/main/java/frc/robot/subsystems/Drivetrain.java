package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Commands;

import frc.robot.Cat5Utils;
import frc.robot.RobotContainer;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.drivetrain.BrakeRotation;
import frc.robot.commands.drivetrain.BrakeTranslation;
import frc.robot.commands.drivetrain.Drive;
import frc.robot.commands.drivetrain.MaxSpeed;
import frc.robot.configs.drivetrain.DriveMotorConfig;
import frc.robot.configs.drivetrain.MaxVelocityConfig;
import frc.robot.configs.drivetrain.OffsetsConfig;
import frc.robot.enums.ModulePosition;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

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
    private final BrakeTranslation brakeTranslationCommand;
    private final BrakeRotation brakeRotationCommand;
    private final MaxSpeed maxSpeedCommand;

    private Drivetrain() {
        super((i) -> instance = i);

        driveCommand = new Drive();
        brakeTranslationCommand = new BrakeTranslation();
        brakeRotationCommand = new BrakeRotation();
        maxSpeedCommand = new MaxSpeed();

        setDefaultCommand(driveCommand);

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

        //#region Bindings
        RobotContainer.get().xbox.leftStick()
            .whileTrue(brakeTranslationCommand);
        RobotContainer.get().xbox.rightStick()
            .whileTrue(brakeRotationCommand);
        
        RobotContainer.get().xbox.y()
            .onTrue(Commands.runOnce(() -> {
                driveCommand.setTargetAngle(Rotation2d.fromDegrees(0));
            }));  
        RobotContainer.get().xbox.b()
            .onTrue(Commands.runOnce(() -> {
                driveCommand.setTargetAngle(Rotation2d.fromDegrees(-90));
            }));
        RobotContainer.get().xbox.a()
            .onTrue(Commands.runOnce(() -> {
                driveCommand.setTargetAngle(Rotation2d.fromDegrees(-180));
            }));
        RobotContainer.get().xbox.x()
            .onTrue(Commands.runOnce(() -> {
                driveCommand.setTargetAngle(Rotation2d.fromDegrees(-270));
            }));
        //#endregion

        //#region Shufflboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.add("Subsystem Info", this);
        layout.addDouble("Average (m per s)", () -> getAverageDriveVelocityMetersPerSecond());

        if (OperatorConstants.DebugShuffleboard) {
            layout.addDouble("Front Left (m per s)", () -> frontLeftModule.getDriveVelocity());
            layout.addDouble("Front Right (m per s)", () -> frontRightModule.getDriveVelocity());
            layout.addDouble("Back Left (m per s)", () -> backLeftModule.getDriveVelocity());
            layout.addDouble("Back Right (m per s)", () -> backRightModule.getDriveVelocity());

            // layout.addDouble("Front Left (A)", () -> DriveMotorConfig.getDriveMotor(ModulePosition.FrontLeft).getStatorCurrent());
            // layout.addDouble("Front Right (A)", () -> DriveMotorConfig.getDriveMotor(ModulePosition.FrontRight).getStatorCurrent());
            // layout.addDouble("Back Left (A)", () -> DriveMotorConfig.getDriveMotor(ModulePosition.BackLeft).getStatorCurrent());
            // layout.addDouble("Back Right (A)", () -> DriveMotorConfig.getDriveMotor(ModulePosition.BackRight).getStatorCurrent());

            var subsystemLayout = getLayout(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList)
                .withSize(2, 1);

            subsystemLayout.add(driveCommand);
            subsystemLayout.add(brakeTranslationCommand);
            subsystemLayout.add(brakeRotationCommand);
            subsystemLayout.add(maxSpeedCommand);
        }
        //#endregion
    }

    //#region Public
    // Front Left
    public void setFrontLeftPercentAngle(double percent, double radians) {
        setFrontLeftVoltageAngle(percent * MaxVoltage, radians);
    }
    public void setFrontLeftSpeedAngle(double speedMetersPerSecond, double radians) {
        setFrontLeftVoltageAngle((speedMetersPerSecond / maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, radians);
    }
    public void setFrontLeftVoltageAngle(double voltage, double radians) {
        frontLeftModule.set(voltage, Cat5Utils.wrapAngle(radians + offsetConfig.getFrontLeftOffsetRadians.getAsDouble()));
    }

    // Front Right
    public void setFrontRightPercentAngle(double percent, double radians) {
        setFrontRightVoltageAngle(percent * MaxVoltage, radians);
    }
    public void setFrontRightSpeedAngle(double speedMetersPerSecond, double radians) {
        setFrontRightVoltageAngle((speedMetersPerSecond / maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, radians);
    }
    public void setFrontRightVoltageAngle(double voltage, double radians) {
        frontRightModule.set(voltage, Cat5Utils.wrapAngle(radians + offsetConfig.getFrontRightOffsetRadians.getAsDouble()));
    }

    // Back Left
    public void setBackLeftPercentAngle(double percent, double radians) {
        setBackLeftVoltageAngle(percent * MaxVoltage, radians);
    }
    public void setBackLeftSpeedAngle(double speedMetersPerSecond, double radians) {
        setBackLeftVoltageAngle((speedMetersPerSecond / maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, radians);
    }
    public void setBackLeftVoltageAngle(double voltage, double radians) {
        backLeftModule.set(voltage, Cat5Utils.wrapAngle(radians + offsetConfig.getBackLeftOffsetRadians.getAsDouble()));
    }

    // Back Right
    public void setBackRightPercentAngle(double percent, double radians) {
        setBackRightVoltageAngle(percent * MaxVoltage, radians);
    }
    public void setBackRightSpeedAngle(double speedMetersPerSecond, double radians) {
        setBackRightVoltageAngle((speedMetersPerSecond / maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble()) * MaxVoltage, radians);
    }
    public void setBackRightVoltageAngle(double voltage, double radians) {
        backRightModule.set(voltage, Cat5Utils.wrapAngle(radians + offsetConfig.getBackRightOffsetRadians.getAsDouble()));
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
        Rotation2d frontLeftRotation = Rotation2d.fromRadians(Cat5Utils.wrapAngle(frontLeftModule.getSteerAngle() - offsetConfig.getFrontLeftOffsetRadians.getAsDouble()));
        Rotation2d frontRightRotation = Rotation2d.fromRadians(Cat5Utils.wrapAngle(frontRightModule.getSteerAngle() - offsetConfig.getFrontRightOffsetRadians.getAsDouble()));
        Rotation2d backLeftRotation = Rotation2d.fromRadians(Cat5Utils.wrapAngle(backLeftModule.getSteerAngle() - offsetConfig.getBackLeftOffsetRadians.getAsDouble()));
        Rotation2d backRightRotation = Rotation2d.fromRadians(Cat5Utils.wrapAngle(backRightModule.getSteerAngle() - offsetConfig.getBackRightOffsetRadians.getAsDouble()));

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
