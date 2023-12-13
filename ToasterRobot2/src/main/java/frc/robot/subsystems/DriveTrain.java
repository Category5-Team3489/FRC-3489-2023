package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class DriveTrain extends SubsystemBase {
    
    public WPI_TalonSRX frontLeft = new WPI_TalonSRX(1);
    public WPI_TalonSRX frontRight = new WPI_TalonSRX(2);
    public WPI_TalonSRX backLeft = new WPI_TalonSRX(3);
    public WPI_TalonSRX backRight = new WPI_TalonSRX(4);


    public DriveTrain() {
        
        frontRight.setInverted(true);
        backRight.setInverted(true);
        
        backLeft.follow(frontLeft);
        backRight.follow(frontRight);
    }
}
