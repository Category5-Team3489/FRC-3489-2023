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
    public static final double MaxVoltage = 12.0;

    public static final double MetersPerRotation = SdsModuleConfigurations.MK4_L2.getDriveReduction() * SdsModuleConfigurations.MK4_L2.getWheelDiameter() * Math.PI;
    public static final double TheoreticalMaxVelocityMetersPerSecond = 6380.0 / 60.0 * MetersPerRotation;

    public static final double WheelsLeftToRightMeters = 0.54;
    public static final double WheelsFrontToBackMeters = 0.54;

    public static final SwerveDriveKinematics Kinematics = new SwerveDriveKinematics(
        new Translation2d(WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0),
        new Translation2d(WheelsLeftToRightMeters / 2.0, -WheelsFrontToBackMeters / 2.0),
        new Translation2d(-WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0),
        new Translation2d(-WheelsLeftToRightMeters / 2.0, -WheelsFrontToBackMeters / 2.0)
    );

    public static final int FrontLeftModuleDriveMotorDeviceId = 1;
    public static final int FrontLeftModuleSteerMotorDeviceId = 2;
    public static final int FrontLeftModuleSteerEncoderDeviceId = 12;

    public static final int FrontRightModuleDriveMotorDeviceId = 3;
    public static final int FrontRightModuleSteerMotorDeviceId = 4;
    public static final int FrontRightModuleSteerEncoderDeviceId = 34;

    public static final int BackLeftModuleDriveMotorDeviceId = 7;
    public static final int BackLeftModuleSteerMotorDeviceId = 8;
    public static final int BackLeftModuleSteerEncoderDeviceId = 18;

    public static final int BackRightModuleDriveMotorDeviceId = 5;
    public static final int BackRightModuleSteerMotorDeviceId = 6;
    public static final int BackRightModuleSteerEncoderDeviceId = 56;

    private static final String MaxVelocityMetersPerSecondPreferencesKey = "Drivetrain/MaxVelocityMetersPerSecond";
    private static double maxVelocityMetersPerSecond;
    public static final DoubleSupplier GetMaxVelocityMetersPerSecond = () -> maxVelocityMetersPerSecond;
    public static final DoubleSupplier GetMaxAngularVelocityRadiansPerSecond = () -> maxVelocityMetersPerSecond / Math.hypot(WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0);

    private static int driveMotorIndex = 0;
    private static final TalonFX[] driveMotors = new TalonFX[4]; // frontLeft, frontRight, backLeft, backRight

    public DrivetrainConstants(Drivetrain subsystem) {
        super(subsystem);

        ShuffleboardLayout configOffsetsLayout = Cat5Shuffleboard.createConstantsLayout("Config Offsets");

        CommandBase enableConfigOffsetsCommand = Commands.startEnd(() -> {
            subsystem.setMode(DrivetrainMode.ConfigOffsets);
        }, () -> {
            subsystem.setMode(DrivetrainMode.ChassisSpeeds);
        })
        .withName("Enable Config Offsets");

        configOffsetsLayout.add("Enable Config Offsets", enableConfigOffsetsCommand)
            .withWidget(BuiltInWidgets.kCommand)
            .withProperties(Map.of("Label position", "HIDDEN"));

        maxVelocityMetersPerSecond = Preferences.getDouble(MaxVelocityMetersPerSecondPreferencesKey, TheoreticalMaxVelocityMetersPerSecond);
    }

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
}
