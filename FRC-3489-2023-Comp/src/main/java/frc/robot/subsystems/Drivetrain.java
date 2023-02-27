package frc.robot.subsystems;

import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Cat5Utils;
import frc.robot.commands.Brake;
import frc.robot.commands.DefaultDrivetrain;
import frc.robot.configs.drivetrain.MaxVelocityConfig;
import frc.robot.configs.drivetrain.OffsetsConfig;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.tests.drivetrain.PercentSpeedTest;

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

    // Tests
    public final PercentSpeedTest percentTest = new PercentSpeedTest();

    // Shuffleboard

    // Devices
    private final SwerveModule frontLeftModule;
    private final SwerveModule frontRightModule;
    private final SwerveModule backLeftModule;
    private final SwerveModule backRightModule;

    // State

    private Drivetrain() {
        super((i) -> instance = i);

        setDefaultCommand(new DefaultDrivetrain());

        //#region Init Modules
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
        //#endregion Init Modules
    }

    @Override
    public void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.add("Subsystem Info", this);

        // FIXME Debug stuff
        layout.add(new Brake());
    }

    //#region Set
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
    //#endregion

    //#region Enums
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
    //#endregion
}
