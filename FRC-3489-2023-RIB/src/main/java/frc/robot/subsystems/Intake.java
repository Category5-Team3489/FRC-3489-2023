package frc.robot.subsystems;

import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.CANSparkMax;
import com.revrobotics.ColorSensorV3.Register;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;

public class Intake extends SubsystemBase{
    public final CANSparkMax rightMotor = new CANSparkMax(1, MotorType.kBrushless);
    //public final WPI_TalonSRX leftMotor = new WPI_TalonSRX(2);

    public Intake() {
        register();

        setDefaultCommand(Commands.run(() -> rightMotor.stopMotor(), this).withInterruptBehavior(InterruptionBehavior.kCancelSelf));
    }

    public void intake() {
        rightMotor.set(0.2);
        //leftMotor.set(-0.25);
    }

    public void placePiece() {
        rightMotor.set(-0.2);
        //leftMotor.set(0.25);
    }

    public void SlowPlacePiece() {
        rightMotor.set(-0.15);
        //leftMotor.set(0.15);
    }

}
