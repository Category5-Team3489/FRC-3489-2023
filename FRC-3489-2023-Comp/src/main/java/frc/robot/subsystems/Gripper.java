package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.Cat5Subsystem;
import frc.robot.Constants.GripperConstants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class Gripper extends Cat5Subsystem<Gripper>{

    private static Gripper instance;

    public static Gripper get() {
        if (instance == null) {
            instance = new Gripper();
        }

        return instance;
    }

    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(GripperConstants.RightMotor);
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(GripperConstants.LeftMotor);
    private final DigitalInput sensor = new DigitalInput(GripperConstants.SensorChannel);
    
    public IntakeState intakeState = IntakeState.Off;

    ColorSensor colorSensor = new ColorSensor();

    public Gripper() {
        super(null);
        
    }
    @Override
    protected void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.add("Subsystem Info", this);
        layout.add("Intake State", intakeState);
    }

    public enum IntakeState {
        Off,
        Intake,
        OutTake,
        PlaceCube,
        PlaceCone
    }
    public void setState(IntakeState intakeState) {
        this.intakeState = intakeState;

        Timer timer = new Timer();
        timer.start();

        switch(intakeState) {
            case Off:
                stopIntake();
            break;
            case Intake:
                // Commands.run(() -> intake(), this)
                //     .until(() -> getLaserSenor())
                //     .andThen(() -> setState(IntakeState.Off))
                //     .schedule();
                intake();
            break;
            case OutTake:
                    Commands.run(() -> outTake(), this)
                    .withTimeout(2)
                    .andThen(() -> setState(IntakeState.Off))
                    .schedule();
                
            break;
            case PlaceCube:
                Commands.run(() -> {
                    if (!sensor.get()) {
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
                    if (!sensor.get()) {
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
        }
    }

    public void getColorSensor() {

    }

    public void intake() {
        rightMotor.set(GripperConstants.IntakeSpeed);
        leftMotor.set(-GripperConstants.IntakeSpeed);
    }

    public void outTake() {
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
}
