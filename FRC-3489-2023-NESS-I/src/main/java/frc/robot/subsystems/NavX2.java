package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class NavX2 extends Cat5Subsystem<NavX2> {
    //#region Singleton
    private static NavX2 instance = new NavX2();

    public static NavX2 get() {
        return instance;
    }
    //#endregion

    // Devices
    // private final AHRS navx = new AHRS(Port.kMXP, (byte)66);
    private final AHRS navx = new AHRS(edu.wpi.first.wpilibj.SerialPort.Port.kUSB1);

    // Commands
    private final CommandBase zeroYawCommand = getZeroYawCommand();

    // State
    private Rotation2d heading = new Rotation2d();

    private NavX2() {
        super(i -> instance = i);

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.addDouble("Heading (deg)", () -> heading.getDegrees());
        
        if (Constants.IsDebugShuffleboardEnabled) {
            layout.addBoolean("Is Calibrating", () -> navx.isCalibrating());
        }
        //#endregion
    }

    //#region Commands
    private CommandBase getZeroYawCommand() {
        return runOnce(() -> {
            navx.zeroYaw();

            Drivetrain.get().resetTargetHeading();
        })
            .ignoringDisable(true)
            .withName("Zero Yaw");
    }
    //#endregion

    //#region Public
    public Rotation2d getRotation() {
        heading = Rotation2d.fromDegrees(360.0 - navx.getYaw());

        return heading;
    }

    public void scheduleZeroYawCommand() {
        zeroYawCommand.schedule();
    }
    //#endregion
}