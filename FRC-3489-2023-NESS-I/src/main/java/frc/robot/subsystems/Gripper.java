package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
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
    private final DigitalInput limitSwitch = new DigitalInput(LimitSwitchChannel);

    // Suppliers
    private final BooleanSupplier isLimitSwitchDisabled;

    // Commands
    private final CommandBase stopCommand = getStopCommand();
    private final CommandBase intakeCommand = getIntakeCommand();
    private final CommandBase lowOuttakeConeCommand = getOuttakeCommand("Low Outtake Cone", LowOuttakeConePercent, LowOuttakeConeSeconds);
    private final CommandBase midOuttakeConeCommand = getOuttakeCommand("Mid Outtake Cone", MidOuttakeConePercent, MidOuttakeConeSeconds);
    private final CommandBase highOuttakeConeCommand = getOuttakeCommand("High Outtake Cone", HighOuttakeConePercent, HighOuttakeConeSeconds);
    private final CommandBase lowOuttakeCubeCommand = getOuttakeCommand("Low Outtake Cube", LowOuttakeCubePercent, LowOuttakeCubeSeconds);
    private final CommandBase midOuttakeCubeCommand = getOuttakeCommand("Mid Outtake Cube", MidOuttakeCubePercent, MidOuttakeCubeSeconds);
    private final CommandBase highOuttakeCubeCommand = getOuttakeCommand("High Outtake Cube", HighOuttakeCubePercent, HighOuttakeCubeSeconds);
    private final CommandBase lowOuttakeUnknownCommand = getOuttakeCommand("Low Outtake Unknown", LowOuttakeUnknownPercent, LowOuttakeUnknownSeconds);
    private final CommandBase midOuttakeUnknownCommand = getOuttakeCommand("Mid Outtake Unknown", MidOuttakeUnknownPercent, MidOuttakeUnknownSeconds);
    private final CommandBase highOuttakeUnknownCommand = getOuttakeCommand("High Outtake Unknown", HighOuttakeUnknownPercent, HighOuttakeUnknownSeconds);
    private final CommandBase unstowPieceCommand = getOuttakeCommand("Unstow Piece", -0.2, 0.5);

    // State
    private GamePiece heldGamePiece = GamePiece.Unknown;
    private double motorPercent = 0;
    private boolean canReintakeAgain = true;
    private Timer reintakeAntiEatTimer = new Timer();

    private Gripper() {
        super(i -> instance = i);

        setDefaultCommand(stopCommand);

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.add("Subsystem Info", this);
        layout.addString("Held Game Piece", () -> heldGamePiece.toString());
        layout.addBoolean("Can Reintake Again", () -> canReintakeAgain);
        layout.addBoolean("Limit Switch", () -> limitSwitch.get());

        var isLimitSwitchDisabledEntry = layout.add("Disable Limit Switch", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isLimitSwitchDisabled = () -> isLimitSwitchDisabledEntry.getBoolean(false);

        if (Constants.IsDebugShuffleboardEnabled) {
            layout.addDouble("Motor (%)", () -> motorPercent);
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
                if (IsConeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cone && !limitSwitch.get()) {
                        intakeCommand.schedule();
                        reintakeAntiEatTimer.restart();
                    }
                }
                if (IsCubeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cube && !limitSwitch.get()) {
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

            if (!isLimitSwitchDisabled.getAsBoolean()) {
                detectedGamePiece = getDetectedGamePiece();
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
    public GamePiece getDetectedGamePiece() {
        if (limitSwitch.get()) {
            return GamePiece.Unknown;
        }

        GamePiece indicated = Leds.get().getIndicatedGamePiece();

        // Default to cone
        if (indicated == GamePiece.Unknown) {
            indicated = GamePiece.Cone;
        }

        return indicated;
    }

    public GamePiece getHeldGamePiece() {
        return heldGamePiece;
    }

    public void setHeldGamePiece(GamePiece heldGamePiece) {
        this.heldGamePiece = heldGamePiece;
    }

    public void scheduleUnstowPieceCommand() {
        unstowPieceCommand.schedule();
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