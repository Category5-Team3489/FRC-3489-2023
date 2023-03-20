package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMax.ControlType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.enums.WristCommand;
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
    private final CANSparkMax motor = new CANSparkMax(MotorDeviceId, MotorType.kBrushless);
    private final SparkMaxPIDController pidController;
    private final RelativeEncoder encoder;
    
    // Commands
    private final CommandBase gotoTargetCommand = getGotoTargetCommand();

    // State
    private double targetRotations = StartingRotations;
    private WristCommand activeCommand = WristCommand.None;

    private Wrist() {
        super(i -> instance = i);

        setDefaultCommand(gotoTargetCommand);

        //#region Devices
        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kBrake);
        // motor.setInverted(true); FIXME Will inverting this overcomplicate stuff?
        motor.enableVoltageCompensation(12.0);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 20); // TODO Should this be done in other places or with other frames?
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor
        //#endregion

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.addDouble("Encoder Angle (deg)", () -> getEncoderAngleDegrees());
        layout.addDouble("Target Angle (rotations)", () -> targetRotations);
        //#endregion
    }

    //#region Control
    private void setTargetRotations(double rotations) {
        rotations = MathUtil.clamp(rotations, MinRotations, MaxRotations);
        targetRotations = rotations;
    }
    //#endregion

    //#region Encoder
    private double getEncoderAngleDegrees() {
        double rotations = encoder.getPosition();
        return rotations * DegreesPerMotorRevolution;
    }
    //#endregion

    //#region Commands
    private CommandBase getGotoTargetCommand() {
        return run(() -> {
            // TODO Do you want to fight gravity with a calculated arb feedforward, around -2% when tested
            pidController.setReference(targetRotations, ControlType.kPosition, 0);
        })
            .withName("Goto Target");
    }
    //#endregion

    //#region Public
    public void command(WristCommand command) {
        activeCommand = command;

        switch (command) {
            case None:
                setTargetRotations(StartingRotations);
                break;
            case Starting:
                setTargetRotations(StartingRotations);
                break;
            case Horizontal:
                setTargetRotations(HorizontalRotations);
                break;
            case Carrying:
                setTargetRotations(CarryingRotations);
                break;
        }
    }

    public WristCommand getActiveCommand() {
        return activeCommand;
    }
    //#endregion
}