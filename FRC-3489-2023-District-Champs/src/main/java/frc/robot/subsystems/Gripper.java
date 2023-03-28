package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.enums.GamePiece;

import static frc.robot.Constants.GripperConstants.*;

public class Gripper extends Cat5Subsystem<Gripper> {

    private static Gripper instance = new Gripper();

    public static Gripper get() {
        return instance;
    }

    // Devices
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(LeftMotorDeviceId);
    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(RightMotorDeviceId);
    private final DigitalInput limitSwitch = new DigitalInput(LimitSwitchChannel);

    // Commands
    private final CommandBase setSpeedState = setSpeedState();
    private final CommandBase stopCommand = getStopCommand();
    private final CommandBase intakeCommand = getIntakeCommand();

    // States
    private GripperState state = GripperState.Intake;
    private GamePiece heldGamePiece = GamePiece.Unknown;
    private boolean canReintakeAgain = true;
    private Timer reintakeAntiEatTimer = new Timer();

    private Gripper() {
        super(i -> instance = i);

        setDefaultCommand(setSpeedState);
    }

    //#region Control
    private CommandBase setSpeedState() {
        return run(() -> {
            setMotors(state.getSpeed());
        })
            .withTimeout(state.getSeconds())
            .withName("Set Speed");
    }
    private void setMotors(double percent) {
        
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

    private CommandBase getStopCommand() {
        return run(() -> {
            if (heldGamePiece == GamePiece.Unknown) {
                canReintakeAgain = true;
            }

            setState(GripperState.Off);

            if (DriverStation.isEnabled() && canReintakeAgain) {
                boolean limitSwitch = getLimitSwitch();
                if (IsConeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cone && !limitSwitch) {
                        intakeCommand.schedule();
                        reintakeAntiEatTimer.restart();
                    }
                }
                if (IsCubeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cube && !limitSwitch) {
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

                setState(GripperState.Intake);
            }
            else {
                heldGamePiece = detectedGamePiece;
                stopCommand.schedule();
            }
        })
            .withName("Intake");
    }

    private CommandBase outtakeCommand(GripperState state) {
        return run(() -> {
            setState(state);
        })
            .withName(state.toString());
    }

    private boolean getLimitSwitch() {
        return limitSwitch.get();
    }

    private void lowOuttake(GamePiece gamePiece) {
        switch (gamePiece) {
            case Cone:
                state = GripperState.LowCone;
            case Cube:
                state = GripperState.LowCube;
            case Unknown:
                state = GripperState.LowUnknown;
        }
            
    }

    // public GamePiece getDetectedGamePiece() {
    //     // return detectedGamePiece;
    //     if (limitSwitch.get()) { // color sensor inverted, not triggered here
    //         return GamePiece.Unknown;
    //     }

    //     GamePiece piece = Leds.get().getIndicatedGamePiece();

    //     if (piece == GamePiece.Unknown) {
    //         piece = GamePiece.Cone;
    //     }

    //     return piece;
    // }

    public void setHeldGamePiece(GamePiece heldGamePiece) {
        this.heldGamePiece = heldGamePiece;
    }

    public GripperState getState() {
        return state;
    }
    
    public void setState(GripperState state) {
        this.state = state;
    }

}