package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Toast extends SubsystemBase {
    //
    private Solenoid leftSolenoid = new Solenoid(36, PneumaticsModuleType.REVPH, 1);
    private Solenoid rightSolenoid = new Solenoid(36, PneumaticsModuleType.REVPH, 2;

    public Toast() {

    }

    public void moveToast(boolean movement) {
        leftSolenoid.set(movement);
        rightSolenoid.set(movement);
    }
}




// :)