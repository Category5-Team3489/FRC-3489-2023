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
import frc.robot.subsystems.ColorSensor.State;
import frc.robot.subsystems.ColorSensor.State;

public class Gripper extends Cat5Subsystem<Gripper>{

    private static Gripper instance = new Gripper();

    public static Gripper get() {
        return instance;
    }

    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(GripperConstants.RightMotor);
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(GripperConstants.LeftMotor);
    
    public IntakeState intakeState = IntakeState.Off;

    public Gripper() {
        super((i) -> instance = i);
        
    }
    @Override
    protected void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        // layout.add("Subsystem Info", this);
        layout.addString("Intake State", () -> intakeState.toString());
    }

    public enum IntakeState {
        Off,
        Intake,
        OutTake
    }

    public void setState(IntakeState intakeState) {
        this.intakeState = intakeState;

        State state = ColorSensor.get().getState();

        switch(intakeState) {
            case Off:
                stopIntake();
                break;
            case Intake:
                if (state == State.Nothing) { //Nothing in intake
                    intake();
                }
                else if (state == State.Cone || state == State.Cube) { // Cone or Cube in intake
                    stopIntake();
                }
                break;
            case OutTake:
                if (state == State.Nothing) { //Nothing
                    outTake();
                }
                else if (state == State.Cone) { //Cone
                    placeCone();
                }
                else if (state == State.Cube) { //Cube
                    placeCube();
                }
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
        rightMotor.set(-GripperConstants.OutakeSpeed);
        leftMotor.set(GripperConstants.OutakeSpeed);
    }

    public void placeCube() {
        rightMotor.set(-GripperConstants.CubeOutTakeSpeed);
        leftMotor.set(GripperConstants.CubeOutTakeSpeed);
    }

    public void placeCone() {
        rightMotor.set(-GripperConstants.ConeOutTakeSpeed);
        leftMotor.set(GripperConstants.ConeOutTakeSpeed);
    }

    public void stopIntake() {
        rightMotor.set(0);
        leftMotor.set(0);
    }
}
