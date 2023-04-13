package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.enums.ArmState;
import frc.robot.enums.GridPosition;

public class Arm extends Cat5Subsystem {
    // Constants
    private static final double CorrectionMaxDegreesPerSecond = 25;
    
    private static final double MotorRevolutionsPerRevolution = 64.0 * (64.0 / 12.0);
    private static final double MotorRevolutionsPerDegree = MotorRevolutionsPerRevolution / 360.0;
    private static final double DegreesPerMotorRevolution = 1.0 / MotorRevolutionsPerDegree;

    public static final double MinAngleDegrees = -114;
    public static final double MaxAngleDegrees = 37.0;

    private static final double HomingPercent = -0.40;
    private static final double HorizontalResistGravityPercent = 0.025;

    private static final int StallSmartCurrentLimitAmps = 30;
    private static final double ClosedLoopSecondsToFull = 0.2;
    private static final double ProportionalGainPercentPerRevolutionOfError = 0.1;
    private static final double MinOutputPercent = -0.8;
    private static final double MaxOutputPercent = 0.8;
    
    private static final int MotorDeviceId = 11;
    private static final int LimitSwitchChannel = 1;

    // Devices
    private final CANSparkMax motor = new CANSparkMax(MotorDeviceId, MotorType.kBrushless);
    private final DigitalInput limitSwitch = new DigitalInput(LimitSwitchChannel);
    private final SparkMaxPIDController pidController;
    private final RelativeEncoder encoder;

    // Commands
    private final CommandBase gotoHomeCommand = getGotoHomeCommand();
    private final CommandBase gotoTargetCommand = getGotoTargetCommand();

    // State
    private boolean isHomed = false;
    private boolean lastLimitSwitchValue = false;
    private GridPosition gridPosition = GridPosition.Low;
    private double targetAngleDegrees = MinAngleDegrees;
    private IdleMode idleMode = IdleMode.kCoast;
    private ArmState state = ArmState.Homing;

    public Arm(RobotContainer robotContainer) {
        super(robotContainer);

        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(idleMode);
        motor.enableVoltageCompensation(12.0);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        motor.setClosedLoopRampRate(ClosedLoopSecondsToFull);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus3, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus4, 50);
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor
    }

    @Override
    public void periodic() {
        if (isLimitSwitchRisingEdge()) {
            if (!isHomed) {
                setEncoderAngleDegrees(MinAngleDegrees);
                isHomed = true;

                // TODO Log that homed, leds too
            }
    
            setState(ArmState.Home);
        }

        if (isHomed) {
            double correctionPercent = robotContainer.input.getArmCorrectionPercent();
            targetAngleDegrees += correctionPercent * CorrectionMaxDegreesPerSecond * Robot.kDefaultPeriod;
            targetAngleDegrees = MathUtil.clamp(targetAngleDegrees, MinAngleDegrees, MaxAngleDegrees);

            gotoTargetCommand.schedule();
        }
        else {
            gotoHomeCommand.schedule();
        }
    }

    private boolean isLimitSwitchRisingEdge() {
        if (limitSwitch.get() && !lastLimitSwitchValue) {
            lastLimitSwitchValue = true;
            return true;
        }

        lastLimitSwitchValue = limitSwitch.get();
        return false;
    }

    public void setState(ArmState state) {
        this.state = state;

        gridPosition = state.getGridPosition();
        targetAngleDegrees = MathUtil.clamp(state.getDegrees(), MinAngleDegrees, MaxAngleDegrees);
        if (idleMode != state.getIdleMode()) {
            idleMode = state.getIdleMode();
            motor.setIdleMode(idleMode);
        }
    }

    private double getEncoderAngleDegrees() {
        double rotations = encoder.getPosition();
        return rotations * DegreesPerMotorRevolution;
    }

    private void setEncoderAngleDegrees(double angleDegrees) {
        double rotations = angleDegrees * MotorRevolutionsPerDegree;
        encoder.setPosition(rotations);
    }

    private double getResistGravityPercent() {
        if (!isHomed) {
            return 0;
        }

        double angleRadians = Math.toRadians(getEncoderAngleDegrees());
        return Math.cos(angleRadians) * HorizontalResistGravityPercent;
    }

    private CommandBase getGotoHomeCommand() {
        return run(() -> {
            if (!isHomed) {
                motor.setVoltage(HomingPercent * 12.0);
            }
        })
            .withName("Goto Home");
    }

    private CommandBase getGotoTargetCommand() {
        return run(() -> {
            double targetRevolutions = targetAngleDegrees * MotorRevolutionsPerDegree;
            double arbFeedforward = getResistGravityPercent();
            pidController.setReference(targetRevolutions, ControlType.kPosition, 0, arbFeedforward * 12.0, ArbFFUnits.kVoltage);
        })
            .withName("Goto Target");
    }

    public void forceHome() {
        lastLimitSwitchValue = false;
        isHomed = false;
    }

    public ArmState getState() {
        return state;
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public double getTargetAngleDegrees() {
        return targetAngleDegrees;
    }
}
