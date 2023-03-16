package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMaxLowLevel.PeriodicFrame;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Cat5Utils;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.enums.GridPosition;
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

    // State
    private boolean isHomed = false;
    private double targetAngleDegrees = MinAngleDegrees;
    private GridPosition gridPosition = GridPosition.Low;
    private IdleMode idleMode = IdleMode.kCoast;

    private Arm() {
        super(i -> instance = i);

        //#region Devices
        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(idleMode);
        motor.enableVoltageCompensation(12.0);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        motor.setPeriodicFramePeriod(PeriodicFrame.kStatus0, 20);
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setD(DerivativeGainPercentPerRevolutionPerMillisecondOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor
        //#endregion

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);
        
        layout.add("Subsystem Info", this);

        layout.addBoolean("Is Homed", () -> isHomed);
        layout.addDouble("Encoder Arm Angle (deg)", () -> getEncoderAngleDegrees());
        layout.addDouble("Target Arm Angle (deg)", () -> targetAngleDegrees);

        layout.addString("Grid Position", () -> gridPosition.toString());
        layout.addString("Idle Mode", () -> idleMode.toString());

        layout.addBoolean("Limit Switch", () -> limitSwitch.get());

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
                isHomed = false;
            })
                .withName("Force Home")
            );
    
            // subsystemLayout.addDouble("Motor Applied Output (V)", () -> motor.getAppliedOutput());
            // subsystemLayout.addDouble("Motor Temperature (deg F)", () -> (motor.getMotorTemperature() * (9.0 / 5.0)) + 32);
        }
        //#endregion
    }

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

        return 0; // Math.cos(Math.toRadians(MatgetEncoderAngleDegrees()) / 2);
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
    private CommandBase getManualControlCommand() {
        return run(() -> {
            double percent = RobotContainer.get().getArmManualControlPercent();

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
    public void setTargetAngleDegrees(GridPosition gridPosition, double angleDegrees, IdleMode idleMode) {
        this.gridPosition = gridPosition;
        angleDegrees = MathUtil.clamp(angleDegrees, MinAngleDegrees, MaxAngleDegrees);
        targetAngleDegrees = angleDegrees;

        if (this.idleMode != idleMode) {
            this.idleMode = idleMode;
            motor.setIdleMode(idleMode);
        }
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }
    //#endregion
}