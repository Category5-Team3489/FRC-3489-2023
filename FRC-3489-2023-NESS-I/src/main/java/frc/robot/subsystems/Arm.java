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
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Cat5Inputs;
import frc.robot.Cat5Utils;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.Constants.ArmConstants;
import frc.robot.enums.ArmCommand;
import frc.robot.enums.GamePiece;
import frc.robot.enums.GridPosition;
import frc.robot.enums.LedPattern;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.ArmConstants.*;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class Arm extends Cat5Subsystem<Arm> {
    //#region Singleton
    private static Arm instance = new Arm();

    public static Arm get() {
        return instance;
    }
    //#endregion

    // Devices
    private final CANSparkMax motor = new CANSparkMax(MotorDeviceId, MotorType.kBrushless);
    private final DigitalInput limitSwitch = new DigitalInput(LimitSwitchChannel);
    private final SparkMaxPIDController pidController;
    private final RelativeEncoder encoder;

    // Suppliers
    private final BooleanSupplier isManualControlEnabled;
    private final BooleanSupplier debugIsTrackingTarget;
    private final DoubleSupplier debugTargetAngleDegrees;

    // Commands
    private final CommandBase gotoHomeCommand = getGotoHomeCommand();
    private final CommandBase gotoTargetCommand = getGotoTargetCommand();
    private final CommandBase manualControlCommand = getManualControlCommand();

    // State
    private boolean isHomed = false;
    private double targetAngleDegrees = MinAngleDegrees;
    private GridPosition gridPosition = GridPosition.Low;
    private IdleMode idleMode = IdleMode.kCoast;
    private boolean lastLimitSwitchValue = false;
    private ArmCommand activeCommand = ArmCommand.None;

    private Arm() {
        super(i -> instance = i);

        setDefaultCommand(gotoHomeCommand);

        //#region Devices
        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(idleMode);
        motor.enableVoltageCompensation(12.0);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        motor.setClosedLoopRampRate(ClosedLoopSecondsToFull);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 20); // TODO Should this be done in other places or with other frames?
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor
        //#endregion

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);
        
        layout.add("Subsystem Info", this);

        layout.addBoolean("Is Homed", () -> isHomed);
        layout.addDouble("Encoder Angle (deg)", () -> getEncoderAngleDegrees());
        layout.addDouble("Target Angle (deg)", () -> targetAngleDegrees);

        layout.addString("Grid Position", () -> gridPosition.toString());
        layout.addString("Idle Mode", () -> idleMode.toString());

        layout.addBoolean("Limit Switch", () -> limitSwitch.get());

        layout.addString("Active Command", () -> activeCommand.toString());

        var subsystemLayout = getLayout(Cat5ShuffleboardTab.Arm, BuiltInLayouts.kList)
            .withSize(2, 1);

        var isManualControlEnabledEntry = layout.add("Enable Manual Control", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isManualControlEnabled = () -> isManualControlEnabledEntry.getBoolean(false);

        var debugIsTrackingTargetEntry = subsystemLayout.add("Debug Track Target", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        debugIsTrackingTarget = () -> debugIsTrackingTargetEntry.getBoolean(false);

        var debugTargetAngleDegreesEntry = subsystemLayout.add("Debug Target Angle (deg)", MinAngleDegrees)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", MinAngleDegrees, "max", MaxAngleDegrees, "block increment", 1.0))
            .getEntry();
        debugTargetAngleDegrees = () -> debugTargetAngleDegreesEntry.getDouble(MinAngleDegrees);

        if (Constants.IsDebugShuffleboardEnabled) {
    
            layout.add("Force Home", Commands.runOnce(() -> {
                command(ArmCommand.ForceHome);
            })
                .withName("Force Home")
            );
    
            subsystemLayout.addDouble("Motor Applied Output (V)", () -> motor.getAppliedOutput());
            subsystemLayout.addDouble("Motor Temperature (deg F)", () -> (motor.getMotorTemperature() * (9.0 / 5.0)) + 32);
        }
        //#endregion
    }

    @Override
    public void periodic() {
        if (pollLimitSwitchRisingEdge()) {
            notifyLimitSwitchRisingEdge();
        }

        if (!isHomed && limitSwitch.get() && lastLimitSwitchValue) {
            setEncoderAngleDegrees(MinAngleDegrees);
            isHomed = true;
        }

        if (isManualControlEnabled.getAsBoolean()) {
            manualControlCommand.schedule();
        }
        else {
            if (isHomed) {
                if (debugIsTrackingTarget.getAsBoolean()) {
                    setTargetAngleDegrees(GridPosition.Mid, debugTargetAngleDegrees.getAsDouble(), IdleMode.kBrake);
                }

                double correctionPercent = Cat5Inputs.getArmCorrectionPercent();
                targetAngleDegrees += correctionPercent * CorrectionMaxDegreesPerSecond * Robot.kDefaultPeriod;
                targetAngleDegrees = MathUtil.clamp(targetAngleDegrees, MinAngleDegrees, MaxAngleDegrees);

                gotoTargetCommand.schedule();
            }
            else {
                gotoHomeCommand.schedule();
            }
        }
    }

    //#region Control
    private void setTargetAngleDegrees(GridPosition gridPosition, double angleDegrees, IdleMode idleMode) {
        this.gridPosition = gridPosition;
        angleDegrees = MathUtil.clamp(angleDegrees, MinAngleDegrees, MaxAngleDegrees);
        targetAngleDegrees = angleDegrees;

        if (this.idleMode != idleMode) {
            this.idleMode = idleMode;
            motor.setIdleMode(idleMode);
        }
    }
    //#endregion

    //#region Encoder
    private double getEncoderAngleDegrees() {
        double rotations = encoder.getPosition();
        return rotations * DegreesPerMotorRevolution;
    }

    private void setEncoderAngleDegrees(double angleDegrees) {
        double rotations = angleDegrees * MotorRevolutionsPerDegree;
        encoder.setPosition(rotations);
    }
    //#endregion

    //#region Feedforward
    private double getResistConstantForceSpringPercent() {
        if (!isHomed) {
            return 0;
        }

        return 0; // Math.cos(Math.toRadians(getEncoderAngleDegrees()) / 2);
    }

    private double getResistGravityPercent() {
        if (!isHomed) {
            return 0;
        }

        double angleRadians = Math.toRadians(getEncoderAngleDegrees());
        return Math.cos(angleRadians) * HorizontalResistGravityPercent;
    }

    private double getResistStaticFrictionPercent(double direction) {
        return direction * ResistStaticFrictionPercent;
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
            double targetRevolutions = targetAngleDegrees * MotorRevolutionsPerDegree;
            double direction = encoder.getVelocity() > 0 ? 1 : -1;
            double arbFeedforward = getResistConstantForceSpringPercent() + getResistGravityPercent() + getResistStaticFrictionPercent(direction);
            pidController.setReference(targetRevolutions, ControlType.kPosition, 0, arbFeedforward * 12.0, ArbFFUnits.kVoltage);
        })
            .withName("Goto Target");
    }
    
    private CommandBase getManualControlCommand() {
        return run(() -> {
            double percent = Cat5Inputs.getArmManualControlPercent();

            if (percent < 0) {
                percent = MathUtil.interpolate(0, ArmConstants.ManualControlMaxDownPercent, -percent);

                if (getEncoderAngleDegrees() < ArmConstants.ManualControlMinAngleDegrees) {
                    percent = 0;
                }
            }
            else {
                percent = MathUtil.interpolate(0, ArmConstants.ManualControlMaxUpPercent, percent);

                if (getEncoderAngleDegrees() > ArmConstants.MaxAngleDegrees) {
                    percent = 0;
                }
            }

            motor.setVoltage(percent * 12.0);
        })
            .withName("Manual Control");
    }
    //#endregion

    //#region Public
    public void command(ArmCommand command) {
        Cat5Utils.time();
        System.out.println("Enter arm command: " + activeCommand.toString());

        ArmCommand lastCommand = activeCommand;
        activeCommand = command;

        GamePiece heldGamePiece = Gripper.get().getHeldGamePiece();

        if ((lastCommand == ArmCommand.Home || lastCommand == ArmCommand.ForceHome) && (activeCommand != ArmCommand.Home && activeCommand != ArmCommand.ForceHome)) {
            Gripper.get().scheduleUnstowPieceCommand();
        }
        
        switch (command) {
            case None:
				break;
            case ForceHome:
                isHomed = false;
                lastLimitSwitchValue = false;
                setTargetAngleDegrees(GridPosition.Low, MinAngleDegrees, IdleMode.kCoast);
				break;
            case Home:
                if (lastCommand == ArmCommand.Home) {
                    isHomed = false;
                    lastLimitSwitchValue = false;
                    setTargetAngleDegrees(GridPosition.Low, MinAngleDegrees, IdleMode.kCoast);

                    activeCommand = ArmCommand.ForceHome;
                }
                else {
                    setTargetAngleDegrees(GridPosition.Low, ArmConstants.MinAngleDegrees, IdleMode.kCoast);
                }
				break;
            case Floor:
                setTargetAngleDegrees(GridPosition.Low, ArmConstants.FloorAngleDegrees, IdleMode.kBrake);
				break;
            case Low:
                switch (heldGamePiece) {
                    case Cone:
                        setTargetAngleDegrees(GridPosition.Low, ArmConstants.LowConeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Cube:
                        setTargetAngleDegrees(GridPosition.Low, ArmConstants.LowCubeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Unknown:
                        setTargetAngleDegrees(GridPosition.Low, ArmConstants.LowUnknownAngleDegrees, IdleMode.kBrake);
                        break;
                }
				break;
			case Mid:
                switch (heldGamePiece) {
                    case Cone:
                        if (lastCommand == ArmCommand.Mid) {
                            setTargetAngleDegrees(GridPosition.Mid, ArmConstants.ScoreMidConeAngleDegrees, IdleMode.kBrake);

                            activeCommand = ArmCommand.ScoreMidCone;
                        }
                        else {
                            setTargetAngleDegrees(GridPosition.Mid, ArmConstants.MidConeAngleDegrees, IdleMode.kBrake);
                        }
                        break;
                    case Cube:
                        setTargetAngleDegrees(GridPosition.Mid, ArmConstants.MidCubeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Unknown:
                        setTargetAngleDegrees(GridPosition.Mid, ArmConstants.MidUnknownAngleDegrees, IdleMode.kBrake);
                        break;
                }
				break;
            case ScoreMidCone:
                if (lastCommand == ArmCommand.ScoreMidCone) {
                    setTargetAngleDegrees(GridPosition.Mid, ArmConstants.MidConeAngleDegrees, IdleMode.kBrake);

                    activeCommand = ArmCommand.ScoreMidCone;
                }
                else {
                    setTargetAngleDegrees(GridPosition.Mid, ArmConstants.ScoreMidConeAngleDegrees, IdleMode.kBrake);
                }
				break;
            case High:
                switch (heldGamePiece) {
                    case Cone:
                        setTargetAngleDegrees(GridPosition.High, ArmConstants.HighConeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Cube:
                        setTargetAngleDegrees(GridPosition.High, ArmConstants.HighCubeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Unknown:
                        setTargetAngleDegrees(GridPosition.High, ArmConstants.HighUnknownAngleDegrees, IdleMode.kBrake);
                        break;
                }
				break;
			case DoubleSubstation:
                setTargetAngleDegrees(GridPosition.High, ArmConstants.DoubleSubstationDegrees, IdleMode.kBrake);
				break;
        }

        Cat5Utils.time();
        System.out.println("Exit arm command: " + activeCommand.toString());
    }

    private boolean pollLimitSwitchRisingEdge() {
        if (limitSwitch.get() && !lastLimitSwitchValue) {
            lastLimitSwitchValue = true;

            return true;
        }

        lastLimitSwitchValue = limitSwitch.get();

        return false;
    }

    private void notifyLimitSwitchRisingEdge() {
        // if (isHomed) {
        //     return;
        // }
        if (!isHomed) {
            setEncoderAngleDegrees(MinAngleDegrees);
            isHomed = true;

            Cat5Utils.time();
            System.out.println("Arm limit switch rising edge, homed");
            Leds.get().getCommand(LedPattern.StrobeBlue, 0.5, true)
                .schedule();
        }

        setTargetAngleDegrees(GridPosition.Low, ArmConstants.MinAngleDegrees, IdleMode.kBrake);
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public double getTargetAngleDegrees() {
        return targetAngleDegrees;
    }
    //#endregion
}