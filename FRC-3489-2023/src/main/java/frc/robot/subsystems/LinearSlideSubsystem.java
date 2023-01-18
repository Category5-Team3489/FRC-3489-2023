package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class LinearSlideSubsystem extends SubsystemBase{

WPI_TalonFX linearSlideMotor = new WPI_TalonFX(0);

DigitalInput topLimitSwitch = new DigitalInput(0);
DigitalInput bottomLimitSwitch = new DigitalInput(1);

public void extendLinearSlide() {
    if (!topLimitSwitch.get()) {
        linearSlideMotor.set(0.5);
    } else {
        linearSlideMotor.stopMotor();
    }
}

public void lowerLinearSlide() {
    if (!bottomLimitSwitch.get()) {
        linearSlideMotor.set(-0.5);
    } else {
        linearSlideMotor.stopMotor();
    }
}

}
