package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMax.ControlType;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxPIDController.ArbFFUnits;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Cat5Subsystem;
import frc.robot.configs.arm.ArmConfig;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.ArmConstants.*;

import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.DoubleSupplier;

public class Arm extends Cat5Subsystem<Arm> {
    //#region Singleton
    private static Arm instance;

    public static Arm get() {
        if (instance == null) {
            instance = new Arm();
        }

        return instance;
    }
    //#endregion

    // Configs
    public final ArmConfig armConfig = new ArmConfig();

    private final CANSparkMax motor = new CANSparkMax(MotorDeviceId, MotorType.kBrushless);
    private final DigitalInput limitSwitch = new DigitalInput(LimitSwitchChannel);
    private final SparkMaxPIDController pidController;
    private final RelativeEncoder encoder;

    private boolean isHomed = false;

    private DoubleSupplier targetAngle;
    private BooleanSupplier isTrackingTarget;

    private Arm() {
        super(null);

        // Remember: NEO position units are rotations
        // https://docs.revrobotics.com/sparkmax/operating-modes/closed-loop-control
        // Zero degrees is horizontal + is up, - down
        // TODO Could add timer with time that arm has been stalled for using velocity and motor being set to something, get amps, maybe abs(degrees)/watt past 10 sec, if that is low then that is bad

        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        motor.restoreFactoryDefaults();
        motor.setIdleMode(IdleMode.kBrake);
        motor.enableVoltageCompensation(12);
        motor.setSmartCurrentLimit(StallSmartCurrentLimitAmps);
        // motor.setOpenLoopRampRate(0); // TODO COULD BE VERY USEFUL!!!!!!!!, WOULD STOP JERKY MOTIONS IN A SIMPLE AND CONTROLLABLE WAY
        // motor.setClosedLoopRampRate(0); // TODO COULD BE VERY USEFUL!!!!!!!!, WOULD STOP JERKY MOTIONS IN A SIMPLE AND CONTROLLABLE WAY
        pidController.setP(ProportionalGainPercentPerRevolutionOfError);
        pidController.setD(DerivativeGainPercentPerRevolutionPerMillisecondOfError);
        pidController.setOutputRange(MinOutputPercent, MaxOutputPercent);
        motor.burnFlash(); // Always remember this - burn flash, not motor

        new Trigger(() -> limitSwitch.get())
            .whileTrue(Commands.runOnce(() -> {
                isHomed = true;
                setAngleDegrees(LimitSwitchAngleDegrees);
            }));
    }

    @Override
    protected void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
        .withSize(2, 6);

        layout.add("Subsystem Info", this);

        layout.addBoolean("Is Homed", () -> isHomed);
        layout.addDouble("Arm Angle", () -> getAngleDegrees());

        layout.addDouble("Motor Applied Output", () -> motor.getAppliedOutput()); // FIXME THIS LIKELY POLLS THE MOTOR OVER THE CAN BUS AT 50HZ
        layout.addDouble("Motor Temperature", () -> (motor.getMotorTemperature() * (9.0 / 5.0)) + 32); // FIXME THIS LIKELY POLLS THE MOTOR OVER THE CAN BUS AT 50HZ

        var isTrackingTargetEntry = layout.add("Is Tracking Target", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isTrackingTarget = () -> isTrackingTargetEntry.getBoolean(false);
        var targetAngleEntry = layout.add("Target Angle", LimitSwitchAngleDegrees)
            .withWidget(BuiltInWidgets.kNumberSlider)
            .withProperties(Map.of("min", LimitSwitchAngleDegrees, "max", MaxAngleDegrees))
            .getEntry();
        targetAngle = () -> targetAngleEntry.getDouble(LimitSwitchAngleDegrees);
    }

    public void gotoHome() {
        motor.setVoltage(HomingVolts);
    }
    public void gotoAngleDegrees(double angleDegrees) {
        double referenceRotations = angleDegrees * MotorRevolutionsPerDegree;
        double direction = encoder.getVelocity() > 0 ? 1 : -1;
        double arbFeedforward = getResistGravityVolts() + getResistStaticFrictionVolts(direction);
        pidController.setReference(referenceRotations, ControlType.kPosition, 0, arbFeedforward, ArbFFUnits.kVoltage);
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

    public double getResistGravityVolts() {
        if (!isHomed) {
            return 0;
        }

        double angleRadians = Math.toRadians(getAngleDegrees());
        return Math.cos(angleRadians) * MaxResistGravityVolts;
    }
    public double getResistStaticFrictionVolts(double direction) {
        return direction * ResistStaticFrictionVolts;
    }

    public boolean isHomed() {
        return isHomed;
    }

    public boolean isTrackingTarget() {
        return isTrackingTarget.getAsBoolean();
    }
    public double getTargetAngleDegrees() {
        return targetAngle.getAsDouble();
    }
}
