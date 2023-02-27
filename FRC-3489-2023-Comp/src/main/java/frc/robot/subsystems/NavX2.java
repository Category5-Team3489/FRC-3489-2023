package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
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

    // State
    private Rotation2d heading = new Rotation2d();

    private NavX2() {
        super((i) -> instance = i);
    }

    @Override
    public void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.add("Subsystem Info", this);

        layout.addDouble("Heading", () -> heading.getDegrees());
    }

    public void zeroYaw() {
        navx.zeroYaw();
    }

    public Rotation2d getRotation() {
        // if (navx.isMagnetometerCalibrated()) {
        //     return Rotation2d.fromDegrees(navx.getFusedHeading());
        // }

        heading = Rotation2d.fromDegrees(360.0 - navx.getYaw());

        return heading;
    }
}
