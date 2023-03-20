package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Utils;
import frc.robot.Constants;
import frc.robot.enums.GamePiece;
import frc.robot.enums.LedPattern;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.GripperConstants.*;

import java.util.function.BooleanSupplier;

public class Gripper extends Cat5Subsystem<Gripper> {
    //#region Singleton
    private static Gripper instance = new Gripper();

    public static Gripper get() {
        return instance;
    }
    //#endregion

    // Devices
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(LeftMotorDeviceId);
    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(RightMotorDeviceId);

    // Suppliers
    private final BooleanSupplier isColorSensorDisabled;

    // Commands
    private final CommandBase stopCommand;
    private final CommandBase intakeCommand;
    private final CommandBase lowOuttakeConeCommand;
    private final CommandBase midOuttakeConeCommand;
    private final CommandBase highOuttakeConeCommand;
    private final CommandBase lowOuttakeCubeCommand;
    private final CommandBase midOuttakeCubeCommand;
    private final CommandBase highOuttakeCubeCommand;
    private final CommandBase lowOuttakeUnknownCommand;
    private final CommandBase midOuttakeUnknownCommand;
    private final CommandBase highOuttakeUnknownCommand;

    // State
    private GamePiece heldGamePiece = GamePiece.Unknown;
    private double motorPercent = 0;
    private boolean canReintakeAgain = true;
    private Timer reintakeAntiEatTimer = new Timer();

    private Gripper() {
        super(i -> instance = i);

        stopCommand = getStopCommand();
        intakeCommand = getIntakeCommand();
        lowOuttakeConeCommand = getOuttakeCommand("Low Outtake Cone", LowOuttakeConePercent, LowOuttakeConeSeconds);
        midOuttakeConeCommand = getOuttakeCommand("Mid Outtake Cone", MidOuttakeConePercent, MidOuttakeConeSeconds);
        highOuttakeConeCommand = getOuttakeCommand("High Outtake Cone", HighOuttakeConePercent, HighOuttakeConeSeconds);
        lowOuttakeCubeCommand = getOuttakeCommand("Low Outtake Cube", LowOuttakeCubePercent, LowOuttakeCubeSeconds);
        midOuttakeCubeCommand = getOuttakeCommand("Mid Outtake Cube", MidOuttakeCubePercent, MidOuttakeCubeSeconds);
        highOuttakeCubeCommand = getOuttakeCommand("High Outtake Cube", HighOuttakeCubePercent, HighOuttakeCubeSeconds);
        lowOuttakeUnknownCommand = getOuttakeCommand("Low Outtake Unknown", LowOuttakeUnknownPercent, LowOuttakeUnknownSeconds);
        midOuttakeUnknownCommand = getOuttakeCommand("Mid Outtake Unknown", MidOuttakeUnknownPercent, MidOuttakeUnknownSeconds);
        highOuttakeUnknownCommand = getOuttakeCommand("High Outtake Unknown", HighOuttakeUnknownPercent, HighOuttakeUnknownSeconds);

        setDefaultCommand(stopCommand);

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.add("Subsystem Info", this);
        layout.addString("Held Game Piece", () -> heldGamePiece.toString());
        layout.addBoolean("Can Reintake Again", () -> canReintakeAgain);

        var isColorSensorDisabledEntry = layout.add("Disable Color Sensor", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isColorSensorDisabled = () -> isColorSensorDisabledEntry.getBoolean(false);

        if (Constants.IsDebugShuffleboardEnabled) {
            layout.addDouble("Motor (%)", () -> motorPercent);

            var subsystemLayout = getLayout(Cat5ShuffleboardTab.Gripper, BuiltInLayouts.kList)
                .withSize(2, 1);

            subsystemLayout.add(stopCommand);
            subsystemLayout.add(intakeCommand);
            subsystemLayout.add(lowOuttakeConeCommand);
            subsystemLayout.add(midOuttakeConeCommand);
            subsystemLayout.add(highOuttakeConeCommand);
            subsystemLayout.add(lowOuttakeCubeCommand);
            subsystemLayout.add(midOuttakeCubeCommand);
            subsystemLayout.add(highOuttakeCubeCommand);
            subsystemLayout.add(lowOuttakeUnknownCommand);
            subsystemLayout.add(midOuttakeUnknownCommand);
            subsystemLayout.add(highOuttakeUnknownCommand);
        }
        //#endregion
    }

    //#region Control
    private void setMotors(double percent) {
        motorPercent = percent;
        
        if (percent != 0) {
            leftMotor.set(percent);
            rightMotor.set(-percent);
        }
        else {
            leftMotor.stopMotor();
            rightMotor.stopMotor();
        }
    }
    //#endregion Control

    //#region Commands
    private CommandBase getStopCommand() {
        return run(() -> {
            if (heldGamePiece == GamePiece.Unknown) {
                canReintakeAgain = true;
            }

            setMotors(0);

            if (DriverStation.isEnabled() && canReintakeAgain) {
                int proximity = ColorSensor.get().getProximity();
                if (IsConeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cone && proximity < ReintakeConeProximityThreshold) {
                        intakeCommand.schedule();
                        reintakeAntiEatTimer.restart();
                    }
                }
                if (IsCubeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cube && proximity < ReintakeCubeProximityThreshold) {
                        intakeCommand.schedule();
                        reintakeAntiEatTimer.restart();
                    }
                }
            }
        })
            .ignoringDisable(true)
            .withName("Stop");
    }

    private CommandBase getIntakeCommand() {
        return run(() -> {
            if (heldGamePiece == GamePiece.Unknown) {
                canReintakeAgain = true;
            }

            GamePiece detectedGamePiece = GamePiece.Unknown;

            if (!isColorSensorDisabled.getAsBoolean()) {
                detectedGamePiece = ColorSensor.get().getDetectedGamePiece();
            }

            if (detectedGamePiece == GamePiece.Unknown) {
                if (IsConeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cone &&
                        canReintakeAgain && reintakeAntiEatTimer.hasElapsed(ReintakeAntiConeEatTimeout)) {
                        canReintakeAgain = false;
                        stopCommand.schedule();
                    }
                }
                if (IsCubeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cube &&
                        canReintakeAgain && reintakeAntiEatTimer.hasElapsed(ReintakeAntiCubeEatTimeout)) {
                        canReintakeAgain = false;
                        stopCommand.schedule();
                    }
                }

                setMotors(IntakePercent);
            }
            else {
                heldGamePiece = detectedGamePiece;
                stopCommand.schedule();
            }
        })
            .withName("Intake");
    }

    private CommandBase getOuttakeCommand(String name, double percent, double seconds) {
        return run(() -> {
            setMotors(percent);
        })
            .withTimeout(seconds)
            .withName(name);
    }
    //#endregion

    //#region Public
    public GamePiece getHeldGamePiece() {
        return heldGamePiece;
    }

    public void setHeldGamePiece(GamePiece heldGamePiece) {
        this.heldGamePiece = heldGamePiece;
    }

    public void scheduleStopCommand() {
        stopCommand.schedule();
    }

    public void scheduleIntakeCommand() {
        intakeCommand.schedule();
    }

    public void scheduleOuttakeCommand() {
        switch (Arm.get().getGridPosition()) {
            case Low:
                switch (heldGamePiece) {
                    case Cone:
                        lowOuttakeConeCommand.schedule();
                        break;
                    case Cube:
                        lowOuttakeCubeCommand.schedule();
                        break;
                    case Unknown:
                        lowOuttakeUnknownCommand.schedule();
                        break;
                }
                break;
            case Mid:
                switch (heldGamePiece) {
                    case Cone:
                        midOuttakeConeCommand.schedule();
                        break;
                    case Cube:
                        midOuttakeCubeCommand.schedule();
                        break;
                    case Unknown:
                        midOuttakeUnknownCommand.schedule();
                        break;
                }
                break;
            case High:
                switch (heldGamePiece) {
                    case Cone:
                        highOuttakeConeCommand.schedule();
                        break;
                    case Cube:
                        highOuttakeCubeCommand.schedule();
                        break;
                    case Unknown:
                        highOuttakeUnknownCommand.schedule();
                        break;
                }
                break;
        }

        heldGamePiece = GamePiece.Unknown;

        Cat5Utils.time();
        System.out.println("Gripper outtake");
        Leds.get().getCommand(LedPattern.StrobeWhite, 1.0, true)
            .schedule();
    }
    //#endregion
}