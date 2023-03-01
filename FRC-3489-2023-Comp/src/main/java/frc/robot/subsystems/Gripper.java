package frc.robot.subsystems;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.enums.GamePiece;
import frc.robot.enums.GridPosition;
import frc.robot.RobotContainer;
import frc.robot.Constants.GripperConstants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class Gripper extends Cat5Subsystem<Gripper> {
    //#region Singleton
    private static Gripper instance = new Gripper();

    public static Gripper get() {
        return instance;
    }
    //#endregion

    // Devices
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(GripperConstants.LeftMotorDeviceId);
    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(GripperConstants.RightMotorDeviceId);

    // Suppliers
    private final BooleanSupplier isColorSensorDisabled;

    // Commands
    private final CommandBase stopCommand = getStopCommand();
    private final CommandBase intakeCommand = getIntakeCommand();
    private final CommandBase lowOuttakeConeCommand = getLowOuttakeConeCommand();
    private final CommandBase lowOuttakeCubeCommand = getLowOuttakeCubeCommand();
    private final CommandBase lowOuttakeUnknownCommand = getLowOuttakeUnknownCommand();
    private final CommandBase midOuttakeConeCommand = getMidOuttakeConeCommand();
    private final CommandBase midOuttakeCubeCommand = getMidOuttakeCubeCommand();
    private final CommandBase midOuttakeUnknownCommand = getMidOuttakeUnknownCommand();
    private final CommandBase highOuttakeConeCommand = getHighOuttakeConeCommand();
    private final CommandBase highOuttakeCubeCommand = getHighOuttakeCubeCommand();
    private final CommandBase highOuttakeUnknownCommand = getHighOuttakeUnknownCommand();
    
    // State
    private GamePiece heldGamePiece = GamePiece.Unknown;
    private double motorPercent = 0;

    private Gripper() {
        super((i) -> instance = i);

        // motorPercent: negative intake, outtake

        setDefaultCommand(stopCommand);

        //#region Bindings
        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(Commands.runOnce(() -> {
                heldGamePiece = ColorSensor.get().getDetectedGamePiece();
            }));

        RobotContainer.get().man.button(GripperConstants.StopManButton)
            .onTrue(Commands.runOnce(() -> {
                heldGamePiece = GamePiece.Unknown;
                stopCommand.schedule();
            }));

        RobotContainer.get().man.button(GripperConstants.IntakeManButton)
            .onTrue(Commands.runOnce(() -> {
                intakeCommand.schedule();
            }));

        RobotContainer.get().man.button(GripperConstants.OuttakeManButton)
            .onTrue(Commands.runOnce(() -> {
                GridPosition gridPosition = Arm.get().getGridPosition();
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
            }));
        //#endregion

        //#region Shuffleboard
        // Main
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.add("Subsystem Info", this);
        layout.addString("Held Game Piece", () -> heldGamePiece.toString());
        layout.addDouble("Motor Percent", () -> motorPercent);

        var isColorSensorDisabledEntry = layout.add("Disable Color Sensor", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isColorSensorDisabled = () -> isColorSensorDisabledEntry.getBoolean(false);

        // Subsystem
        var subsystemLayout = getLayout(Cat5ShuffleboardTab.Gripper, BuiltInLayouts.kList)
            .withSize(2, 3);

        subsystemLayout.add(stopCommand);
        subsystemLayout.add(intakeCommand);
        subsystemLayout.add(lowOuttakeConeCommand);
        subsystemLayout.add(lowOuttakeCubeCommand);
        subsystemLayout.add(lowOuttakeUnknownCommand);
        subsystemLayout.add(midOuttakeConeCommand);
        subsystemLayout.add(midOuttakeCubeCommand);
        subsystemLayout.add(midOuttakeUnknownCommand);
        subsystemLayout.add(highOuttakeConeCommand);
        subsystemLayout.add(highOuttakeCubeCommand);
        subsystemLayout.add(highOuttakeUnknownCommand);
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
        return Commands.run(() -> {
            setMotors(0);

            if (DriverStation.isEnabled() && heldGamePiece == GamePiece.Cube) {
                int proximity = ColorSensor.get().getProximity();
                if (proximity < GripperConstants.IntakeCubeProximityThreshold) {
                    intakeCommand.schedule();
                }
            }
        }, this)
            .ignoringDisable(true)
            .withName("Stop");
    }
    private CommandBase getIntakeCommand() {
        return Commands.run(() -> {
            GamePiece detectedGamePiece = GamePiece.Unknown;

            if (!isColorSensorDisabled.getAsBoolean()) {
                detectedGamePiece = ColorSensor.get().getDetectedGamePiece();
            }

            if (detectedGamePiece == GamePiece.Unknown) {
                setMotors(GripperConstants.IntakePercent);
            }
            else {
                heldGamePiece = detectedGamePiece;
                stopCommand.schedule();
            }
        }, this)
            .withName("Intake");
    }
    // Low
    private CommandBase getLowOuttakeConeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.LowOuttakeConePercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.LowOuttakeConeSeconds)
            .withName("Low Outtake Cone");
    }
    private CommandBase getLowOuttakeCubeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.LowOuttakeCubePercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.LowOuttakeCubeSeconds)
            .withName("Low Outtake Cube");  
    }
    private CommandBase getLowOuttakeUnknownCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.LowOuttakeUnknownPercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.LowOuttakeUnknownSeconds)
            .withName("Low Outtake Unknown");
    }
    // Mid
    private CommandBase getMidOuttakeConeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.MidOuttakeConePercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.MidOuttakeConeSeconds)
            .withName("Mid Outtake Cone");
    }
    private CommandBase getMidOuttakeCubeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.MidOuttakeCubePercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.MidOuttakeCubeSeconds)
            .withName("Mid Outtake Cube");  
    }
    private CommandBase getMidOuttakeUnknownCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.MidOuttakeUnknownPercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.MidOuttakeUnknownSeconds)
            .withName("Mid Outtake Unknown");
    }
    // High
    private CommandBase getHighOuttakeConeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.HighOuttakeConePercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.HighOuttakeConeSeconds)
            .withName("High Outtake Cone");
    }
    private CommandBase getHighOuttakeCubeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.HighOuttakeCubePercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.HighOuttakeCubeSeconds)
            .withName("High Outtake Cube");  
    }
    private CommandBase getHighOuttakeUnknownCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.HighOuttakeUnknownPercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withTimeout(GripperConstants.HighOuttakeUnknownSeconds)
            .withName("High Outtake Unknown");
    }
    //#endregion

    //#region Public
    public GamePiece getHeldGamePiece() {
        return heldGamePiece;
    }
    //#endregion
}
