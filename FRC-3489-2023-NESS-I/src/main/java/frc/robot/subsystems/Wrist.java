package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.WristConstants.*;

public class Wrist extends Cat5Subsystem<Wrist> {
    //#region Singleton
    private static Wrist instance = new Wrist();

    public static Wrist get() {
        return instance;
    }
    //#endregion

    // Devices
    private final WPI_TalonSRX motor = new WPI_TalonSRX(WristMotorId);
    private final DigitalInput topLimitSwitch = new DigitalInput(topLimitSwitchChannel);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(bottomLimitSwitchChannel);

    // Commands
    private final CommandBase setWristAngleCommand;
    private final CommandBase homeBottomWristCommand;
    private final CommandBase homeTopWristCommand;

    // State
    private double targetClicks;
    private boolean isTopHomed = false;
    private boolean isBottomHomed = false;

    private Wrist() {
        super((i) -> instance = i);

        setWristAngleCommand = setWristAngleCommand();
        homeBottomWristCommand = homeBottomWristCommand();
        homeTopWristCommand = homeTopWristCommand();

        motor.configFactoryDefault();
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        motor.configPeakOutputForward(PeakOutputForward);
		motor.configPeakOutputReverse(PeakOutputReverse);

        motor.config_kP(SlotIdx, KP);

        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.addDouble("Wrist Target Clicks", () -> targetClicks);
        layout.addBoolean("Is Bottom Homed", () -> isBottomHomed);
        layout.addBoolean("Is Top Homed", () -> isTopHomed);
        layout.addBoolean("Top Limit Switch", () -> topLimitSwitch.get());
        layout.addBoolean("Bottom Limit Switch", () -> bottomLimitSwitch.get());
    }

    public void setWristAngle(double targetPositionDegree) {
        targetClicks = targetPositionDegree * (ClicksPerRotation / 360);
    }

    private CommandBase setWristAngleCommand() {
        return Commands.run(() -> {
            motor.set(ControlMode.Position, targetClicks);
        }, this);
    }

    private CommandBase homeTopWristCommand() {
        return Commands.run(() -> {
            if (isTopHomed) {
                return;
            }
            if (!isTopHomed && topLimitSwitch.get()) {
                setWristAngle(MaxAngleDegrees);
                isTopHomed = true;
            }
            else {
                motor.setVoltage(HomingPercent * 12.0);
            }
        }, this);
    }

    private CommandBase homeBottomWristCommand() {
        return Commands.run(() -> {
            if (isBottomHomed) {
                return;
            }
            if (!isBottomHomed && bottomLimitSwitch.get()) {
                setWristAngle(MinAngleDegrees);
                isBottomHomed = true;
            }
            else {
                motor.setVoltage(-HomingPercent * 12.0);
            }
        }, this);
    }

    @Override
    public void periodic() {
        if (isBottomHomed) {
            setWristAngleCommand.schedule();
        }
        else {
            homeBottomWristCommand.schedule();
        }
    }
}
