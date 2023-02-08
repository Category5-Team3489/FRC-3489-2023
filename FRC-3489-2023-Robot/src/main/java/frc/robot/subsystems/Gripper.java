package frc.robot.subsystems;

import javax.tools.Diagnostic;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;
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
    
    public IntakeState intakeState = IntakeState.Off;
    
    private final GenericEntry laserDiagnostic;

    ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("Gripper")
            .withSize(2, 4);

    
    public Gripper() {
        register();

        ShuffleboardLayout mainLayout = Cat5Shuffleboard.createMainLayout("Gripper")
            .withSize(2, 4);
        mainLayout.addDouble("Right Intake Speed", () -> rightMotor.get());
        mainLayout.addDouble("Left Intake Speed", () -> leftMotor.get());
        mainLayout.addString("Intake State", () -> intakeState.toString());
        mainLayout.addBoolean("Gripper Laser", () -> gripperSensor.get());

        diagnosticLayout.addBoolean("Gripper Laser", () -> gripperSensor.get());
        diagnosticLayout.add("Test Intake", intakeDiagnostic());

        laserDiagnostic = diagnosticLayout.add("Intake Diagnostic", true).getEntry();
    }

    public enum IntakeState {
        Off,
        Grab,
        Place,
        PlaceCube,
        PlaceCone,
        laserDiagnostic
    }

    public void setState(IntakeState intakeState) {
        this.intakeState = intakeState;

        Timer timer = new Timer();
        timer.start();

        switch(intakeState) {
            case Off:
                stopIntake();
            break;
            case Grab:
                Commands.run(() -> grab(), this)
                    .until(() -> gripperSensor.get())
                    .andThen(() -> setState(IntakeState.Off))
                    .schedule();
            break;
            case Place:
                Commands.race(
                    Commands.run(() -> place(), this),
                    new WaitCommand(2)
                )
                .andThen(() -> setState(IntakeState.Off))
                .schedule();
                
            break;
            case PlaceCube:
                Commands.run(() -> {
                    if (!gripperSensor.get()) {
                        //timer.start();
                        if (timer.hasElapsed(2)) {
                            setState(IntakeState.Off);
                        }
                    }
                    else {
                        placeCube();
                    }
                }, this)
                    .schedule();
                
            break;
            case PlaceCone:
                Commands.run(() -> {
                    if (!gripperSensor.get()) {
                        //timer.start();
                        if (timer.hasElapsed(2)) {
                            setState(IntakeState.Off);
                        }
                    }
                    else {
                        placeCone();
                    }
                }, this)
                    .schedule();
            break;
            case laserDiagnostic:
            Commands.run(() -> {
                if (!gripperSensor.get()) {
                        setState(IntakeState.Off);
                }
            }, this);
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

    public boolean getLaserSenor() {
        return gripperSensor.get();
    }

    public CommandBase intakeDiagnostic() {
        return Commands.race(
            Commands.runOnce(() -> setState(IntakeState.Grab), this),
            new WaitCommand(3))
        .andThen(Commands.race(
            Commands.runOnce(() -> setState(IntakeState.Place), this),
            new WaitCommand(3)))
        .andThen(Commands.race(
            Commands.runOnce(() -> setState(IntakeState.PlaceCone), this),
            new WaitCommand(3)))
        .andThen(Commands.race(
            Commands.runOnce(() -> setState(IntakeState.PlaceCube), this),
            new WaitCommand(3)))
        .andThen(Commands.runOnce(() -> setState(IntakeState.Off), this))
        .withName("Intake Diagnostic");
    }


}
