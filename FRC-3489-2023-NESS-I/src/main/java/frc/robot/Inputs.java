package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;

public class Inputs {
    // Devices
    public static final CommandXboxController Xbox = new CommandXboxController(OperatorConstants.XboxPort);
    public static final CommandJoystick Man = new CommandJoystick(OperatorConstants.ManPort);

    //#region Drive
    public static double getDriveXPercent() {
        return Cat5Utils.quadraticAxis(-Xbox.getLeftY(), OperatorConstants.XboxAxisDeadband);
    }

    public static double getDriveYPercent() {
        return Cat5Utils.quadraticAxis(-Xbox.getLeftX(), OperatorConstants.XboxAxisDeadband);
    }

    public static double getDriveOmegaPercent() {
        return Cat5Utils.quadraticAxis(-Xbox.getRightX(), OperatorConstants.XboxAxisDeadband);
    }

    public static double getDriveSpeedLimiterPercent() {
        double speedLimiter = 1.0 / 2.0;

        if (Xbox.leftBumper().getAsBoolean()) {
            speedLimiter = 1.0 / 3.0;
        }
        else if (Xbox.rightBumper().getAsBoolean()) {
            speedLimiter = 1.0;
        }

        return speedLimiter;
    }

    public static int getDrivePovAngle() {
        return Xbox.getHID().getPOV();
    }

    public static double getDriveLeftHeadingAdjustmentPercent() {
        return Cat5Utils.deadband(Xbox.getLeftTriggerAxis(), OperatorConstants.XboxAxisDeadband);
    }

    public static double getDriveRightHeadingAdjustmentPercent() {
        return Cat5Utils.deadband(Xbox.getRightTriggerAxis(), OperatorConstants.XboxAxisDeadband);
    }
    //#endregion

    //#region Arm
    public static double getArmManualControlPercent() {
        return Cat5Utils.linearAxis(-Man.getY(), OperatorConstants.ManAxisDeadband);
    }

    public static double getArmCorrectionPercent() {
        return Cat5Utils.linearAxis(-Man.getY(), OperatorConstants.LargeManAxisDeadband);
    }
    //#endregion
}
