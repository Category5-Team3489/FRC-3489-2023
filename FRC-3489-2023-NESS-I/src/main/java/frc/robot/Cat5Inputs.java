package frc.robot;

import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.enums.GamePiece;

public class Cat5Inputs {
    // Constants
    private static final int XboxPort = 0;
    private static final int ManPort = 1;

    private static final double XboxAxisDeadband = 0.05;
    private static final double ManAxisDeadband = 0.1;
    private static final double ManLargeAxisDeadband = 0.5;

    private static final double XboxTeleoperationAxisThreshold = 0.1;
    private static final double ManGamePieceIndicatorAxisThreshold = 0.8;

    public static final double ManButtonDebounceSeconds = 0.1;

    //#region Buttons
    public static final int AutomateManButton = 5;

    public static final int GripperStopManButton = 1;
    public static final int GripperIntakeManButton = 2;
    public static final int GripperOuttakeManButton = 3;

    public static final int WristPickupManButton = 4;
    public static final int WristCarryManButton = 6;

    public static final int HomeManButton = 10;
    public static final int DoubleSubstationManButton = 8;
    public static final int FloorManButton = 12;
    public static final int LowManButton = 11;
    public static final int MidManButton = 9;
    public static final int HighManButton = 7;
    //#endregion

    // Devices
    public static final CommandXboxController Xbox = new CommandXboxController(XboxPort);
    public static final CommandJoystick Man = new CommandJoystick(ManPort);

    //#region Drive
    public static double getDriveXPercent() {
        return Cat5Utils.quadraticAxis(-Xbox.getLeftY(), XboxAxisDeadband);
    }

    public static double getDriveYPercent() {
        return Cat5Utils.quadraticAxis(-Xbox.getLeftX(), XboxAxisDeadband);
    }

    public static double getDriveOmegaPercent() {
        return Cat5Utils.quadraticAxis(-Xbox.getRightX(), XboxAxisDeadband);
    }

    public static boolean isBeingDriven() {
        return Math.abs(Xbox.getLeftX()) > XboxTeleoperationAxisThreshold ||
            Math.abs(Xbox.getLeftY()) > XboxTeleoperationAxisThreshold ||
            Math.abs(Xbox.getRightX()) > XboxTeleoperationAxisThreshold;
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
        return Cat5Utils.deadband(Xbox.getLeftTriggerAxis(), XboxAxisDeadband);
    }

    public static double getDriveRightHeadingAdjustmentPercent() {
        return Cat5Utils.deadband(Xbox.getRightTriggerAxis(), XboxAxisDeadband);
    }
    //#endregion

    //#region Arm
    public static double getArmManualControlPercent() {
        return Cat5Utils.linearAxis(-Man.getY(), ManAxisDeadband);
    }

    public static double getArmCorrectionPercent() {
        return Cat5Utils.linearAxis(-Man.getY(), ManLargeAxisDeadband);
    }
    //#endregion

    //#region Leds
    public static GamePiece getIndicatedGamePiece() {
        double value = Man.getThrottle();
        if (value > ManGamePieceIndicatorAxisThreshold) {
            return GamePiece.Cone;
        }
        else if (value < -ManGamePieceIndicatorAxisThreshold) {
            return GamePiece.Cube;
        }
        return GamePiece.Unknown;
    }
    //#endregion
}
