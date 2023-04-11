package frc.robot.configs.drivetrain;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.robot.Cat5;
import frc.robot.enums.ModulePosition;

public class DriveMotorConfig {
    private static int driveMotorIndex = 0;
    private static final TalonFX[] driveMotors = new TalonFX[4]; // frontLeft, frontRight, backLeft, backRight

    public static void supplyDriveMotor(TalonFX motor) {
        if (driveMotorIndex == 4) {
            Cat5.error("Too many drive motors supplied in drive motor config", false);
            return;
        }
        
        driveMotors[driveMotorIndex++] = motor;
    }
    
    public static TalonFX getDriveMotor(ModulePosition position) {
        return driveMotors[position.getIndex()];
    }
}