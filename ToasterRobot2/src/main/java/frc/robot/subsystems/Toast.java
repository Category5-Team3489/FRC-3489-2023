package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Toast extends SubsystemBase {

    public Solenoid leftSolenoid = new Solenoid(36, PneumaticsModuleType.REVPH, 6);
    public Solenoid rightSolenoid = new Solenoid(36, PneumaticsModuleType.REVPH, 7);

    public Toast() {

    }
}
