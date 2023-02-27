package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;
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
    private final AHRS navx = new AHRS(SPI.Port.kMXP);

    // Commands
    private final CommandBase zeroYawCommand = getZeroYawCommand();

    // State
    private Rotation2d heading = new Rotation2d();

    private NavX2() {
        super((i) -> instance = i);

        // TODO Zeroing the yaw effects the pose estimator!!!!!!!!!!!!!!!

        RobotContainer.get().xbox.start()
            .onTrue(zeroYawCommand);
    }

    @Override
    public void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.addString("Heading", () -> Double.toString(heading.getDegrees()));
        layout.add(zeroYawCommand);
    }

    public Rotation2d getRotation() {
        // if (navx.isMagnetometerCalibrated()) {
        //     return Rotation2d.fromDegrees(navx.getFusedHeading());
        // }

        heading = Rotation2d.fromDegrees(360.0 - navx.getYaw());

        return heading;
    }

    //#region Commands
    private CommandBase getZeroYawCommand() {
        return Commands.runOnce(() -> {
            navx.zeroYaw();
        })
            .withName("Zero Yaw")
            .ignoringDisable(true);
    }
    //#endregion
}
