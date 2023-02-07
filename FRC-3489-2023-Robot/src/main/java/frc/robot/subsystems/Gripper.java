package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Cat5Shuffleboard;
import frc.robot.Constants;
import frc.robot.Constants.IntakeConstants;

public class Gripper extends SubsystemBase {
    public final WPI_TalonSRX rightMotor = new WPI_TalonSRX(IntakeConstants.RightIntakeMotor);
    public final WPI_TalonSRX leftMotor = new WPI_TalonSRX(IntakeConstants.RightIntakeMotor);

    public final DigitalInput gripperSensor = new DigitalInput(IntakeConstants.GripperSensor);
    
    public IntakeState intakeState = IntakeState.off;

    public Gripper() {
        register();

        ShuffleboardLayout mainLayout = Cat5Shuffleboard.createMainLayout("Linear Slide")
            .withSize(2, 1);
        mainLayout.addDouble("Right Intake Speed", () -> rightMotor.get());
        mainLayout.addDouble("Left Intake Speed", () -> leftMotor.get());
        mainLayout.addString("Intake State", () -> intakeState.toString());

    }

    enum IntakeState {
        off,
        Grab,
        Place,
        PlaceCube,
        PlaceCone
    }

    public void setState(IntakeState intakeState) {
        this.intakeState = intakeState;

        Timer timer = new Timer();
        timer.start();

        switch(intakeState) {
            case off:
                stopIntake();
            break;
            case Grab:
                Commands.run(() -> grab(), this)
                    .until(() -> gripperSensor.get())
                    .andThen(() -> setState(IntakeState.off))
                    .schedule();
            break;
            case Place:
                Commands.race(
                    Commands.run(() -> place(), this),
                    new WaitCommand(2)
                )
                .andThen(() -> setState(IntakeState.off))
                .schedule();
                
            break;
            case PlaceCube:
                Commands.run(() -> {
                    if (!gripperSensor.get() && timer.hasElapsed(2)) {
                        setState(IntakeState.off);
                    }
                    else {
                        placeCube();
                    }
                }, this)
                    .schedule();
                
            break;
            case PlaceCone:
                Commands.run(() -> {
                    if (!gripperSensor.get() && timer.hasElapsed(2)) {
                        setState(IntakeState.off);
                    }
                    else {
                        placeCone();
                    }
                }, this)
                    .schedule();
            break;
        }
    }

    public void grab() {
        rightMotor.set(IntakeConstants.IntakeSpeed);
        leftMotor.set(-IntakeConstants.IntakeSpeed);
    }

    public void place() {
        rightMotor.set(-IntakeConstants.IntakeSpeed);
        leftMotor.set(IntakeConstants.IntakeSpeed);
    }

    public void placeCube() {
        rightMotor.set(-IntakeConstants.SlowPlaceSpeed);
        leftMotor.set(IntakeConstants.SlowPlaceSpeed);
    }

    public void placeCone() {
        rightMotor.set(-IntakeConstants.SlowPlaceSpeed);
        leftMotor.set(IntakeConstants.SlowPlaceSpeed);
    }

    public void stopIntake() {
        rightMotor.stopMotor();
        leftMotor.stopMotor();
    }
}
