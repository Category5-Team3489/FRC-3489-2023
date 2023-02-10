package frc.robot.subsystems;

import javax.tools.Diagnostic;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Preferences;
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
import frc.robot.Constants.GripperConstants;

public class Gripper extends SubsystemBase {
    public final WPI_TalonSRX rightMotor = new WPI_TalonSRX(GripperConstants.RightMotor);
    public final WPI_TalonSRX leftMotor = new WPI_TalonSRX(GripperConstants.RightMotor);

    public final DigitalInput gripperSensor = new DigitalInput(GripperConstants.SensorChannel);
    
    public IntakeState intakeState = IntakeState.Off;
    
    private final GenericEntry laserState;

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

        laserState = diagnosticLayout.add("Laser State", false).getEntry();
        diagnosticLayout.add("Laser Diagnostic", laserDiagnostic());

        diagnosticLayout.add("Test Intake", intakeDiagnostic());
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
                System.out.println(intakeState);
            break;
            case Grab:
                Commands.run(() -> grab(), this)
                    .until(() -> gripperSensor.get())
                    .andThen(() -> setState(IntakeState.Off))
                    .schedule();
                    System.out.println(intakeState);
            break;
            case Place:
                    Commands.run(() -> place(), this)
                    .withTimeout(2)
                    .andThen(() -> setState(IntakeState.Off))
                    .schedule();
                    System.out.println(intakeState);
                
            break;
            case PlaceCube:
                Commands.run(() -> {
                    if (!gripperSensor.get()) {
                        if (timer.hasElapsed(2)) {
                            setState(IntakeState.Off);
                        }
                    }
                    else {
                        placeCube();
                        System.out.println(intakeState);
                    }
                }, this)
                    .schedule();
                    System.out.println(intakeState);
                
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
                        System.out.println(intakeState);
                    }
                }, this)
                    .schedule();
                    System.out.println(intakeState);
            break;
            case laserDiagnostic:
                Commands.runOnce(() -> {
                    if (gripperSensor.get() || timer.hasElapsed(6)) {
                        laserState.setBoolean(gripperSensor.get());
                    }
                }, this);
            break;
        }
    }

    public void grab() {
        rightMotor.set(GripperConstants.IntakeSpeed);
        leftMotor.set(-GripperConstants.IntakeSpeed);
    }

    public void place() {
        rightMotor.set(-GripperConstants.IntakeSpeed);
        leftMotor.set(GripperConstants.IntakeSpeed);
    }

    public void placeCube() {
        rightMotor.set(-GripperConstants.SlowPlaceSpeed);
        leftMotor.set(GripperConstants.SlowPlaceSpeed);
    }

    public void placeCone() {
        rightMotor.set(-GripperConstants.SlowPlaceSpeed);
        leftMotor.set(GripperConstants.SlowPlaceSpeed);
    }

    public void stopIntake() {
        rightMotor.set(0);
        leftMotor.set(0);
    }

    public boolean getLaserSenor() {
        return gripperSensor.get();
    }

    public CommandBase intakeDiagnostic() {
        return Commands.runOnce(() -> setState(IntakeState.Grab), this)
            .withTimeout(3)

            .andThen(Commands.runOnce(() -> setState(IntakeState.Place), this)
            .withTimeout(3))

            .andThen(Commands.runOnce(() -> setState(IntakeState.PlaceCone), this)
            .withTimeout(3))

            .andThen(Commands.runOnce(() -> setState(IntakeState.PlaceCube), this)
            .withTimeout(3))

            .andThen(Commands.runOnce(() -> stopIntake()))
            .withName("Intake Diagnostic");

            
    }

    public CommandBase laserDiagnostic() {
        return Commands.runOnce(() -> setState(IntakeState.laserDiagnostic), this)
        .withName("Laser Diagnostic");
    }


}
