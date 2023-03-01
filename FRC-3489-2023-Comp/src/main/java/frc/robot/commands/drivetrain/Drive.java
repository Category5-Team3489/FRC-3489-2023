package frc.robot.commands.drivetrain;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Utils;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.NavX2;

import static frc.robot.Constants.DrivetrainConstants.*;

public class Drive extends CommandBase {
    public Drive() {
        addRequirements(Drivetrain.get());
    }

    private double frontLeftSteerAngleRadians = 0;
    private double frontRightSteerAngleRadians = 0;
    private double backLeftSteerAngleRadians = 0;
    private double backRightSteerAngleRadians = 0;

    @Override
    public void execute() {
        if (DriverStation.isAutonomous()) {
            return;
        }

        double maxVelocityMetersPerSecond = Drivetrain.get().maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble();
        double maxAngularVelocityRadiansPerSecond = Drivetrain.get().maxVelocityConfig.getMaxAngularVelocityRadiansPerSecond.getAsDouble();

        //#region Chassis Speeds
        double x = -RobotContainer.get().xbox.getLeftY();
        x = Cat5Utils.quadraticAxis(x, XboxAxisDeadband);
        x *= maxVelocityMetersPerSecond;

        double y = -RobotContainer.get().xbox.getLeftX();
        y = Cat5Utils.quadraticAxis(y, XboxAxisDeadband);
        y *= maxVelocityMetersPerSecond;

        // Translation2d xy = new Translation2d(-RobotContainer.get().xbox.getLeftX(), -RobotContainer.get().xbox.getLeftY());
        // double xyMag = xy.getNorm();

        double omega = -RobotContainer.get().xbox.getRightX();
        omega = Cat5Utils.quadraticAxis(omega, XboxAxisDeadband);
        omega *= maxAngularVelocityRadiansPerSecond;
        // omega += 0.15 * xyMag;

        Rotation2d theta = NavX2.get().getRotation();

        ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(x, y, omega, theta);
        //#endregion

        //#region Center of Rotation
        // double corLeft = RobotContainer.get().xbox.getLeftTriggerAxis();
        // if (corLeft < 0.05) {
        //     corLeft = 0;
        // }
        // else if (corLeft < 0.15) {
        //     corLeft = 1;
        // }
        // else {
        //     corLeft = CenterOfRotationMaxScale * Cat5Utils.inverseLerpUnclamped(corLeft, 0.15, 1.0);
        // }

        // double corRight = RobotContainer.get().xbox.getRightTriggerAxis();
        // if (corRight < 0.05) {
        //     corRight = 0;
        // }
        // else if (corLeft < 0.15) {
        //     corRight = 1;
        // }
        // else {
        //     corRight = CenterOfRotationMaxScale * Cat5Utils.inverseLerpUnclamped(corRight, 0.15, 1.0);
        // }

        Translation2d centerOfRotation = new Translation2d();
        // centerOfRotation.plus(FrontLeftMeters.times(corLeft));
        // centerOfRotation.plus(FrontRightMeters.times(corRight));
        //#endregion

        //#region Apply
        SwerveModuleState[] states = Kinematics.toSwerveModuleStates(chassisSpeeds, centerOfRotation);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxVelocityMetersPerSecond);

        if (chassisSpeeds.vxMetersPerSecond != 0 ||
            chassisSpeeds.vyMetersPerSecond != 0 ||
            chassisSpeeds.omegaRadiansPerSecond != 0) {
            frontLeftSteerAngleRadians = states[0].angle.getRadians();
            frontRightSteerAngleRadians = states[1].angle.getRadians();
            backLeftSteerAngleRadians = states[2].angle.getRadians();
            backRightSteerAngleRadians = states[3].angle.getRadians();
            
            Drivetrain.get().setFrontLeftSpeedAngle(states[0].speedMetersPerSecond, frontLeftSteerAngleRadians);
            Drivetrain.get().setFrontRightSpeedAngle(states[1].speedMetersPerSecond, frontRightSteerAngleRadians);
            Drivetrain.get().setBackLeftSpeedAngle(states[2].speedMetersPerSecond, backLeftSteerAngleRadians);
            Drivetrain.get().setBackRightSpeedAngle(states[3].speedMetersPerSecond, backRightSteerAngleRadians);
        }
        else {
            Drivetrain.get().setFrontLeftPercentAngle(0, frontLeftSteerAngleRadians);
            Drivetrain.get().setFrontRightPercentAngle(0, frontRightSteerAngleRadians);
            Drivetrain.get().setBackLeftPercentAngle(0, backLeftSteerAngleRadians);
            Drivetrain.get().setBackRightPercentAngle(0, backRightSteerAngleRadians);
        }
        //#endregion
    }

    @Override
    public void end(boolean interrupted) {
        frontLeftSteerAngleRadians = 0;
        frontRightSteerAngleRadians = 0;
        backLeftSteerAngleRadians= 0;
        backRightSteerAngleRadians = 0;
    }
}
