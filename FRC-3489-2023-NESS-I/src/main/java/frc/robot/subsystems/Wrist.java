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
import frc.robot.Cat5Inputs;
import frc.robot.Robot;
import frc.robot.enums.GridPosition;
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
    private final CANSparkMax motor = new CANSparkMax(MotorDeviceId, MotorType.kBrushless); // Negative up, positive down
    private final SparkMaxPIDController pidController;
    private final RelativeEncoder encoder;
    
    // Commands
    private final CommandBase gotoTargetCommand = getGotoTargetCommand();

    // State
    private WristState state = WristState.Start;
    private double rotations = WristState.Start.getRotations();

    private Wrist() {
        super(i -> instance = i);

        setDefaultCommand(gotoTargetCommand);

        //#region Devices
        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kBrake);
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

        layout.addString("State", () -> state.toString());
        layout.addDouble("Target (rotations)", () -> rotations);
        layout.addDouble("Encoder (rotations)", () -> encoder.getPosition());
        //#endregion
    }

    @Override
    public void periodic() {
        double correctionPercent = Cat5Inputs.getWristCorrectionPercent();
        rotations -= correctionPercent * 7.5 * Robot.kDefaultPeriod;
        if (Arm.get().getGridPosition() == GridPosition.High) {
            rotations = MathUtil.clamp(rotations, WristState.MinAtHigh.getRotations(), WristState.Max.getRotations());
        }
        else {
            rotations = MathUtil.clamp(rotations, WristState.Min.getRotations(), WristState.Max.getRotations());
        }
    }

    // //#region Encoder
    // private double getEncoderAngleDegrees() {
    //     double rotations = encoder.getPosition();
    //     return rotations * DegreesPerMotorRevolution;
    // }
    // //#endregion

    //#region Commands
    private CommandBase getGotoTargetCommand() {
        return run(() -> {
            pidController.setReference(rotations, ControlType.kPosition, 0);
        })
            .withName("Goto Target");
    }
    //#endregion

    //#region Public
    public WristState getState() {
        return state;
    }
    
    public void setState(WristState state) {
        this.state = state;

        rotations = state.getRotations();
    }
    //#endregion
}