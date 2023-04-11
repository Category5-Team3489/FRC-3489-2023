package frc.robot.subsystems;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5;
import frc.robot.RobotContainer;
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
    public final CommandBase lowOuttakeConeCommand = getNamePercentSecondsCommand("Low Outtake Cone", 0.2, 1.0);
    public final CommandBase midOuttakeConeCommand = getNamePercentSecondsCommand("Mid Outtake Cone", 0.1, 2.0);
    public final CommandBase highOuttakeConeCommand = getNamePercentSecondsCommand("High Outtake Cone", 0.5, 0.6);
    public final CommandBase lowOuttakeCubeCommand = getNamePercentSecondsCommand("Low Outtake Cube", 0.2, 0.5);
    public final CommandBase midOuttakeCubeCommand = getNamePercentSecondsCommand("Mid Outtake Cube", 0.3, 0.5);
    public final CommandBase highOuttakeCubeCommand = getNamePercentSecondsCommand("High Outtake Cube", 1.0, 0.6);
    public final CommandBase unstowPieceCommand = getNamePercentSecondsCommand("Unstow Piece", -0.2, 0.5);

    // State
    private final Indicator indicator;
    private GamePiece heldGamePiece = GamePiece.Unknown;
    @SuppressWarnings("unused")
    private double motorPercent = 0;
    private boolean canReintakeAgain = true;
    private final Timer reintakeAntiEatTimer = new Timer();

    public Gripper(RobotContainer robotContainer, Indicator indicator) {
        super(robotContainer);
        this.indicator = indicator;

        setDefaultCommand(stopCommand);

        var isLimitSwitchDisabledEntry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Workarounds)
            .add("Disable Limit Switch", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .withSize(2, 1)
            .getEntry();
        isLimitSwitchDisabled = () -> isLimitSwitchDisabledEntry.getBoolean(false);
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
            .beforeStarting(() -> {
                Cat5.print("Gripper Stop");
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
                // TODO
                // Cat5Utils.time();
                // System.out.println("Now holding game piece: " + detectedGamePiece);
                // System.out.println("Arm angle degrees: " + Arm.get().getTargetAngleDegrees());

                heldGamePiece = detectedGamePiece;
                stopCommand.schedule();
            }
        })
            .beforeStarting(() -> {
                Cat5.print("Gripper Intake");
            })
            .withName("Intake");
    }

    private CommandBase getNamePercentSecondsCommand(String name, double percent, double seconds) {
        return run(() -> {
            setMotors(percent);
        })
            .withTimeout(seconds)
            .beforeStarting(() -> {
                Cat5.print("Gripper " + name);
            })
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
}
