package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.ColorSensorV3.Register;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class Intake extends SubsystemBase{
    public final WPI_TalonSRX rightMotor = new WPI_TalonSRX(1);
    public final WPI_TalonSRX leftMotor = new WPI_TalonSRX(2);

    public Intake() {
        register();
    }

    public void intake() {
        rightMotor.set(0.25);
        leftMotor.set(-0.25);
    }

    public void placePiece() {
        rightMotor.set(-0.25);
        leftMotor.set(0.25);
    }

    public void SlowPlacePiece() {
        rightMotor.set(-0.15);
        leftMotor.set(0.15);
    }

}
