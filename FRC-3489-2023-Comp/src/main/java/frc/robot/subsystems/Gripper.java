package frc.robot.subsystems;

import java.util.function.BooleanSupplier;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.GamePiece;
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
    private final CommandBase outtakeConeCommand = getOuttakeConeCommand();
    private final CommandBase outtakeCubeCommand = getOuttakeCubeCommand();
    private final CommandBase outtakeUnknownCommand = getOuttakeUnknownCommand();
    
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
                stopCommand.schedule();
            }));

        RobotContainer.get().man.button(GripperConstants.IntakeManButton)
            .onTrue(Commands.runOnce(() -> {
                intakeCommand.schedule();
            }));

        RobotContainer.get().man.button(GripperConstants.OuttakeManButton)
            .onTrue(Commands.runOnce(() -> {
                switch (heldGamePiece) {
                    case Cone:
                        outtakeConeCommand.schedule();
                        break;
                    case Cube:
                        outtakeCubeCommand.schedule();
                        break;
                    case Unknown:
                        outtakeUnknownCommand.schedule();
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
        subsystemLayout.add(outtakeConeCommand);
        subsystemLayout.add(outtakeCubeCommand);
        subsystemLayout.add(outtakeUnknownCommand);
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
        }, this)
            .withName("Stop")
            .ignoringDisable(true);
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
    private CommandBase getOuttakeConeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.OuttakeConePercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withName("Outtake Cone")
            .withTimeout(GripperConstants.OuttakeConeSeconds);
    }
    private CommandBase getOuttakeCubeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.OuttakeCubePercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withName("Outtake Cube")
            .withTimeout(GripperConstants.OuttakeCubeSeconds);
    }
    private CommandBase getOuttakeUnknownCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.OuttakeUnknownPercent);
            heldGamePiece = GamePiece.Unknown;
        }, this)
            .withName("Outtake Unknown")
            .withTimeout(GripperConstants.OuttakeUnknownSeconds);
    }
    //#endregion

    //#region Public
    public GamePiece getHeldGamePiece() {
        return heldGamePiece;
    }
    //#endregion
}
