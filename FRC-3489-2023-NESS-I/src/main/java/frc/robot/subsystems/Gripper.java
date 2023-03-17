package frc.robot.subsystems;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import frc.robot.enums.GamePiece;
import frc.robot.enums.GridPosition;
import frc.robot.enums.LedPattern;
import frc.robot.Constants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.GripperConstants.*;

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
    public final CommandBase stopCommand = getStopCommand();
    public final CommandBase intakeCommand = getIntakeCommand();
    public final CommandBase lowOuttakeConeCommand = getOuttakeCommand("Low Cone", LowOuttakeConePercent, LowOuttakeConeSeconds);
    public final CommandBase midOuttakeConeCommand = getOuttakeCommand("Mid Cone", MidOuttakeConePercent, MidOuttakeConeSeconds);
    public final CommandBase highOuttakeConeCommand = getOuttakeCommand("High Cone", HighOuttakeConePercent, HighOuttakeConeSeconds);
    public final CommandBase lowOuttakeCubeCommand = getOuttakeCommand("Low Cube", LowOuttakeCubePercent, LowOuttakeCubeSeconds);
    public final CommandBase midOuttakeCubeCommand = getOuttakeCommand("Mid Cube", MidOuttakeCubePercent, MidOuttakeCubeSeconds);
    public final CommandBase highOuttakeCubeCommand = getOuttakeCommand("High Cube", HighOuttakeCubePercent, HighOuttakeCubeSeconds);
    public final CommandBase lowOuttakeUnknownCommand = getOuttakeCommand("Low Unknown", LowOuttakeUnknownPercent, LowOuttakeUnknownSeconds);
    public final CommandBase midOuttakeUnknownCommand = getOuttakeCommand("Mid Unknown", MidOuttakeUnknownPercent, MidOuttakeUnknownSeconds);
    public final CommandBase highOuttakeUnknownCommand = getOuttakeCommand("High Unknown", HighOuttakeUnknownPercent, HighOuttakeUnknownSeconds);

    private GridPosition gridPosition = Arm.get().getGridPosition();
    
    // State
    private GamePiece heldGamePiece = GamePiece.Unknown;
    private double motorPercent = 0;
    private boolean canReintakeAgain = true;
    private Timer reintakeAntiEatTimer = new Timer();

    private Gripper() {
        super((i) -> instance = i);

        // motorPercent: negative intake, outtake

        setDefaultCommand(stopCommand);

        //#region Bindings
        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(Commands.runOnce(() -> {
                heldGamePiece = ColorSensor.get().getDetectedGamePiece();
            }));
        //#endregion

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
        }
        //#endregion
    }

    public void outtakeCommand() {
        switch (gridPosition) {
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
        Leds.get().getCommand(LedPattern.StrobeWhite,2,true).schedule();
        heldGamePiece = GamePiece.Unknown;
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
        return Commands.run(() -> {
            if (heldGamePiece == GamePiece.Unknown) {
                canReintakeAgain = true;
            }

            setMotors(0);

            if (DriverStation.isEnabled() && canReintakeAgain) {
                int proximity = ColorSensor.get().getProximity();
                // if (heldGamePiece == GamePiece.Cone) {
                //     if (proximity < GripperConstants.ReintakeConeProximityThreshold) {
                //         intakeCommand.schedule();
                //         reintakeAntiEatTimer.restart();
                //     }
                // }
                if (heldGamePiece == GamePiece.Cube) {
                    if (proximity < ReintakeCubeProximityThreshold) {
                        intakeCommand.schedule();
                        reintakeAntiEatTimer.restart();
                    }
                }
            }
        }, this)
            .ignoringDisable(true)
            .withName("Stop");
    }
    private CommandBase getIntakeCommand() {
        return Commands.run(() -> {
            if (heldGamePiece == GamePiece.Unknown) {
                canReintakeAgain = true;
            }

            GamePiece detectedGamePiece = GamePiece.Unknown;

            if (!isColorSensorDisabled.getAsBoolean()) {
                detectedGamePiece = ColorSensor.get().getDetectedGamePiece();
            }

            if (detectedGamePiece == GamePiece.Unknown) {
                // if (heldGamePiece == GamePiece.Cone) {
                //     if (canReintakeAgain && reintakeAntiEatTimer.hasElapsed(GripperConstants.ReintakeAntiConeEatTimeout)) {
                //         canReintakeAgain = false;
                //         stopCommand.schedule();
                //     }
                // }
                if (heldGamePiece == GamePiece.Cube) {
                    if (canReintakeAgain && reintakeAntiEatTimer.hasElapsed(ReintakeAntiCubeEatTimeout)) {
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
        }, this)
            .withName("Intake");
    }

    private CommandBase getOuttakeCommand(String name, double percent, double seconds) {
        return Commands.run(() -> {
            setMotors(percent);
        }, this)
            .withTimeout(seconds)
            .withName(name);
    }
    //#endregion

    //#region Public
    public GamePiece getHeldGamePiece() {
        return heldGamePiece;
    }
    //#endregion
}
