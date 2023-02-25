package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Cat5Subsystem;
import frc.robot.Cat5Utils;
import frc.robot.RobotContainer;
import frc.robot.Constants.ArmConstants;
import frc.robot.commands.arm.GotoHome;
import frc.robot.commands.arm.GotoTarget;
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
    private BooleanSupplier isManualControlEnabled;
    private DoubleSupplier targetAngleDegrees;
    private BooleanSupplier isTrackingTarget;

    // Commands
    private final GotoTarget gotoTarget = new GotoTarget();

    // State
    private boolean isHomed = false;

    private Arm() {
        super((i) -> instance = i);

        // Remember: NEO position units are rotations
        // https://docs.revrobotics.com/sparkmax/operating-modes/closed-loop-control
        // Zero degrees is horizontal + is up, - down
        // TODO Could add timer with time that arm has been stalled for using velocity and motor being set to something, get amps, maybe abs(degrees)/watt past 10 sec, if that is low then that is bad
        // https://docs.revrobotics.com/sparkmax/operating-modes/control-interfaces
        // encoder.setPositionConversionFactor() // TODO THIS IS USEFUL
        // encoder.setVelocityConversionFactor() // TODO THIS IS USEFUL

        setDefaultCommand(new GotoHome());

        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kBrake); // FIXME was brake
        motor.enableVoltageCompensation(12);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        // motor.setOpenLoopRampRate(0.5); // TODO COULD BE VERY USEFUL!!!!!!!!, WOULD STOP JERKY MOTIONS IN A SIMPLE AND CONTROLLABLE WAY
        // motor.setClosedLoopRampRate(5); // TODO COULD BE VERY USEFUL!!!!!!!!, WOULD STOP JERKY MOTIONS IN A SIMPLE AND CONTROLLABLE WAY
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setD(DerivativeGainPercentPerRevolutionPerMillisecondOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor
    }

    @Override
    public void periodic() {
        if (limitSwitch.get()) {
            isHomed = true;
            setAngleDegrees(LimitSwitchAngleDegrees);
        }

        if (isManualControlEnabled.getAsBoolean()) {
            gotoTarget.cancel();

            double y = -RobotContainer.get().man.getY();
            y = Cat5Utils.linearAxis(y, 0.1);
            if (y >= 0) {
                y = MathUtil.interpolate(0, ArmConstants.ManualControlMaxUpPercent, y);
            }
            else {
                y = MathUtil.interpolate(0, ArmConstants.ManualControlMaxDownPercent, -y);
            }
            motor.setVoltage(y * 12.0);
        }
        else {
            if (isHomed) {
                gotoTarget.schedule();
            }
            else {
                gotoTarget.cancel();
            }
        }
    }

    @Override
    protected void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
        .withSize(2, 3);

        layout.add("Subsystem Info", this);

        layout.addBoolean("Is Homed", () -> isHomed);
        layout.addDouble("Arm Angle (°)", () -> getAngleDegrees());

        layout.addDouble("Motor Applied Output (%)", () -> motor.getAppliedOutput());
        layout.addDouble("Motor Temperature (°F)", () -> (motor.getMotorTemperature() * (9.0 / 5.0)) + 32);

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

        var isTrackingTargetEntry = layout.add("Is Tracking Target", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isTrackingTarget = () -> isTrackingTargetEntry.getBoolean(false);

        var targetAngleDegreesEntry = layout.add("Target Angle (°)", LimitSwitchAngleDegrees)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", LimitSwitchAngleDegrees, "max", MaxAngleDegrees))
            .getEntry();
        targetAngleDegrees = () -> targetAngleDegreesEntry.getDouble(LimitSwitchAngleDegrees);
    }

    public void gotoHome() {
        if (isHomed) {
            return;
        }
        
        motor.setVoltage(HomingPercent * 12.0);
    }
    public void gotoAngleDegrees(double angleDegrees) {
        double referenceRotations = angleDegrees * MotorRevolutionsPerDegree;
        double direction = encoder.getVelocity() > 0 ? 1 : -1;
        double arbFeedforward = getResistGravityPercent() + getResistStaticFrictionPercent(direction);
        pidController.setReference(referenceRotations, ControlType.kPosition, 0, arbFeedforward * 12.0, ArbFFUnits.kVoltage);
    }
    public void brake() {
        motor.setVoltage(0);
    }

    private void setAngleDegrees(double angleDegrees) {
        double rotations = angleDegrees * MotorRevolutionsPerDegree;
        encoder.setPosition(rotations);
    }
    private double getAngleDegrees() {
        double rotations = encoder.getPosition();
        return rotations * DegreesPerMotorRevolution;
    }

    public double getResistGravityPercent() {
        if (!isHomed) {
            return 0;
        }

        double angleRadians = Math.toRadians(getAngleDegrees());
        return Math.cos(angleRadians) * MaxResistGravityPercent;
    }
    public double getResistStaticFrictionPercent(double direction) {
        return direction * ResistStaticFrictionPercent;
    }

    public boolean isTrackingTarget() {
        return isTrackingTarget.getAsBoolean();
    }
    public double getTargetAngleDegrees() {
        return targetAngleDegrees.getAsDouble();
    }
}
