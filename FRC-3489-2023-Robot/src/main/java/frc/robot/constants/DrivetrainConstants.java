package frc.robot.constants;

import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

import frc.robot.subsystems.Drivetrain;

public class DrivetrainConstants extends ConstantsBase<Drivetrain> {
    public static final double WheelsLeftToRightMeters = 0.54;
    public static final double WheelsFrontToBackMeters = 0.54;

    public static final double TheoreticalMaxVelocityMetersPerSecond = 6380.0 / 60.0 *
        SdsModuleConfigurations.MK4_L2.getDriveReduction() *
        SdsModuleConfigurations.MK4_L2.getWheelDiameter() * Math.PI;

    public static final int FrontLeftModuleDriveMotorDeviceId = 1;
    public static final int FrontLeftModuleSteerMotorDeviceId = 2;
    public static final int FrontLeftModuleSteerEncoderDeviceId = 12;

    public static final int FrontRightModuleDriveMotorDeviceId = 3;
    public static final int FrontRightModuleSteerMotorDeviceId = 4;
    public static final int FrontRightModuleSteerEncoderDeviceId = 34;

    public static final int BackLeftModuleDriveMotorDeviceId = 7;
    public static final int BackLeftModuleSteerMotorDeviceId = 8;
    public static final int BackLeftModuleSteerEncoderDeviceId = 18;

    public static final int BackRightModuleDriveMotorDeviceId = 5;
    public static final int BackRightModuleSteerMotorDeviceId = 6;
    public static final int BackRightModuleSteerEncoderDeviceId = 56;

    public static final int ChargingStationButton = 8;

    public DrivetrainConstants(Drivetrain subsystem) {
        super(subsystem);
    }
}
