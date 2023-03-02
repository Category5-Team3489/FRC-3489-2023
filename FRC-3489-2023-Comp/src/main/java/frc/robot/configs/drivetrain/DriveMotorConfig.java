package frc.robot.configs.drivetrain;

import com.ctre.phoenix.motorcontrol.can.TalonFX;

import frc.robot.PercentVelocityMap;
import frc.robot.configs.Cat5Config;
import frc.robot.enums.ModulePosition;

public class DriveMotorConfig extends Cat5Config {
    private static int driveMotorIndex = 0;
    private static final TalonFX[] driveMotors = new TalonFX[4]; // frontLeft, frontRight, backLeft, backRight
    private static final PercentVelocityMap[] percentVelocityMaps = new PercentVelocityMap[4]; // frontLeft, frontRight, backLeft, backRight

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

    public static PercentVelocityMap getPercentVelocityMap(ModulePosition position) {
        return percentVelocityMaps[position.index];
    }

    public static double getPercent(TalonFX motor, double percent) {
        return percent;

        // int index = -1;
        // for (int i = 0; i < 4; i++) {
        //     if (driveMotors[i] == motor) {
        //         index = i;
        //         break;
        //     }
        // }

        // double maxVelocityMetersPerSecond = Drivetrain.get().maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble();
        // return percentVelocityMaps[index].getPercent(percent * maxVelocityMetersPerSecond);
    }
}
