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
    private final WPI_TalonSRX motor = new WPI_TalonSRX(MotorDeviceId);
    private final DigitalInput limitSwitch = new DigitalInput(LimitSwitchChannel);
    
    // Commands
    private final CommandBase gotoHomeCommand;
    private final CommandBase gotoTargetCommand;

    // State
    private boolean isHomed = false;
    private double encoderOffsetClicks = 0;
    private double targetAngleDegrees = 0;

    private Wrist() {
        super(i -> instance = i);

        gotoHomeCommand = getGotoHomeCommand();
        gotoTargetCommand = getGotoTargetCommand();

        setDefaultCommand(gotoHomeCommand);

        //#region Devices
        motor.configFactoryDefault();
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
        motor.configPeakOutputForward(MinOutputPercent);
        motor.configPeakOutputReverse(MaxOutputPercent);
        motor.config_kP(0, ProportionalGainPercentPerClickOfError);
        //#endregion

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);
        
        layout.addBoolean("Is Homed", () -> isHomed);
        layout.addDouble("Encoder Offset (clicks)", () -> encoderOffsetClicks);
        layout.addDouble("Target Angle (deg)", () -> targetAngleDegrees);
        layout.addDouble("Encoder Angle (deg)", () -> getEncoderAngleDegrees());
        layout.addBoolean("Limit Switch", () -> limitSwitch.get());

        layout.add("Force Home", Commands.runOnce(() -> {
            forceHome();
        })
            .withName("Force Home")
        );
        //#endregion
    }

    @Override
    public void periodic() {
        if (limitSwitch.get()) {
            resetEncoder();
        }

        if (isHomed) {
            gotoTargetCommand.schedule();
        }
        else {
            gotoHomeCommand.schedule();
        }
    }

    //#region Encoder
    private double getEncoderAngleDegrees() {
        double clicks = motor.getSelectedSensorPosition() - encoderOffsetClicks;
        return clicks * DegreesPerClick;
    }

    private void resetEncoder() {
        encoderOffsetClicks = motor.getSelectedSensorPosition();
        isHomed = true;
    }
    //#endregion

    //#region Commands
    private CommandBase getGotoHomeCommand() {
        return run(() -> {
            if (isHomed) {
                return;
            }
            
            motor.setVoltage(HomingPercent * 12.0);
        })
            .withName("Goto Home");
    }
    
    private CommandBase getGotoTargetCommand() {
        return run(() -> {
            double targetClicks = encoderOffsetClicks + (targetAngleDegrees * ClicksPerDegree);
            motor.set(ControlMode.Position, targetClicks);
        })
            .withName("Goto Target");
    }
    //#endregion

    //#region Public
    public void forceHome() {
        isHomed = false;
    }

    public void setTargetAngleDegrees(double angleDegrees) {
        targetAngleDegrees = angleDegrees;
    }
    //#endregion
}