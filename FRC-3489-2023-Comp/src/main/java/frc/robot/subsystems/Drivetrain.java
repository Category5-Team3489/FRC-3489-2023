package frc.robot.subsystems;

import java.util.function.Consumer;

import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import frc.robot.Cat5Subsystem;
import frc.robot.Cat5Utils;
import frc.robot.commands.Brake;
import frc.robot.commands.TeleopDrive;
import frc.robot.configs.drivetrain.OffsetsConfig;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.tests.drivetrain.PercentSpeedTest;

import static frc.robot.Constants.DrivetrainConstants.*;

public class Drivetrain extends Cat5Subsystem<Drivetrain> {
    //#region Singleton
    private static Drivetrain instance;

    public static Drivetrain get() {
        if (instance == null) {
            instance = new Drivetrain();
        }

        return instance;
    }
    //#endregion

    // Configs
    public final OffsetsConfig offsetConfig = new OffsetsConfig();

    // Tests
    public final PercentSpeedTest percentTest = new PercentSpeedTest();

    // Shuffleboard

    // Devices
    private final SwerveModule frontLeftModule;
    private final SwerveModule frontRightModule;
    private final SwerveModule backLeftModule;
    private final SwerveModule backRightModule;

    // State

    protected Drivetrain() {
        super(null);

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
    protected void initShuffleboard() {
        ShuffleboardLayout main = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 6);
        
        main.add("Subsystem Info", this);

        // FIXME Debug stuff
        main.add(new Brake());
    }

    @Override
    public void periodic() {

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
}
