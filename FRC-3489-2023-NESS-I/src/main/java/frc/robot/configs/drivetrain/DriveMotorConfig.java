package frc.robot.configs.drivetrain;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Cat5Utils;
import frc.robot.configs.Cat5Config;
import frc.robot.enums.ModulePosition;

public class DriveMotorConfig extends Cat5Config {
    private static int driveMotorIndex = 0;
    private static final TalonFX[] driveMotors = new TalonFX[4]; // frontLeft, frontRight, backLeft, backRight

    //#region Motor
    public static void supplyDriveMotor(TalonFX motor) {
        if (driveMotorIndex == 4) {
            Cat5Utils.time();
            DriverStation.reportWarning("Too many drive motors supplied in drive motor config", false);
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