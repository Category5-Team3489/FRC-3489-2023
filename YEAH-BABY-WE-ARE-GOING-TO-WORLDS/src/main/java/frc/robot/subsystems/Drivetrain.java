package frc.robot.subsystems;

import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;

public class Drivetrain {
    private static final double WheelsLeftToRightMeters = 0.54;
    private static final double WheelsFrontToBackMeters = 0.54;
    private static final double MetersPerRotation = SdsModuleConfigurations.MK4_L2.getDriveReduction() * SdsModuleConfigurations.MK4_L2.getWheelDiameter() * Math.PI;
    private static final double TheoreticalMaxVelocityMetersPerSecond = 6380.0 / 60.0 * MetersPerRotation;
    private static final double TheoreticalMaxAngularVelocityMetersPerSecond = TheoreticalMaxVelocityMetersPerSecond / Math.hypot(WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0);
}
