package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Cat5;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.data.Cat5DeltaTracker;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;
import frc.robot.enums.GridPosition;
import frc.robot.enums.WristState;

public class Wrist extends Cat5Subsystem {
    // Constants
    private static final double MotorRevolutionsPerRevolution = (100.0 / 1.0) * (2.0 / 1.0);
    private static final double MotorRevolutionsPerDegree = MotorRevolutionsPerRevolution / 360.0;
    public static final double DegreesPerMotorRevolution = 1.0 / MotorRevolutionsPerDegree;

    private static final double CorrectionMultiplier = 7.5;

    private static final int StallSmartCurrentLimitAmps = 20;
    private static final double ProportionalGainPercentPerRevolutionOfError = 0.5;
    private static final double MinOutputPercent = -0.30;
    private static final double MaxOutputPercent = 0.30;

    private static final int MotorDeviceId = 12;
    
    // Devices
    private final CANSparkMax motor = new CANSparkMax(MotorDeviceId, MotorType.kBrushless); // Negative up, positive down
    private final SparkMaxPIDController pidController;
    private final RelativeEncoder encoder;

    // State
    private WristState state = WristState.Home;
    private double targetDegrees = state.getDegrees();

    public Wrist(RobotContainer robotContainer) {
        super(robotContainer);

        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kBrake);
        motor.enableVoltageCompensation(12.0);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus1, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus2, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus3, 50);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus4, 50);
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor

        GenericEntry stateEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Manipulator)
            .add("Wrist State", state.toString())
            .getEntry();
        StringLogEntry stateLogEntry = new StringLogEntry(robotContainer.dataLog, "/wrist/state");
        robotContainer.data.createDatapoint(() -> state.toString())
            .withShuffleboardUpdater(data -> {
                stateEntry.setString(data);
            })
            .withShuffleboardHz(4)
            .withLogUpdater(data -> {
                stateLogEntry.append(data);
            });

        GenericEntry targetDegreesEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Manipulator)
            .add("Wrist Target Degrees", targetDegrees)
            .getEntry();
        DoubleLogEntry targetDegreesLogEntry = new DoubleLogEntry(robotContainer.dataLog, "/wrist/target-degrees");
        robotContainer.data.createDatapoint(() -> targetDegrees)
            .withShuffleboardUpdater(data -> {
                targetDegreesEntry.setDouble(data);
            })
            .withShuffleboardHz(4)
            .withLogUpdater(data -> {
                targetDegreesLogEntry.append(data);
            });

        GenericEntry encoderDegreesEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Manipulator)
            .add("Wrist Encoder Degrees", getEncoderAngleDegrees())
            .getEntry();
        DoubleLogEntry encoderDegreesLogEntry = new DoubleLogEntry(robotContainer.dataLog, "/wrist/encoder-degrees");
        robotContainer.data.createDatapoint(() -> getEncoderAngleDegrees())
            .withShuffleboardUpdater(data -> {
                encoderDegreesEntry.setDouble(data);
            })
            .withShuffleboardHz(4)
            .withLogUpdater(data -> {
                encoderDegreesLogEntry.append(data);
            });

        new Cat5DeltaTracker<WristState>(robotContainer, state,
        last -> {
            return last != state;
        }, last -> {
            Cat5.print("Wrist state: " + last.toString() + " -> " + state.toString());
            return state;
        });
    }

    @Override
    public void periodic() {
        if (DriverStation.isTeleopEnabled()) {
            double correctionPercent = robotContainer.input.getWristCorrectionPercent();
            targetDegrees -= correctionPercent * CorrectionMultiplier * DegreesPerMotorRevolution * Robot.kDefaultPeriod;
            if (robotContainer.getGridPosition() == GridPosition.Mid || robotContainer.getGridPosition() == GridPosition.High) {
                targetDegrees = MathUtil.clamp(targetDegrees, WristState.HighestWithHighArm.getDegrees(), WristState.Lowest.getDegrees());
            }
            else {
                targetDegrees = MathUtil.clamp(targetDegrees, WristState.Carry.getDegrees(), WristState.Lowest.getDegrees());
            }
        }

        if (DriverStation.isEnabled()) {
            pidController.setReference(targetDegrees * MotorRevolutionsPerDegree, ControlType.kPosition, 0);
        }
    }

    private double getEncoderAngleDegrees() {
        double rotations = encoder.getPosition();
        return rotations * DegreesPerMotorRevolution;
    }

    public void setState(WristState state) {
        this.state = state;
        targetDegrees = state.getDegrees();
    }
}
