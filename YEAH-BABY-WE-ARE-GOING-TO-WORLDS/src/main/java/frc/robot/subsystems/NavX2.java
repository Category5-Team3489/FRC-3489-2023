package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.RobotContainer;

public class NavX2 extends Cat5Subsystem {
    // Devices
    private final AHRS navx = new AHRS(Port.kMXP, (byte)66);

    // State
    private Rotation2d heading = new Rotation2d();
    private Rotation2d headingOffset = new Rotation2d();

    public NavX2(RobotContainer robotContainer) {
        super(robotContainer);

        // TODO Log heading
        // TODO Switch to new slow update shuffleboard system once complete
        robotContainer.layouts.vitals.addBoolean("NavX2 Connected", () -> isConnected());
    }

    public CommandBase getZeroYawCommand() {
        return runOnce(() -> {
            headingOffset = new Rotation2d();

            navx.zeroYaw();

            navx.isConnected();

            // TODO Reset drivetrain target heading
        })
            .ignoringDisable(true)
            .withName("Zero Yaw");
    }

    public void setHeadingOffset(Rotation2d headingOffset) {
        this.headingOffset = headingOffset;
    }

    public Rotation2d getRotation() {
        heading = Rotation2d.fromDegrees(360.0 - navx.getYaw()).plus(headingOffset);

        return heading;
    }

    public boolean isCalibrated() {
        return !navx.isCalibrating();
    }

    public boolean isConnected() {
        return navx.isConnected();
    }
}
