package frc.robot.subsystems;

import com.swervedrivespecialties.swervelib.Mk4SwerveModuleHelper;
import com.swervedrivespecialties.swervelib.SdsModuleConfigurations;
import com.swervedrivespecialties.swervelib.SwerveModule;

import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Cat5Math;

import static frc.robot.Constants.DrivetrainConstants.*;

public class Drivetrain extends SubsystemBase {
    /**
     * The maximum voltage that will be delivered to the drive motors.
     * <p>
     * This can be reduced to cap the robot's maximum speed. Typically, this is useful during initial testing of the robot.
     */
    public static final double MaxVoltage = 12.0;

    //  The formula for calculating the theoretical maximum velocity is:
    //   <Motor free speed RPM> / 60 * <Drive reduction> * <Wheel diameter meters> * pi
    //  By default this value is setup for a Mk3 standard module using Falcon500s to drive.
    //  An example of this constant for a Mk4 L2 module with NEOs to drive is:
    //   5880.0 / 60.0 / SdsModuleConfigurations.MK4_L2.getDriveReduction() * SdsModuleConfigurations.MK4_L2.getWheelDiameter() * Math.PI
    /**
     * The maximum velocity of the robot in meters per second.
     * <p>
     * This is a measure of how fast the robot should be able to drive in a straight line.
     */
    public static final double MaxVelocityMetersPerSecond = 6380.0 / 60.0 *
        SdsModuleConfigurations.MK4_L2.getDriveReduction() *
        SdsModuleConfigurations.MK4_L2.getWheelDiameter() * Math.PI;

    /**
     * The maximum angular velocity of the robot in radians per second.
     * <p>
     * This is a measure of how fast the robot can rotate in place.
     */
    // Here we calculate the theoretical maximum angular velocity. You can also replace this with a measured amount.
    public static final double MaxAngularVelocityRadiansPerSecond = MaxVelocityMetersPerSecond /
        Math.hypot(DrivetrainTrackwidthMeters / 2.0, DrivetrainWheelbaseMeters / 2.0);

    private final SwerveDriveKinematics kinematics = new SwerveDriveKinematics(
        // Front left
        new Translation2d(DrivetrainTrackwidthMeters / 2.0, DrivetrainWheelbaseMeters / 2.0),
        // Front right
        new Translation2d(DrivetrainTrackwidthMeters / 2.0, -DrivetrainWheelbaseMeters / 2.0),
        // Back left
        new Translation2d(-DrivetrainTrackwidthMeters / 2.0, DrivetrainWheelbaseMeters / 2.0),
        // Back right
        new Translation2d(-DrivetrainTrackwidthMeters / 2.0, -DrivetrainWheelbaseMeters / 2.0)
    );

    // These are our modules. We initialize them in the constructor.
    private final SwerveModule frontLeftModule;
    private final SwerveModule frontRightModule;
    private final SwerveModule backLeftModule;
    private final SwerveModule backRightModule;

    // [0, 2pi) radians
    private double frontLeftSteerAngleRadians = 0;
    private double frontRightSteerAngleRadians = 0;
    private double backLeftSteerAngleRadians = 0;
    private double backRightSteerAngleRadians = 0;

    // [0, 2pi) radians
    private double frontLeftSteerAngleOffsetRadians = 0;
    private double frontRightSteerAngleOffsetRadians = 0;
    private double backLeftSteerAngleOffsetRadians = 0;
    private double backRightSteerAngleOffsetRadians = 0;

    private ChassisSpeeds chassisSpeeds = new ChassisSpeeds(0.0, 0.0, 0.0);

    public Drivetrain() {
        ShuffleboardTab tab = Shuffleboard.getTab("Drivetrain");

        // There are 4 methods you can call to create your swerve modules.
        // The method you use depends on what motors you are using.
        //
        // Mk3SwerveModuleHelper.createFalcon500(...)
        //   Your module has two Falcon 500s on it. One for steering and one for driving.
        //
        // Mk3SwerveModuleHelper.createNeo(...)
        //   Your module has two NEOs on it. One for steering and one for driving.
        //
        // Mk3SwerveModuleHelper.createFalcon500Neo(...)
        //   Your module has a Falcon 500 and a NEO on it. The Falcon 500 is for driving and the NEO is for steering.
        //
        // Mk3SwerveModuleHelper.createNeoFalcon500(...)
        //   Your module has a NEO and a Falcon 500 on it. The NEO is for driving and the Falcon 500 is for steering.
        //
        // Similar helpers also exist for Mk4 modules using the Mk4SwerveModuleHelper class.

        // By default we will use Falcon 500s in standard configuration. But if you use a different configuration or motors
        // you MUST change it. If you do not, your code will crash on startup.
        frontLeftModule = Mk4SwerveModuleHelper.createFalcon500(
        // This parameter is optional, but will allow you to see the current state of the module on the dashboard.
        tab.getLayout("Front Left Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(0, 0),
            // This can either be STANDARD or FAST depending on your gear configuration
            Mk4SwerveModuleHelper.GearRatio.L2,
            // This is the ID of the drive motor
            FrontLeftModuleDriveMotorDeviceId,
            // This is the ID of the steer motor
            FrontLeftModuleSteerMotorDeviceId,
            // This is the ID of the steer encoder
            FrontLeftModuleSteerEncoderDeviceId,
            // This is how much the steer encoder is offset from true zero (In our case, zero is facing straight forward)
            0
        );

        // We will do the same for the other modules
        frontRightModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Front Right Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(2, 0),
                Mk4SwerveModuleHelper.GearRatio.L2,
            FrontRightModuleDriveMotorDeviceId,
            FrontRightModuleSteerMotorDeviceId,
            FrontRightModuleSteerEncoderDeviceId,
            0
        );

        backLeftModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Back Left Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(4, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            BackLeftModuleDriveMotorDeviceId,
            BackLeftModuleSteerMotorDeviceId,
            BackLeftModuleSteerEncoderDeviceId,
            0
        );

        backRightModule = Mk4SwerveModuleHelper.createFalcon500(
            tab.getLayout("Back Right Module", BuiltInLayouts.kList)
                .withSize(2, 4)
                .withPosition(6, 0),
            Mk4SwerveModuleHelper.GearRatio.L2,
            BackRightModuleDriveMotorDeviceId,
            BackRightModuleSteerMotorDeviceId,
            BackRightModuleSteerEncoderDeviceId,
            0
        );
    }

    public void drive(ChassisSpeeds chassisSpeeds) {
        this.chassisSpeeds = chassisSpeeds;
    }

    @Override
    public void periodic() {
        
    }

    private void driveChassisSpeeds() {
        SwerveModuleState[] states = kinematics.toSwerveModuleStates(chassisSpeeds);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, MaxVelocityMetersPerSecond);

        if (chassisSpeeds.vxMetersPerSecond != 0 ||
            chassisSpeeds.vyMetersPerSecond != 0 ||
            chassisSpeeds.omegaRadiansPerSecond != 0) {
            frontLeftSteerAngleRadians = states[0].angle.getRadians();
            frontRightSteerAngleRadians = states[1].angle.getRadians();
            backLeftSteerAngleRadians = states[2].angle.getRadians();
            backRightSteerAngleRadians = states[3].angle.getRadians();
        }

        frontLeftModule.set(states[0].speedMetersPerSecond / MaxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(frontLeftSteerAngleRadians, frontLeftSteerAngleOffsetRadians));
        frontRightModule.set(states[1].speedMetersPerSecond / MaxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(frontRightSteerAngleRadians, frontRightSteerAngleOffsetRadians));
        backLeftModule.set(states[2].speedMetersPerSecond / MaxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(backLeftSteerAngleRadians, backLeftSteerAngleOffsetRadians));
        backRightModule.set(states[3].speedMetersPerSecond / MaxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(backRightSteerAngleRadians, backRightSteerAngleOffsetRadians));
    }

    // private void drivePercentAngle(double speedPercent, double angleRadians) {
    //     driveSpeedAngle(speedPercent * MaxVelocityMetersPerSecond, angleRadians);
    // }

    private void driveSpeedAngle(double speedMetersPerSecond, double angleRadians) {
        frontLeftModule.set(speedMetersPerSecond / MaxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(angleRadians, frontLeftSteerAngleOffsetRadians));
        frontRightModule.set(speedMetersPerSecond / MaxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(angleRadians, frontRightSteerAngleOffsetRadians));
        backLeftModule.set(speedMetersPerSecond / MaxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(angleRadians, backLeftSteerAngleOffsetRadians));
        backRightModule.set(speedMetersPerSecond / MaxVelocityMetersPerSecond * MaxVoltage, Cat5Math.offsetAngle(angleRadians, backRightSteerAngleOffsetRadians));
    }
}
