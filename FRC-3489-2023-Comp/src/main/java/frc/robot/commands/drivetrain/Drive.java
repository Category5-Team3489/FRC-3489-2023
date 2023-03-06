package frc.robot.commands.drivetrain;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Utils;
import frc.robot.RobotContainer;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;

import static frc.robot.Constants.DrivetrainConstants.*;
import static frc.robot.Constants.OperatorConstants.*;

public class Drive extends CommandBase {
    private double frontLeftSteerAngleRadians = 0;
    private double frontRightSteerAngleRadians = 0;
    private double backLeftSteerAngleRadians = 0;
    private double backRightSteerAngleRadians = 0;

    private Rotation2d targetAngle = null;
    private PIDController omegaController = new PIDController(OmegaProportionalGainDegreesPerSecondPerDegreeOfError, OmegaIntegralGainDegreesPerSecondPerDegreeSecondOfError, OmegaDerivativeGainDegreesPerSecondPerDegreePerSecondOfError);

    private PIDController centerConeNodeController = new PIDController(0.12, 0, 0); // 0.05
    private PIDController distanceConeNodeController = new PIDController(0.12, 0, 0);

    private double autoX = 0;
    private double autoY = 0;

    public Drive() {
        addRequirements(Drivetrain.get());

        omegaController.enableContinuousInput(-180, 180);
        omegaController.setTolerance(OmegaToleranceDegrees / 2.0);

        // centerConeNodeController.setTolerance(0.5 / 2.0);
        // distanceConeNodeController.setTolerance(0.5 / 2.0);
    }

    @Override
    public void execute() {
        double maxVelocityMetersPerSecond = Drivetrain.get().maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble();
        double maxAngularVelocityRadiansPerSecond = Drivetrain.get().maxVelocityConfig.getMaxAngularVelocityRadiansPerSecond.getAsDouble();
        
        double x = 0;
        double y = 0;
        double omega = 0;

        Rotation2d theta = NavX2.get().getRotation();
        Translation2d centerOfRotation = new Translation2d();

        if (DriverStation.isAutonomous()) {
            x = autoX;
            y = autoY;
        }
        else {            
            //#region Input
            x = -RobotContainer.get().xbox.getLeftY();
            x = Cat5Utils.quadraticAxis(x, XboxAxisDeadband);
            x *= maxVelocityMetersPerSecond;

            y = -RobotContainer.get().xbox.getLeftX();
            y = Cat5Utils.quadraticAxis(y, XboxAxisDeadband);
            y *= maxVelocityMetersPerSecond;

            omega = -RobotContainer.get().xbox.getRightX();
            omega = Cat5Utils.quadraticAxis(omega, XboxAxisDeadband);
            omega *= maxAngularVelocityRadiansPerSecond;
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
            //#endregion
        }

        if (x == 0 && y == 0 && omega == 0) {
            // .debounce(0.2, DebounceType.kBoth)
            if (RobotContainer.get().man.button(CenterConeNodeManButton).getAsBoolean()) {
                targetAngle = Rotation2d.fromDegrees(-180);
                // if (Limelight.get().isPipeline(LimelightConstants.MidRetroreflectivePipelineIndex)) {
                double targetX = Limelight.get().getTargetX();
                double targetY = Limelight.get().getTargetY();
                y = centerConeNodeController.calculate(-targetX, 3.54); // 3.9
                y = MathUtil.clamp(y, -0.5, 0.5); // 0.5
                x = distanceConeNodeController.calculate(targetY, -14.7); // -6.6 works
                x = MathUtil.clamp(x, -0.75, 0.75); // 0.75
                // }
            }
            else {
                centerConeNodeController.reset();
                distanceConeNodeController.reset();
            }
        }
        else {
            centerConeNodeController.reset();
            distanceConeNodeController.reset();
        }

        if (omega == 0) {
            if (targetAngle == null) {
                targetAngle = NavX2.get().getRotation();
            }

            double desiredDegreesPerSecond = omegaController.calculate(theta.getDegrees(), targetAngle.getDegrees());
            desiredDegreesPerSecond = MathUtil.clamp(desiredDegreesPerSecond, -OmegaMaxDegreesPerSecond, OmegaMaxDegreesPerSecond);

            if (!omegaController.atSetpoint()) {
                omega = Math.toRadians(desiredDegreesPerSecond);
            }
        }
        else {
            targetAngle = NavX2.get().getRotation();
            omegaController.reset();

            // centerOfRotation.plus(FrontLeftMeters.times(corLeft));
            // centerOfRotation.plus(FrontRightMeters.times(corRight));
        }

        ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(x, y, omega, theta);

        double speedLimiter = 0.5;
        if (RobotContainer.get().xbox.leftBumper().getAsBoolean()) {
            speedLimiter = 1.0 / 3.0;
        }
        else if (RobotContainer.get().xbox.rightBumper().getAsBoolean()) {
            speedLimiter = 1.0;
        }

        //#region Apply
        SwerveModuleState[] states = Kinematics.toSwerveModuleStates(chassisSpeeds, centerOfRotation);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxVelocityMetersPerSecond * speedLimiter);

        if (x != 0 || y != 0 || omega != 0) {
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

        targetAngle = null;
    }

    //#region Public
    public void setTargetAngle(Rotation2d targetAngle) {
        this.targetAngle = targetAngle;
    }

    public void setAutoX(double x) {
        autoX = x;
    }
    public void setAutoY(double y) {
        autoY = y;
    }
    //#endregion
}
