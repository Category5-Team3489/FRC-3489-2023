package frc.robot.configs.drivetrain;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.robot.configs.Cat5Config;
import frc.robot.enums.ModulePosition;

public class DriveMotorConfig extends Cat5Config {
    private static int driveMotorIndex = 0;
    private static final TalonFX[] driveMotors = new TalonFX[4]; // frontLeft, frontRight, backLeft, backRight

    //#region Motor
    public static void supplyDriveMotor(TalonFX motor) {
        if (driveMotorIndex == 4) {
            System.out.println("[ERORR] TOO MANY DRIVE MOTORS SUPPLIED!!!");
            return;
        }
        
        driveMotors[driveMotorIndex++] = motor;
    }
    
    public static TalonFX getDriveMotor(ModulePosition position) {
        return driveMotors[position.index];
    }

    public static boolean isConfigured() {
        return driveMotorIndex == 4;
    }
    //#endregion
}