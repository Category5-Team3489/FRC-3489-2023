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
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.data.Cat5DeltaTracker;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;
import frc.robot.enums.ArmState;
import frc.robot.enums.GridPosition;

public class Arm extends Cat5Subsystem {
    // Constants
    private static final double AroundTargetThresholdDegrees = 5;
    private static final double CorrectionMaxDegreesPerSecond = 25;
    
    private static final double MotorRevolutionsPerRevolution = 64.0 * (64.0 / 12.0);
    private static final double MotorRevolutionsPerDegree = MotorRevolutionsPerRevolution / 360.0;
    private static final double DegreesPerMotorRevolution = 1.0 / MotorRevolutionsPerDegree;

    public static final double MinDegrees = -114;
    public static final double MaxDegrees = 37.0;

    private static final double HomingPercent = -0.8; // TODO TEST THIS SPEED INCREASE -0.4
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
    private double targetDegrees = MinDegrees;
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

        GenericEntry limitSwitchEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Manipulator)
            .add("Arm Limit Switch", limitSwitch.get())
            .getEntry();
        BooleanLogEntry limitSwitchLogEntry = new BooleanLogEntry(robotContainer.dataLog, "/arm/limit-switch");
        robotContainer.data.createDatapoint(() -> limitSwitch.get())
            .withShuffleboard(data -> {
                limitSwitchEntry.setBoolean(data);
            }, 25)
            .withLog(data -> {
                limitSwitchLogEntry.append(data);
            }, 25);
        
        BooleanLogEntry isHomedLogEntry = new BooleanLogEntry(robotContainer.dataLog, "/arm/is-homed");
        robotContainer.data.createDatapoint(() -> isHomed)
            .withLog(data -> {
                isHomedLogEntry.append(data);
            }, 5);

        GenericEntry stateEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Manipulator)
            .add("Arm State", state.toString())
            .getEntry();
        StringLogEntry stateLogEntry = new StringLogEntry(robotContainer.dataLog, "/arm/state");
        robotContainer.data.createDatapoint(() -> state.toString())
            .withShuffleboard(data -> {
                stateEntry.setString(data);
            }, 5)
            .withLog(data -> {
                stateLogEntry.append(data);
            }, 5);

        DoubleLogEntry targetDegreesLogEntry = new DoubleLogEntry(robotContainer.dataLog, "/arm/target-degrees");
        robotContainer.data.createDatapoint(() -> targetDegrees)
            .withLog(data -> {
                targetDegreesLogEntry.append(data);
            }, 25);

        DoubleLogEntry encoderDegreesLogEntry = new DoubleLogEntry(robotContainer.dataLog, "/arm/encoder-degrees");
        robotContainer.data.createDatapoint(() -> getEncoderDegrees())
            .withLog(data -> {
                encoderDegreesLogEntry.append(data);
            }, 25);

        new Cat5DeltaTracker<ArmState>(robotContainer, state,
        last -> {
            return last != state;
        }, last -> {
            Cat5.print("Arm state: " + last.toString() + " -> " + state.toString());
            return state;
        });
        new Cat5DeltaTracker<GridPosition>(robotContainer, gridPosition,
        last -> {
            return last != gridPosition;
        }, last -> {
            Cat5.print("Grid position: " + last.toString() + " -> " + gridPosition.toString());
            return gridPosition;
        });
        new Cat5DeltaTracker<IdleMode>(robotContainer, idleMode,
        last -> {
            return last != idleMode;
        }, last -> {
            Cat5.print("Arm idle mode: " + last.toString() + " -> " + idleMode.toString());
            return idleMode;
        });
        new Cat5DeltaTracker<Command>(robotContainer, getCurrentCommand(),
        last -> {
            return last != getCurrentCommand();
        }, last -> {
            String lastString = last == null ? "None" : last.getName();
            Command currentCommand = getCurrentCommand();
            String currentString = currentCommand == null ? "None" : currentCommand.getName();
            Cat5.print("Arm current command: " + lastString + " -> " + currentString);
            return currentCommand;
        });
    }

    @Override
    public void periodic() {
        if (isLimitSwitchRisingEdge()) {
            if (!isHomed) {
                setEncoderAngleDegrees(MinDegrees);
                isHomed = true;

                setState(ArmState.Home);
                Cat5.print("Arm now homed!");
            }

            Cat5.print("Arm limit switch rising edge!");
        }

        if (isHomed) {
            double correctionPercent = robotContainer.input.getArmCorrectionPercent();
            targetDegrees += correctionPercent * CorrectionMaxDegreesPerSecond * Robot.kDefaultPeriod;
            targetDegrees = MathUtil.clamp(targetDegrees, MinDegrees, MaxDegrees);

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
        targetDegrees = MathUtil.clamp(state.getDegrees(), MinDegrees, MaxDegrees);
        if (idleMode != state.getIdleMode()) {
            idleMode = state.getIdleMode();
            motor.setIdleMode(idleMode);
        }
    }

    private double getEncoderDegrees() {
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

        double angleRadians = Math.toRadians(getEncoderDegrees());
        return Math.cos(angleRadians) * HorizontalResistGravityPercent;
    }

    private CommandBase getGotoHomeCommand() {
        return run(() -> {
            if (!isHomed) {
                setState(ArmState.Homing);

                motor.setVoltage(HomingPercent * 12.0);
            }
        })
            .withName("Goto Home");
    }

    private CommandBase getGotoTargetCommand() {
        return run(() -> {
            double targetRevolutions = targetDegrees * MotorRevolutionsPerDegree;
            double arbFeedforward = getResistGravityPercent();
            pidController.setReference(targetRevolutions, ControlType.kPosition, 0, arbFeedforward * 12.0, ArbFFUnits.kVoltage);
        })
            .withName("Goto Target");
    }

    public void forceHome() {
        lastLimitSwitchValue = false;
        isHomed = false;

        setState(ArmState.Homing);

        Cat5.print("Arm force homing...");
    }

    public ArmState getState() {
        return state;
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public double getTargetDegrees() {
        return targetDegrees;
    }

    public boolean isAroundTarget() {
        return Math.abs(getEncoderDegrees() - targetDegrees) < AroundTargetThresholdDegrees;
    }
}
