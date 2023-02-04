package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.ArmConstants;

public class Arm extends SubsystemBase {
    private final CANSparkMax motor = new CANSparkMax(ArmConstants.MotorDeviceId, MotorType.kBrushless);

    public Arm() {
        register();
    }

    @Override
    public void periodic() {
        
    }
}
