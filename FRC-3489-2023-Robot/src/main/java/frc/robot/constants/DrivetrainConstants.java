package frc.robot.constants;

import java.util.Map;
import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.can.TalonFX;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.shuffleboard.Cat5Shuffleboard;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Drivetrain.ModulePosition;
import frc.robot.subsystems.Drivetrain.DrivetrainMode;

public class DrivetrainConstants extends ConstantsBase<Drivetrain> {
    //#region General
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
    //#endregion

    //#region CAN IDs
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
    //#endregion

    public DrivetrainConstants(Drivetrain subsystem) {
        super(subsystem);

        initConfigOffsets();
        initMaxVelocity();
    }

    //#region Drive TalonFX
    private static int driveMotorIndex = 0;
    private static final TalonFX[] driveMotors = new TalonFX[4]; // frontLeft, frontRight, backLeft, backRight
    
    public static void supplyDriveMotor(TalonFX motor) {
        if (driveMotorIndex == 4) {
            System.out.println("[ERORR] TOO MANY DRIVE MOTORS SUPPLIED!!!");
            return;
        }
        
        driveMotors[driveMotorIndex++] = motor;
    }
    public static TalonFX getDriveMotor(ModulePosition position) {
        return driveMotors[position.index];
    }
    //#endregion

    //#region Offsets
    // [0, 2pi) radians
    private static double frontLeftOffsetRadians = 0;
    private static double frontRightOffsetRadians = 0;
    private static double backLeftOffsetRadians = 0;
    private static double backRightOffsetRadians = 0;

    public static final DoubleSupplier GetFrontLeftOffsetRadians = () -> frontLeftOffsetRadians;
    public static final DoubleSupplier GetFrontRightOffsetRadians = () -> frontRightOffsetRadians;
    public static final DoubleSupplier GetBackLeftOffsetRadians = () -> backLeftOffsetRadians;
    public static final DoubleSupplier GetBackRightOffsetRadians = () -> backRightOffsetRadians;

    public static final String FrontLeftOffsetRadiansPreferencesKey = "Drivetrain/FrontLeftOffsetRadians";
    public static final String FrontRightOffsetRadiansPreferencesKey = "Drivetrain/FrontRightOffsetRadians";
    public static final String BackLeftOffsetRadiansPreferencesKey = "Drivetrain/BackLeftOffsetRadians";
    public static final String BackRightOffsetRadiansPreferencesKey = "Drivetrain/BackRightOffsetRadians";
    
    private void initConfigOffsets() {
        // Preferences.setDouble(DrivetrainConstants.BackLeftOffsetRadiansPreferencesKey, 1.4481737375632882 - Math.toRadians(38.2));

        frontLeftOffsetRadians = Preferences.getDouble(FrontLeftOffsetRadiansPreferencesKey, 0);
        frontRightOffsetRadians = Preferences.getDouble(FrontRightOffsetRadiansPreferencesKey, 0);
        backLeftOffsetRadians = Preferences.getDouble(BackLeftOffsetRadiansPreferencesKey, 0);
        backRightOffsetRadians = Preferences.getDouble(BackRightOffsetRadiansPreferencesKey, 0);

        // System.out.println(backLeftOffsetRadians);

        ShuffleboardLayout offsetsLayout = Cat5Shuffleboard.createConstantsLayout("Offsets");

        offsetsLayout.addDouble("Front Left", () -> Math.toDegrees(GetFrontLeftOffsetRadians.getAsDouble()));
        offsetsLayout.addDouble("Front Right", () -> Math.toDegrees(GetFrontRightOffsetRadians.getAsDouble()));
        offsetsLayout.addDouble("Back Left", () -> Math.toDegrees(GetBackLeftOffsetRadians.getAsDouble()));
        offsetsLayout.addDouble("Back Right", () -> Math.toDegrees(GetBackRightOffsetRadians.getAsDouble()));

        // Zero
        CommandBase zeroCommand = Commands.runOnce(() -> {
            subsystem.setMode(DrivetrainMode.External);

            subsystem.frontLeftModule.set(0, 0);
            subsystem.frontRightModule.set(0, 0);
            subsystem.backLeftModule.set(0, 0);
            subsystem.backRightModule.set(0, 0);
        })
        .withName("Zero");

        offsetsLayout.add("Zero", zeroCommand)
            .withWidget(BuiltInWidgets.kCommand)
            .withProperties(Map.of("Label position", "HIDDEN"));

        // Chassis Speeds
        CommandBase chassisSpeedsCommand = Commands.runOnce(() -> {
            subsystem.setMode(DrivetrainMode.ChassisSpeeds);
        })
        .withName("Chassis Speeds");

        offsetsLayout.add("Chassis Speeds", chassisSpeedsCommand)
            .withWidget(BuiltInWidgets.kCommand)
            .withProperties(Map.of("Label position", "HIDDEN"));

        // Save
        CommandBase saveCommand = Commands.runOnce(() -> {
            double frontLeft = subsystem.frontLeftModule.getSteerAngle();
            double frontRight = subsystem.frontRightModule.getSteerAngle();
            double backLeft = subsystem.backLeftModule.getSteerAngle();
            double backRight = subsystem.backRightModule.getSteerAngle();

            frontLeftOffsetRadians = frontLeft;
            frontRightOffsetRadians = frontRight;
            backLeftOffsetRadians = backLeft;
            backRightOffsetRadians = backRight;

            Preferences.setDouble(DrivetrainConstants.FrontLeftOffsetRadiansPreferencesKey, frontLeft);
            Preferences.setDouble(DrivetrainConstants.FrontRightOffsetRadiansPreferencesKey, frontRight);
            Preferences.setDouble(DrivetrainConstants.BackLeftOffsetRadiansPreferencesKey, backLeft);
            Preferences.setDouble(DrivetrainConstants.BackRightOffsetRadiansPreferencesKey, backRight);
        })
        .ignoringDisable(true)
        .withName("Save");

        offsetsLayout.add("Save", saveCommand)
            .withWidget(BuiltInWidgets.kCommand)
            .withProperties(Map.of("Label position", "HIDDEN"));
    }
    //#endregion

    //#region Max Velocity
    private static double maxVelocityMetersPerSecond;

    private static final String MaxVelocityMetersPerSecondPreferencesKey = "Drivetrain/MaxVelocityMetersPerSecond";

    public static final DoubleSupplier GetMaxVelocityMetersPerSecond = () -> maxVelocityMetersPerSecond;
    public static final DoubleSupplier GetMaxAngularVelocityRadiansPerSecond = () -> maxVelocityMetersPerSecond / Math.hypot(WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0);

    private void initMaxVelocity() {
        Preferences.setDouble(MaxVelocityMetersPerSecondPreferencesKey, 4.965078313782811); // Measured with max speed diagnostic
        maxVelocityMetersPerSecond = Preferences.getDouble(MaxVelocityMetersPerSecondPreferencesKey, TheoreticalMaxVelocityMetersPerSecond);
    }
    //#endregion
}
