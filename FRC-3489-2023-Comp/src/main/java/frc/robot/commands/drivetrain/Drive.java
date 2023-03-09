package frc.robot.commands.drivetrain;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

import frc.robot.Cat5Utils;
import frc.robot.RobotContainer;
import frc.robot.Constants.ArmConstants;
import frc.robot.commands.DriveToRelativePose;
import frc.robot.commands.automation.HighConeNode;
import frc.robot.commands.automation.HighCubeNode;
import frc.robot.commands.automation.MidConeNode;
import frc.robot.commands.automation.MidCubeNode;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.NavX2;

import static frc.robot.Constants.DrivetrainConstants.*;
import static frc.robot.Constants.OperatorConstants.*;

import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax.IdleMode;

public class Drive extends CommandBase {
    private double frontLeftSteerAngleRadians = 0;
    private double frontRightSteerAngleRadians = 0;
    private double backLeftSteerAngleRadians = 0;
    private double backRightSteerAngleRadians = 0;

    private SlewRateLimiter xRateLimiter = new SlewRateLimiter(XYRateLimiterPercentPerSecond);
    private SlewRateLimiter yRateLimiter = new SlewRateLimiter(XYRateLimiterPercentPerSecond);

    private Rotation2d targetAngle = null;
    private PIDController omegaController = new PIDController(OmegaProportionalGainDegreesPerSecondPerDegreeOfError, OmegaIntegralGainDegreesPerSecondPerDegreeSecondOfError, OmegaDerivativeGainDegreesPerSecondPerDegreePerSecondOfError);

    private boolean isAutomating = false;
    private DoubleSupplier automationXSupplier = null;
    private DoubleSupplier automationYSupplier = null;
    private DoubleSupplier automationSpeedLimiterSupplier = null;

    private Trigger automateTrigger = RobotContainer.get().man.button(AutomateManButton);
    private Trigger stopAutomationTrigger = RobotContainer.get().man.button(StopAutomationManButton);

    public Drive() {
        addRequirements(Drivetrain.get());

        omegaController.enableContinuousInput(-180, 180);
        omegaController.setTolerance(OmegaToleranceDegrees / 2.0);

        //#region Bindings
        new Trigger(() -> isAutomating)
            .onFalse(Commands.runOnce(() -> {
                frontLeftSteerAngleRadians = 0;
                frontRightSteerAngleRadians = 0;
                backLeftSteerAngleRadians= 0;
                backRightSteerAngleRadians = 0;

                xRateLimiter.reset(0);
                yRateLimiter.reset(0);

                targetAngle = null;
                omegaController.reset();

                isAutomating = false;
                automationXSupplier = null;
                automationYSupplier = null;
                automationSpeedLimiterSupplier = null;
            }));
            
        //#endregion
    }

    @Override
    public void execute() {
        double maxVelocityMetersPerSecond = Drivetrain.get().maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble();
        double maxAngularVelocityRadiansPerSecond = Drivetrain.get().maxVelocityConfig.getMaxAngularVelocityRadiansPerSecond.getAsDouble();
    
        Rotation2d theta = NavX2.get().getRotation();
        Translation2d centerOfRotation = new Translation2d();

        double xMetersPerSecond = 0;
        double yMetersPerSecond = 0;
        double omegaRadiansPerSecond = 0;

        double corLeft = 0;
        double corRight = 0;

        double speedLimiter = 1.0 / 2.0;

        if (DriverStation.isTeleop()) {
            if (RobotContainer.get().xbox.leftBumper().getAsBoolean()) {
                speedLimiter = 1.0 / 3.0;
            }
            else if (RobotContainer.get().xbox.rightBumper().getAsBoolean()) {
                speedLimiter = 1.0;
            }

            xMetersPerSecond = -RobotContainer.get().xbox.getLeftY();
            xMetersPerSecond = Cat5Utils.quadraticAxis(xMetersPerSecond, XboxAxisDeadband);
            if (speedLimiter == 1.0) {
                xMetersPerSecond = xRateLimiter.calculate(xMetersPerSecond);
            }
            else {
                xRateLimiter.reset(0);
            }
            xMetersPerSecond *= maxVelocityMetersPerSecond;

            yMetersPerSecond = -RobotContainer.get().xbox.getLeftX();
            yMetersPerSecond = Cat5Utils.quadraticAxis(yMetersPerSecond, XboxAxisDeadband);
            if (speedLimiter == 1.0) {
                yMetersPerSecond = yRateLimiter.calculate(yMetersPerSecond);
            }
            else {
                yRateLimiter.reset(0);
            }
            yMetersPerSecond *= maxVelocityMetersPerSecond;

            if (xMetersPerSecond == 0 && yMetersPerSecond == 0) {
                int pov = RobotContainer.get().xbox.getHID().getPOV();
                if (pov != -1) {
                    pov += 90;
                    
                    xMetersPerSecond += Math.sin(Math.toRadians(pov));
                    yMetersPerSecond += Math.cos(Math.toRadians(pov));

                    double speedMetersPerSecond = Math.sqrt((xMetersPerSecond * xMetersPerSecond) + (yMetersPerSecond * yMetersPerSecond));
                    xMetersPerSecond /= speedMetersPerSecond;
                    yMetersPerSecond /= speedMetersPerSecond;

                    xMetersPerSecond *= PovSpeedMetersPerSecond;
                    yMetersPerSecond *= PovSpeedMetersPerSecond;
                }
            }

            omegaRadiansPerSecond = -RobotContainer.get().xbox.getRightX();
            omegaRadiansPerSecond = Cat5Utils.quadraticAxis(omegaRadiansPerSecond, XboxAxisDeadband);
            omegaRadiansPerSecond *= maxAngularVelocityRadiansPerSecond;

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
        else {
            xRateLimiter.reset(0);
            yRateLimiter.reset(0);
        }

        boolean isNotBeingTeleoperated = xMetersPerSecond == 0 && yMetersPerSecond == 0 && omegaRadiansPerSecond == 0;
        
        if (DriverStation.isTeleop()) {
            if (isNotBeingTeleoperated && automateTrigger.getAsBoolean() && !isAutomating) {
                isAutomating = true;

                enableTeleopAutomation();
            }

            if (stopAutomationTrigger.getAsBoolean() || !isNotBeingTeleoperated) {
                isAutomating = false;
            }
        }

        if (isAutomationAllowed()) {
            if (automationXSupplier != null) {
                xMetersPerSecond = automationXSupplier.getAsDouble();
            }
            if (automationYSupplier != null) {
                yMetersPerSecond = automationYSupplier.getAsDouble();
            }
            if (automationSpeedLimiterSupplier != null) {
                speedLimiter = automationSpeedLimiterSupplier.getAsDouble();
            }
        }

        if (omegaRadiansPerSecond == 0) {
            if (targetAngle == null) {
                targetAngle = theta;
            }

            double outputDegreesPerSecond = omegaController.calculate(theta.getDegrees(), targetAngle.getDegrees());
            outputDegreesPerSecond = MathUtil.clamp(outputDegreesPerSecond, -OmegaMaxDegreesPerSecond, OmegaMaxDegreesPerSecond);

            if (!omegaController.atSetpoint()) {
                omegaRadiansPerSecond = Math.toRadians(outputDegreesPerSecond);
                omegaRadiansPerSecond = MathUtil.clamp(omegaRadiansPerSecond, -maxAngularVelocityRadiansPerSecond, maxAngularVelocityRadiansPerSecond);
            }
        }
        else {
            targetAngle = theta;
            omegaController.reset();

            centerOfRotation.plus(FrontLeftMeters.times(corLeft));
            centerOfRotation.plus(FrontRightMeters.times(corRight));
        }

        ChassisSpeeds chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xMetersPerSecond, yMetersPerSecond, omegaRadiansPerSecond, theta);
        
        SwerveModuleState[] states = Kinematics.toSwerveModuleStates(chassisSpeeds, centerOfRotation);
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxVelocityMetersPerSecond * speedLimiter);

        if (xMetersPerSecond != 0 || yMetersPerSecond != 0 || omegaRadiansPerSecond != 0) {
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
        frontLeftSteerAngleRadians = 0;
        frontRightSteerAngleRadians = 0;
        backLeftSteerAngleRadians= 0;
        backRightSteerAngleRadians = 0;

        xRateLimiter.reset(0);
        yRateLimiter.reset(0);

        targetAngle = null;
        omegaController.reset();

        isAutomating = false;
        automationXSupplier = null;
        automationYSupplier = null;
        automationSpeedLimiterSupplier = null;
    }

    //#region Public
    public void setTargetAngle(Rotation2d targetAngle) {
        this.targetAngle = targetAngle;
    }

    public boolean isAutomationAllowed() {
        return DriverStation.isAutonomous() || isAutomating;
    }
    public void setAutomationXSupplier(DoubleSupplier automationXSupplier) {
        this.automationXSupplier = automationXSupplier;
    }
    public void setAutomationYSupplier(DoubleSupplier automationYSupplier) {
        this.automationYSupplier = automationYSupplier;
    }
    public void setAutomationSpeedLimiterSupplier(DoubleSupplier automationSpeedLimiterSupplier) {
        this.automationSpeedLimiterSupplier = automationSpeedLimiterSupplier;
    }
    //#endregion

    private void enableTeleopAutomation() {
        // Use down, mid, up pov and automation button
        // Get camerapose_targetspace,
        // Have individual commands use limelight more directly
        // Less abstraction

        // Pose Estimator turn right = minus
        // -Y = right
        // +X = forward

        switch (Arm.get().getGridPosition()) {
            case Low:
                Commands.sequence(
                    Commands.print("Test drive to relative pose start"),
                    new DriveToRelativePose(new Pose2d(0, 1, Rotation2d.fromDegrees(0)), 1.0),
                    Commands.print("Test drive to relative pose end")
                ).schedule();
                break;
            case Mid:
                switch (Gripper.get().getHeldGamePiece()) {
                    case Cone:
                        System.out.println("start cone mid place");
                        Commands.sequence(
                            new MidConeNode(),
                            Commands.waitSeconds(1),
                            Commands.runOnce(() -> {
                                Arm.get().setTargetAngleDegrees(ArmConstants.OnMidConeAngleDegrees, IdleMode.kBrake);
                            }),
                            Commands.waitSeconds(0.5),
                            Commands.runOnce(() -> {
                                Gripper.get().midOuttakeConeCommand.schedule();
                            }),
                            Commands.waitSeconds(0.5)
                        ).schedule();
                        break;
                    case Cube:
                        System.out.println("start cube mid place");
                        Commands.sequence(
                            new MidCubeNode(),
                            Commands.runOnce(() -> {
                                Gripper.get().midOuttakeCubeCommand.schedule();
                            })
                        ).schedule();
                        break;
                    default:
                        break;
                }
                break;
            case High:
                switch (Gripper.get().getHeldGamePiece()) {
                    case Cone:
                        System.out.println("start cone high place");
                        Commands.sequence(
                            new HighConeNode(),
                            Commands.waitSeconds(1),
                            Commands.runOnce(() -> {
                                Gripper.get().highOuttakeConeCommand.schedule();
                            })
                            // new DriveToRelativePose(new Pose2d(0, 1, Rotation2d.fromDegrees(0)), 0.4)
                        ).schedule();
                        break;
                    case Cube:
                        System.out.println("start cube high place");
                        Commands.sequence(
                            new HighCubeNode(),
                            new WaitCommand(1),
                            Commands.runOnce(() -> {
                                Gripper.get().highOuttakeCubeCommand.schedule();
                            })
                        ).schedule();
                        break;
                    default:
                        break;
                }
                break;
        }
    }
}
