package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.CommandBase;

import static frc.robot.Constants.GripperConstants.*;

public class Gripper extends Cat5Subsystem<Gripper> {

    private static Gripper instance = new Gripper();

    public static Gripper get() {
        return instance;
    }

    // Devices
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(LeftMotorDeviceId);
    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(RightMotorDeviceId);

    // Commands
    private final CommandBase setSpeedState = setSpeedState();

    // State
    private GripperState state = GripperState.Off;

    private Gripper() {
        super(i -> instance = i);

        setDefaultCommand(setSpeedState);
    }

    private CommandBase setSpeedState() {
        return run(() -> {
            setSpeed(state.getSpeed());
        })
            .withTimeout(state.getSeconds())
            .withName("Set Speed");
    }

    private void setSpeed(double speed) {
        leftMotor.set(speed);
        rightMotor.set(speed);
    }

    public GripperState getState() {
        return state;
    }
    
    public void setState(GripperState state) {
        this.state = state;
    }

}