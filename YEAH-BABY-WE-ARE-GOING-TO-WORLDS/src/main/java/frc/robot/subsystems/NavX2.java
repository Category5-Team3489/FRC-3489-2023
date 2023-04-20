package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.I2C.Port;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5;
import frc.robot.RobotContainer;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;

public class NavX2 extends Cat5Subsystem {
    // Devices
    private final AHRS navx = new AHRS(Port.kMXP, (byte)66);

    // State
    private Rotation2d heading = new Rotation2d();
    private Rotation2d headingOffset = new Rotation2d();

    public NavX2(RobotContainer robotContainer) {
        super(robotContainer);

        GenericEntry headingEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Driver)
            .add("NavX2 Heading (deg)", 0.0)
            .getEntry();
        DoubleLogEntry headingLogEntry = new DoubleLogEntry(robotContainer.dataLog, "/navx/heading");
        robotContainer.data.createDatapoint(() -> heading.getDegrees())
            .withShuffleboard(data -> {
                headingEntry.setDouble(data);
            }, 5)
            .withLog(data -> {
                headingLogEntry.append(data);
            }, 25);

        GenericEntry connectedEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Driver)
            .add("NavX2 Connected", false)
            .getEntry();
        BooleanLogEntry connectedLogEntry = new BooleanLogEntry(robotContainer.dataLog, "/navx/connected");
        robotContainer.data.createDatapoint(() -> isConnected())
            .withShuffleboard(data -> {
                connectedEntry.setBoolean(data);
            }, 5)
            .withLog(data -> {
                connectedLogEntry.append(data);
            }, 5);
    }

    public CommandBase getZeroYawCommand() {
        return runOnce(() -> {
            headingOffset = new Rotation2d();
            navx.zeroYaw();
            robotContainer.notifyHeadingJump();
            Cat5.print("NavX2 zeroed yaw and heading offset");
        })
            .ignoringDisable(true)
            .withName("Zero Yaw");
    }

    public void setHeadingOffset(Rotation2d headingOffset) {
        this.headingOffset = headingOffset;
        robotContainer.notifyHeadingJump();
        Cat5.print("NavX2 heading offset updated (deg): " + headingOffset.getDegrees());
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

    public double getTilt() {
        return navx.getPitch();
    }
}
