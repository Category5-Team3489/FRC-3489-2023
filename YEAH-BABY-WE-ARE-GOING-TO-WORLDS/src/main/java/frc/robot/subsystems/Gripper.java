package frc.robot.subsystems;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.datalog.BooleanLogEntry;
import edu.wpi.first.util.datalog.StringLogEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.data.Cat5DeltaTracker;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;
import frc.robot.enums.GamePiece;

public class Gripper extends Cat5Subsystem {
    // Constants
    private static final boolean IsConeReintakingEnabled = true;
    private static final boolean IsCubeReintakingEnabled = true;
    private static final double ReintakeAntiConeEatTimeout = 2.0;
    private static final double ReintakeAntiCubeEatTimeout = 2.0;

    private static final double IntakePercent = -0.5;

    private static final int LeftMotorDeviceId = 9;
    private static final int RightMotorDeviceId = 10;
    private static final int LimitSwitchChannel = 2;

    // Devices
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(LeftMotorDeviceId);
    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(RightMotorDeviceId);
    private final DigitalInput limitSwitch = new DigitalInput(LimitSwitchChannel);

    // Suppliers
    private final BooleanSupplier isLimitSwitchDisabled;

    // Commands
    public final CommandBase stopCommand = getStopCommand();
    public final CommandBase intakeCommand = getIntakeCommand();
    public final CommandBase lowOuttakeConeCommand = getNamePercentSecondsCommand("Low Outtake Cone", 0.5, 1.0); // 0.2, 1.0
    public final CommandBase midOuttakeConeCommand = getNamePercentSecondsCommand("Mid Outtake Cone", 0.3, 2.0); // 0.1, 2.0
    public final CommandBase highOuttakeConeCommand = getNamePercentSecondsCommand("High Outtake Cone", 0.5, 0.6);
    public final CommandBase lowOuttakeCubeCommand = getNamePercentSecondsCommand("Low Outtake Cube", 0.5, 0.5); // 0.5, 0.5
    public final CommandBase midOuttakeCubeCommand = getNamePercentSecondsCommand("Mid Outtake Cube", 0.5, 0.5); // 0.3, 0.5
    public final CommandBase highOuttakeCubeCommand = getNamePercentSecondsCommand("High Outtake Cube", 1.0, 0.6);
    public final CommandBase unstowPieceCommand = getNamePercentSecondsCommand("Unstow Piece", -0.2, 0.5);
    public final CommandBase keepPieceCommand = getNamePercentSecondsCommand("Keep Piece", -0.2, 0.25);

    // State
    private final Indicator indicator;
    private GamePiece heldGamePiece = GamePiece.Unknown;
    private double motorPercent = 0;
    private boolean canReintakeAgain = true;
    private final Timer reintakeAntiEatTimer = new Timer();

    public Gripper(RobotContainer robotContainer, Indicator indicator) {
        super(robotContainer);
        this.indicator = indicator;

        setDefaultCommand(stopCommand);

        GenericEntry isLimitSwitchDisabledEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Driver)
            .add("Gripper Disable Limit Switch", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isLimitSwitchDisabled = () -> isLimitSwitchDisabledEntry.getBoolean(false);

        GenericEntry limitSwitchEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Manipulator)
            .add("Gripper Limit Switch", limitSwitch.get())
            .getEntry();
        BooleanLogEntry limitSwitchLogEntry = new BooleanLogEntry(robotContainer.dataLog, "/gripper/limit-switch");
        robotContainer.data.createDatapoint(() -> limitSwitch.get())
            .withShuffleboard(data -> {
                limitSwitchEntry.setBoolean(data);
            }, 25)
            .withLog(data -> {
                limitSwitchLogEntry.append(data);
            }, 25);

        GenericEntry heldGamePieceEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Manipulator)
            .add("Gripper Held Game Piece", heldGamePiece.toString())
            .getEntry();
        StringLogEntry heldGamePieceLogEntry = new StringLogEntry(robotContainer.dataLog, "/gripper/held-game-piece");
        robotContainer.data.createDatapoint(() -> heldGamePiece.toString())
            .withShuffleboard(data -> {
                heldGamePieceEntry.setString(data);
            }, 5)
            .withLog(data -> {
                heldGamePieceLogEntry.append(data);
            }, 5);

        GenericEntry canReintakeAgainEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Manipulator)
            .add("Gripper Can Reintake Again", canReintakeAgain)
            .getEntry();
        BooleanLogEntry canReintakeAgainLogEntry = new BooleanLogEntry(robotContainer.dataLog, "/gripper/can-reintake-again");
        robotContainer.data.createDatapoint(() -> canReintakeAgain)
            .withShuffleboard(data -> {
                canReintakeAgainEntry.setBoolean(data);
            }, 5)
            .withLog(data -> {
                canReintakeAgainLogEntry.append(data);
            }, 5);

        if (Constants.IsDebugShuffleboardEnabled) {
            var layout = robotContainer.layouts.get(Cat5ShuffleboardLayout.Debug_Gripper);
            layout.addDouble("Motor (%)", () -> motorPercent);
            layout.addDouble("Reintake Anti Eat (sec)", () -> reintakeAntiEatTimer.get());
        }

        new Cat5DeltaTracker<GamePiece>(robotContainer, heldGamePiece,
        last -> {
            return last != heldGamePiece;
        }, last -> {
            Cat5.print("Gripper held game piece: " + last.toString() + " -> " + heldGamePiece.toString());
            return heldGamePiece;
        });
        new Cat5DeltaTracker<Boolean>(robotContainer, canReintakeAgain,
        last -> {
            return last != canReintakeAgain;
        }, last -> {
            Cat5.print("Gripper can reintake again: " + last.toString() + " -> " + canReintakeAgain);
            return canReintakeAgain;
        });
        new Cat5DeltaTracker<Command>(robotContainer, getCurrentCommand(),
        last -> {
            return last != getCurrentCommand();
        }, last -> {
            String lastString = last == null ? "None" : last.getName();
            Command currentCommand = getCurrentCommand();
            String currentString = currentCommand == null ? "None" : currentCommand.getName();
            Cat5.print("Gripper current command: " + lastString + " -> " + currentString);
            return currentCommand;
        });
    }

    @Override
    public void periodic() {
        if (heldGamePiece != GamePiece.Unknown) {
            GamePiece indicated = indicator.getIndicatedGamePiece();
            if (indicated != GamePiece.Unknown) {
                heldGamePiece = indicated;
            }
        }
    }

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
    
    private CommandBase getStopCommand() {
        return run(() -> {
            if (heldGamePiece == GamePiece.Unknown) {
                canReintakeAgain = true;
            }

            setMotors(0);

            if (canReintakeAgain) {
                if (IsConeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cone && limitSwitch.get()) {
                        intakeCommand.schedule();
                        reintakeAntiEatTimer.restart();
                    }
                }
                if (IsCubeReintakingEnabled) {
                    if (heldGamePiece == GamePiece.Cube && limitSwitch.get()) {
                        intakeCommand.schedule();
                        reintakeAntiEatTimer.restart();
                    }
                }
            }
        })
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

                robotContainer.pickedUpGamePiece();
            }
        })
            .withName("Intake");
    }

    private CommandBase getNamePercentSecondsCommand(String name, double percent, double seconds) {
        return run(() -> {
            setMotors(percent);
        })
            .withTimeout(seconds)
            .withName(name);
    }

    public GamePiece getDetectedGamePiece() {
        if (limitSwitch.get()) {
            return GamePiece.Unknown;
        }

        GamePiece indicated = indicator.getIndicatedGamePiece();

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

    public void resetCanReintakeAgain() {
        canReintakeAgain = true;
    }
}
