package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Input;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.Constants.WristConstants.WristState;
import frc.robot.data.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.enums.GridPosition;

import static frc.robot.Constants.WristConstants.*;

public class Wrist extends Cat5Subsystem{
    
    // Devices
    private final CANSparkMax motor = new CANSparkMax(MotorDeviceId, MotorType.kBrushless); // Negative up, positive down
    private final SparkMaxPIDController pidController;
    private final RelativeEncoder encoder;
    
    // Commands
    private final CommandBase gotoTargetCommand = getGotoTargetCommand();

    // State
    private final Indicator indicator;
    private WristState state = WristState.Start;
    private double rotations = WristState.Start.getRotations();

    private Wrist(RobotContainer robotContainer, Indicator indicator) {
        super(robotContainer);
        this.indicator = indicator;

        setDefaultCommand(gotoTargetCommand);

        //#region Devices
        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kBrake);
        motor.enableVoltageCompensation(12.0);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 50);//20 // TODO Should this be done in other places or with other frames?
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus3, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus4, 50);
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor
        //#endregion
    }

    @Override
    public void periodic() {
        double correctionPercent = robotContainer.input.getWristCorrectionPercent();
        rotations -= correctionPercent * 7.5 * Robot.kDefaultPeriod;
        if (Arm.get().getGridPosition() == GridPosition.High || Arm.get().getGridPosition() == GridPosition.Mid) {
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
