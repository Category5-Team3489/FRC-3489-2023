package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.RobotContainer;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class NavX2 extends Cat5Subsystem<NavX2> {
    //#region Singleton
    private static NavX2 instance = new NavX2();

    public static NavX2 get() {
        return instance;
    }
    //#endregion

    // Devices
    private final AHRS navx = new AHRS(Port.kMXP,(byte) 66);

    // Commands
    private final CommandBase zeroYawCommand = getZeroYawCommand();

    // State
    private Rotation2d heading = new Rotation2d();

    private NavX2() {
        super((i) -> instance = i);

        //#region Bindings
        RobotContainer.get().xbox.start()
            .onTrue(zeroYawCommand);
        //#endregion

        //#region Shuffleboard
        // Main
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.addDouble("Heading (deg)", () -> heading.getDegrees());

        layout.add(zeroYawCommand);
        //#endregion
    }

    //#region Commands
    private CommandBase getZeroYawCommand() {
        return Commands.runOnce(() -> {
            navx.zeroYaw();

            Rotation2d rotation = getRotation();
            Drivetrain.get().driveCommand.setTargetAngle(rotation);
            PoseEstimator.get().notifyNavxZeroYaw(rotation);
        })
            .ignoringDisable(true)
            .withName("Zero Yaw");
    }
    //#endregion

    //#region Public
    public Rotation2d getRotation() {
        // if (navx.isMagnetometerCalibrated()) {
        //     return Rotation2d.fromDegrees(navx.getFusedHeading());
        // }

        heading = Rotation2d.fromDegrees(360.0 - navx.getYaw());

        return heading;
    }

    public boolean isCalibrating() {
        return navx.isCalibrating();
    }
    //#endregion Public
}
