package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Cat5Utils;
import frc.robot.GamePiece;
import frc.robot.RobotContainer;
import frc.robot.Constants.ArmConstants;
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
    private double targetAngleDegrees = ArmConstants.MinAngleDegrees;

    private Arm() {
        super((i) -> instance = i);

        // Remember: NEO position units are rotations
        // Zero degrees is horizontal + is up, - down
        // https://docs.revrobotics.com/sparkmax/operating-modes/closed-loop-control
        // https://docs.revrobotics.com/sparkmax/operating-modes/control-interfaces
        // TODO Figure out how to get resist constant force spring feedforward
        // TODO Figure out how to set IdleMode for constant force spring to work properly
        // TODO ^^^ Remember, motor.getIdleMode and motor.setIdleMode should be called infrequently
        // TODO ^^^ Cache those in here somewhere, so current value is always known

        //#region Devices
        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kBrake);
        motor.enableVoltageCompensation(12.0);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setD(DerivativeGainPercentPerRevolutionPerMillisecondOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor
        //#endregion

        setDefaultCommand(gotoHomeCommand);

        //#region Bindings
        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(Commands.runOnce(() -> {
                setTargetAngleDegrees(ArmConstants.MinAngleDegrees);
            }));

        RobotContainer.get().man.button(ArmConstants.HomeManButton)
            .onTrue(Commands.runOnce(() -> {
                setTargetAngleDegrees(MinAngleDegrees);
            }));
        
        RobotContainer.get().man.button(ArmConstants.LowManButton)
            .onTrue(Commands.runOnce(() -> {
                GamePiece heldGamePiece = Gripper.get().getHeldGamePiece();
                switch (heldGamePiece) {
                    case Cone:
                        setTargetAngleDegrees(LowConeAngleDegrees);
                        break;
                    case Cube:
                        setTargetAngleDegrees(LowCubeAngleDegrees);
                        break;
                    case Unknown:
                        setTargetAngleDegrees(LowUnknownAngleDegrees);
                        break;
                }
            }));

        RobotContainer.get().man.button(ArmConstants.MidManButton)
            .onTrue(Commands.runOnce(() -> {
                GamePiece heldGamePiece = Gripper.get().getHeldGamePiece();
                switch (heldGamePiece) {
                    case Cone:
                        setTargetAngleDegrees(MidConeAngleDegrees);
                        break;
                    case Cube:
                        setTargetAngleDegrees(MidCubeAngleDegrees);
                        break;
                    case Unknown:
                        setTargetAngleDegrees(MidUnknownAngleDegrees);
                        break;
                }
            }));

        RobotContainer.get().man.button(ArmConstants.HighManButton)
            .onTrue(Commands.runOnce(() -> {
                GamePiece heldGamePiece = Gripper.get().getHeldGamePiece();
                switch (heldGamePiece) {
                    case Cone:
                        setTargetAngleDegrees(HighConeAngleDegrees);
                        break;
                    case Cube:
                        setTargetAngleDegrees(HighCubeAngleDegrees);
                        break;
                    case Unknown:
                        setTargetAngleDegrees(HighUnknownAngleDegrees);
                        break;
                }
            }));
        //#endregion

        //#region Shuffleboard
        // Main
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
        .withSize(2, 3);

        layout.add("Subsystem Info", this);

        layout.addBoolean("Is Homed", () -> isHomed);
        layout.addDouble("Encoder Arm Angle (°)", () -> getEncoderAngleDegrees());
        layout.addDouble("Target Arm Angle (°)", () -> targetAngleDegrees);

        layout.addBoolean("Limit Switch", () -> limitSwitch.get());

        layout.add("Force Home", Commands.runOnce(() -> {
            isHomed = false;
        })
            .withName("Force Home")
        );

        var isManualControlEnabledEntry = layout.add("Enable Manual Control", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isManualControlEnabled = () -> isManualControlEnabledEntry.getBoolean(false);

        // Subsystem
        var subsystemLayout = getLayout(Cat5ShuffleboardTab.Arm, BuiltInLayouts.kList)
            .withSize(2, 3);

        subsystemLayout.addDouble("Motor Applied Output (V)", () -> motor.getAppliedOutput());
        subsystemLayout.addDouble("Motor Temperature (°F)", () -> (motor.getMotorTemperature() * (9.0 / 5.0)) + 32);

        var debugIsTrackingTargetEntry = subsystemLayout.add("Debug Track Target", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        debugIsTrackingTarget = () -> debugIsTrackingTargetEntry.getBoolean(false);

        var debugTargetAngleDegreesEntry = subsystemLayout.add("Debug Target Angle (°)", MinAngleDegrees)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", MinAngleDegrees, "max", MaxAngleDegrees, "block increment", 1.0))
            .getEntry();
        debugTargetAngleDegrees = () -> debugTargetAngleDegreesEntry.getDouble(MinAngleDegrees);
        //#endregion
    }

    @Override
    public void periodic() {
        if (limitSwitch.get()) {
            isHomed = true;
            setEncoderAngleDegrees(MinAngleDegrees);
        }

        if (isManualControlEnabled.getAsBoolean()) {
            manualControlCommand.schedule();
        }
        else {
            if (isHomed) {
                if (debugIsTrackingTarget.getAsBoolean()) {
                    setTargetAngleDegrees(debugTargetAngleDegrees.getAsDouble());
                }

                gotoTargetCommand.schedule();
            }
            else {
                gotoHomeCommand.schedule();
            }
        }
    }

    //#region Control
    private void setTargetAngleDegrees(double angleDegrees) {
        angleDegrees = MathUtil.clamp(angleDegrees, MinAngleDegrees, MaxAngleDegrees);
        targetAngleDegrees = angleDegrees;
    }
    //#endregion Control

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
        return Commands.run(() -> {
            if (isHomed) {
                return;
            }
            
            motor.setVoltage(HomingPercent * 12.0);
        }, this)
            .withName("Goto Home");
    }
    private CommandBase getGotoTargetCommand() {
        return Commands.run(() -> {
            double targetRevolutions = targetAngleDegrees * MotorRevolutionsPerDegree;
            double direction = encoder.getVelocity() > 0 ? 1 : -1;
            double arbFeedforward = getResistGravityPercent() + getResistStaticFrictionPercent(direction);
            pidController.setReference(targetRevolutions, ControlType.kPosition, 0, arbFeedforward * 12.0, ArbFFUnits.kVoltage);
        }, this)
            .withName("Goto Target");
    }
    private CommandBase getManualControlCommand() {
        return Commands.run(() -> {
            double y = -RobotContainer.get().man.getY();
            y = Cat5Utils.linearAxis(y, 0.1);

            if (y < 0) {
                y = MathUtil.interpolate(0, ArmConstants.ManualControlMaxDownPercent, -y);
            }
            else {
                y = MathUtil.interpolate(0, ArmConstants.ManualControlMaxUpPercent, y);
            }

            motor.setVoltage(y * 12.0);
        }, this)
            .withName("Manual Control");
    }
    //#endregion
}
