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
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Cat5Utils;
import frc.robot.RobotContainer;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;

import static frc.robot.Constants.DrivetrainConstants.*;
import static frc.robot.Constants.OperatorConstants.*;

public class Drive extends CommandBase {
    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    private double omegaMetersPerSecond = 0;

    private double corLeft;
    private double corRight;
    private double maxVelocityMetersPerSecond;
    private double maxAngularVelocityRadiansPerSecond;
    private Rotation2d theta;
    private Translation2d centerOfRotation;
    private ChassisSpeeds chassisSpeeds;
    private double speedLimiter;

    private double frontLeftSteerAngleRadians = 0;
    private double frontRightSteerAngleRadians = 0;
    private double backLeftSteerAngleRadians = 0;
    private double backRightSteerAngleRadians = 0;

    private Rotation2d targetAngle = null;
    private PIDController omegaController = new PIDController(OmegaProportionalGainDegreesPerSecondPerDegreeOfError, OmegaIntegralGainDegreesPerSecondPerDegreeSecondOfError, OmegaDerivativeGainDegreesPerSecondPerDegreePerSecondOfError);

    public Drive() {
        addRequirements(Drivetrain.get());

        omegaController.enableContinuousInput(-180, 180);
        omegaController.setTolerance(OmegaToleranceDegrees / 2.0);
    }

    @Override
    public void execute() {
        maxVelocity();
        theta();

        if (DriverStation.isTeleop()) {
            xMetersPerSecond = -RobotContainer.get().xbox.getLeftY();
            xMetersPerSecond = Cat5Utils.quadraticAxis(xMetersPerSecond, XboxAxisDeadband);
            xMetersPerSecond *= maxVelocityMetersPerSecond;

            yMetersPerSecond = -RobotContainer.get().xbox.getLeftX();
            yMetersPerSecond = Cat5Utils.quadraticAxis(yMetersPerSecond, XboxAxisDeadband);
            yMetersPerSecond *= maxVelocityMetersPerSecond;

            if (xMetersPerSecond == 0 && yMetersPerSecond == 0) {
                int pov = RobotContainer.get().xbox.getHID().getPOV();
                if (pov != -1) {
                    xMetersPerSecond += Math.sin(Math.toRadians(pov)) * PovSpeedMetersPerSecond;
                    yMetersPerSecond += Math.cos(Math.toRadians(pov)) * PovSpeedMetersPerSecond;
                }
            }

            omegaMetersPerSecond = -RobotContainer.get().xbox.getRightX();
            omegaMetersPerSecond = Cat5Utils.quadraticAxis(omegaMetersPerSecond, XboxAxisDeadband);
            omegaMetersPerSecond *= maxAngularVelocityRadiansPerSecond;

            centerOfRotation();
        }

        automation();

        if (omegaMetersPerSecond == 0) {
            if (targetAngle == null) {
                targetAngle = theta;
            }

            double outputDegreesPerSecond = omegaController.calculate(theta.getDegrees(), targetAngle.getDegrees());
            outputDegreesPerSecond = MathUtil.clamp(outputDegreesPerSecond, -OmegaMaxDegreesPerSecond, OmegaMaxDegreesPerSecond);

            if (!omegaController.atSetpoint()) {
                omegaMetersPerSecond = Math.toRadians(outputDegreesPerSecond);
            }
        }
        else {
            targetAngle = theta;
            omegaController.reset();

            centerOfRotation.plus(FrontLeftMeters.times(corLeft));
            centerOfRotation.plus(FrontRightMeters.times(corRight));
        }

        omega();
        chassisSpeeds();
        speedLimiter();
        apply();
    }

    private void maxVelocity() {
        maxVelocityMetersPerSecond = Drivetrain.get().maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble();
        maxAngularVelocityRadiansPerSecond = Drivetrain.get().maxVelocityConfig.getMaxAngularVelocityRadiansPerSecond.getAsDouble();
    }

    private void theta() {
        theta = NavX2.get().getRotation();
    }

    private void centerOfRotation() {
        corLeft = RobotContainer.get().xbox.getLeftTriggerAxis();
        if (corLeft < 0.05) {
            corLeft = 0;
        }
        else if (corLeft < 0.15) {
            corLeft = 1;
        }
        else {
            corLeft = 1 + ((CenterOfRotationMaxScale - 1) * Cat5Utils.inverseLerpUnclamped(corLeft, 0.15, 1.0));
        }

        corRight = RobotContainer.get().xbox.getRightTriggerAxis();
        if (corRight < 0.05) {
            corRight = 0;
        }
        else if (corLeft < 0.15) {
            corRight = 1;
        }
        else {
            corRight = 1 + ((CenterOfRotationMaxScale - 1) * Cat5Utils.inverseLerpUnclamped(corRight, 0.15, 1.0));
        }
    }

    private void omega() {
        if (omegaMetersPerSecond == 0) {
            if (targetAngle == null) {
                targetAngle = theta;
            }

            double outputDegreesPerSecond = omegaController.calculate(theta.getDegrees(), targetAngle.getDegrees());
            outputDegreesPerSecond = MathUtil.clamp(outputDegreesPerSecond, -OmegaMaxDegreesPerSecond, OmegaMaxDegreesPerSecond);

            if (!omegaController.atSetpoint()) {
                omegaMetersPerSecond = Math.toRadians(outputDegreesPerSecond);
            }
        }
        else {
            targetAngle = theta;
            omegaController.reset();

            centerOfRotation.plus(FrontLeftMeters.times(corLeft));
            centerOfRotation.plus(FrontRightMeters.times(corRight));
        }
    }

    private void chassisSpeeds() {
        chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xMetersPerSecond, yMetersPerSecond, omegaMetersPerSecond, theta);
    }

    private void speedLimiter() {
        speedLimiter = 1.0 / 2.0;
        if (RobotContainer.get().xbox.leftBumper().getAsBoolean()) {
            speedLimiter = 1.0 / 3.0;
        }
        else if (RobotContainer.get().xbox.rightBumper().getAsBoolean()) {
            speedLimiter = 1.0;
        }
    }

    private void apply() {
        SwerveModuleState[] states = Kinematics.toSwerveModuleStates(chassisSpeeds, centerOfRotation);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxVelocityMetersPerSecond * speedLimiter);

        if (xMetersPerSecond != 0 || yMetersPerSecond != 0 || omegaMetersPerSecond != 0) {
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
    }

    @Override
    public void end(boolean interrupted) {
        xMetersPerSecond = 0;
        yMetersPerSecond = 0;
        omegaMetersPerSecond = 0;

        corLeft = 0;
        corRight = 0;
        maxVelocityMetersPerSecond = 0;
        maxAngularVelocityRadiansPerSecond = 0;
        theta = null;
        centerOfRotation = null;
        chassisSpeeds = null;
        speedLimiter = 0;

        frontLeftSteerAngleRadians = 0;
        frontRightSteerAngleRadians = 0;
        backLeftSteerAngleRadians= 0;
        backRightSteerAngleRadians = 0;

        targetAngle = null;
        omegaController.reset();

        resetAutomation();
    }

    //#region Public
    public void setTargetAngle(Rotation2d targetAngle) {
        this.targetAngle = targetAngle;
    }
    //#endregion

    //#region Automation
    private Trigger automateTrigger = RobotContainer.get().man.button(5).debounce(0.1, DebounceType.kFalling);

    private PIDController centerConeNodeController = new PIDController(0.12, 0, 0);
    private PIDController distanceConeNodeController = new PIDController(0.12, 0, 0);

    private void automation() {
        boolean isBeingTeleoperated = xMetersPerSecond == 0 && yMetersPerSecond == 0 && omegaMetersPerSecond == 0;

        if (!isBeingTeleoperated || !automateTrigger.getAsBoolean()) {
            resetAutomation();
            return;
        }

        targetAngle = Rotation2d.fromDegrees(-180);
        Limelight.get().setDesiredPipeline(LimelightConstants.MidRetroreflectivePipelineIndex);

        if (!Limelight.get().isActivePipeline(LimelightConstants.MidRetroreflectivePipelineIndex)) {
            resetAutomation();
            return;
        }

        double targetX = Limelight.get().getTargetX();
        double targetY = Limelight.get().getTargetY();
        yMetersPerSecond = centerConeNodeController.calculate(-targetX, 3.54); // 3.9
        yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -0.5, 0.5); // 0.5
        xMetersPerSecond = distanceConeNodeController.calculate(targetY, -14.7); // -6.6 works
        xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -0.75, 0.75); // 0.75
    }

    private void resetAutomation() {
        centerConeNodeController.reset();
        distanceConeNodeController.reset();
    }
    //#endregion
}
