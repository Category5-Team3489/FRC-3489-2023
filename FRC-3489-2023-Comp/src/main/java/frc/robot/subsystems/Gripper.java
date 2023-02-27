package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.GamePiece;
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

    // Commands
    private final CommandBase stopCommand = getStopCommand();
    private final CommandBase intakeCommand = getIntakeCommand();
    private final CommandBase outtakeConeCommand = getOuttakeConeCommand();
    private final CommandBase outtakeCubeCommand = getOuttakeCubeCommand();
    
    // State
    private GamePiece heldGamePiece = GamePiece.Unknown; // set unknown when intaking and unknown, set unknown when outaking

    private Gripper() {
        super((i) -> instance = i);

        setDefaultCommand(stopCommand);
    }

    @Override
    public void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.add("Subsystem Info", this);

        var subsystemLayout = getLayout(Cat5ShuffleboardTab.Gripper, BuiltInLayouts.kList)
            .withSize(2, 3);

        subsystemLayout.add(stopCommand);
        subsystemLayout.add(intakeCommand);
        subsystemLayout.add(outtakeConeCommand);
        subsystemLayout.add(outtakeCubeCommand);
    }

    @Override
    public void periodic() {
        GamePiece detectedGamePiece
    }

    public void setState(IntakeState intakeState) {
        this.intakeState = intakeState;

        ColorSensorState state = ColorSensor.get().getDetectedGamePiece();

        switch(intakeState) {
            case Off:
                stopIntake();
                break;
            case Intake:
                if (state == ColorSensorState.Nothing) { //Nothing in intake
                    intake();
                }
                else if (state == ColorSensorState.Cone || state == ColorSensorState.Cube) { // Cone or Cube in intake
                    stopIntake();
                }
                break;
            case OutTake:
                if (state == ColorSensorState.Nothing) { //Nothing
                    outTake();
                }
                else if (state == ColorSensorState.Cone) { //Cone
                    placeCone();
                }
                else if (state == ColorSensorState.Cube) { //Cube
                    placeCube();
                }
                break;
        }
    }

    public void getColorSensor() {

    }

    public void intake() {
        rightMotor.set(GripperConstants.IntakePercent);
        leftMotor.set(-GripperConstants.IntakePercent);
    }

    public void outTake() {
        rightMotor.set(-GripperConstants.OutakePercent);
        leftMotor.set(GripperConstants.OutakePercent);
    }

    public void placeCube() {
        rightMotor.set(-GripperConstants.OuttakeCubePercent);
        leftMotor.set(GripperConstants.OuttakeCubePercent);
    }

    public void placeCone() {
        rightMotor.set(-GripperConstants.OuttakeConePercent);
        leftMotor.set(GripperConstants.OuttakeConePercent);
    }

    public void stopIntake() {
        rightMotor.set(0);
        leftMotor.set(0);
    }

    // Percent: negative pulls in, positive pushes out
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

    //#region Commands
    private CommandBase getStopCommand() {
        return Commands.run(() -> {
            setMotors(0);
        }, this)
            .withName("Stop");
    }
    private CommandBase getIntakeCommand() {
        return Commands.run(() -> {
            GamePiece detectedGamePiece = ColorSensor.get().getDetectedGamePiece();
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
        }, this)
            .withName("Outtake Cone");
    }
    private CommandBase getOuttakeCubeCommand() {
        return Commands.run(() -> {
            setMotors(GripperConstants.OuttakeCubePercent);
        }, this)
            .withName("Outtake Cube");
    }
    //#endregion
}
