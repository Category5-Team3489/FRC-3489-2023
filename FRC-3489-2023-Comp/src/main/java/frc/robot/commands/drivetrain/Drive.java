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
import frc.robot.commands.automation.MidConeNode;
import frc.robot.commands.automation.MidCubeNode;
import frc.robot.enums.GridPosition;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;

import static frc.robot.Constants.DrivetrainConstants.*;
import static frc.robot.Constants.OperatorConstants.*;

public class Drive extends CommandBase {
    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    private double omegaRadiansPerSecond = 0;

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
            // TODO FIND BETTER WAY
            double x = xMetersPerSecond;
            double y = yMetersPerSecond;

            xMetersPerSecond = -RobotContainer.get().xbox.getLeftY();
            xMetersPerSecond = Cat5Utils.quadraticAxis(xMetersPerSecond, XboxAxisDeadband);
            xMetersPerSecond *= maxVelocityMetersPerSecond;

            yMetersPerSecond = -RobotContainer.get().xbox.getLeftX();
            yMetersPerSecond = Cat5Utils.quadraticAxis(yMetersPerSecond, XboxAxisDeadband);
            yMetersPerSecond *= maxVelocityMetersPerSecond;

            if (xMetersPerSecond == 0 && yMetersPerSecond == 0) {
                int pov = RobotContainer.get().xbox.getHID().getPOV();
                if (pov != -1) {
                    pov += 90;
                    xMetersPerSecond += Math.sin(Math.toRadians(pov)) * PovSpeedMetersPerSecond;
                    yMetersPerSecond += Math.cos(Math.toRadians(pov)) * PovSpeedMetersPerSecond;
                }

                xMetersPerSecond = x;
                yMetersPerSecond = y;
            }

            omegaRadiansPerSecond = -RobotContainer.get().xbox.getRightX();
            omegaRadiansPerSecond = Cat5Utils.quadraticAxis(omegaRadiansPerSecond, XboxAxisDeadband);
            omegaRadiansPerSecond *= maxAngularVelocityRadiansPerSecond;

            centerOfRotation();
        }

        // if (!isAutomating) {
        //     isAutomating = true;
        //     new MidConeNode().schedule();
        // }

        speedLimiter();

        automation();

        omega();
        // System.out.println(yMetersPerSecond);
        chassisSpeeds();
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
        centerOfRotation = new Translation2d();

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

    private void speedLimiter() {
        speedLimiter = 1.0 / 2.0;
        if (RobotContainer.get().xbox.leftBumper().getAsBoolean()) {
            speedLimiter = 1.0 / 3.0;
        }
        else if (RobotContainer.get().xbox.rightBumper().getAsBoolean()) {
            speedLimiter = 1.0;
        }
    }

    private void omega() {
        if (omegaRadiansPerSecond == 0) {
            if (targetAngle == null) {
                targetAngle = theta;
            }

            double outputDegreesPerSecond = omegaController.calculate(theta.getDegrees(), targetAngle.getDegrees());
            outputDegreesPerSecond = MathUtil.clamp(outputDegreesPerSecond, -OmegaMaxDegreesPerSecond, OmegaMaxDegreesPerSecond);

            if (!omegaController.atSetpoint()) {
                omegaRadiansPerSecond = Math.toRadians(outputDegreesPerSecond);
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
        chassisSpeeds = ChassisSpeeds.fromFieldRelativeSpeeds(xMetersPerSecond, yMetersPerSecond, omegaRadiansPerSecond, theta);
    }

    private void apply() {
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
        xMetersPerSecond = 0;
        yMetersPerSecond = 0;
        omegaRadiansPerSecond = 0;

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
    public void setXMetersPerSecond(double xMetersPerSecond) {
        this.xMetersPerSecond = xMetersPerSecond;
    }
    public void setYMetersPerSecond(double yMetersPerSecond) {
        this.yMetersPerSecond = yMetersPerSecond;
    }
    public void setSpeedLimiter(double speedLimiter) {
        this.speedLimiter = speedLimiter;
    }
    public void setTargetAngle(Rotation2d targetAngle) {
        this.targetAngle = targetAngle;
    }
    //#endregion

    //#region Automation
    private Trigger automateTrigger = RobotContainer.get().man.button(AutomateManButton);
    private Trigger stopAutomationTrigger = RobotContainer.get().man.button(StopAutomationManButton);
    
    private boolean isAutomating = false;

    // double targetX = Limelight.get().getTargetX();
    // double targetY = Limelight.get().getTargetY();
    // yMetersPerSecond = centerConeNodeController.calculate(-targetX, 3.54); // 3.9
    // yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -0.5, 0.5); // 0.5
    // xMetersPerSecond = distanceConeNodeController.calculate(targetY, -14.7); // -6.6 works
    // xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -0.75, 0.75); // 0.75

    int i = 0;

    private void automation() {
        boolean isNotBeingTeleoperated = xMetersPerSecond == 0 && yMetersPerSecond == 0 && omegaRadiansPerSecond == 0;

        if (i++ % 50 == 0) {
            System.out.println("isNotBeingTeleoperated "+ isNotBeingTeleoperated);
            System.out.println(" automateTrigger.getAsBoolean()"+  automateTrigger.getAsBoolean());
            System.out.println("!isAutomating"+ !isAutomating);
        }

        if (isNotBeingTeleoperated && automateTrigger.getAsBoolean() && !isAutomating) {
            isAutomating = true;

            switch (Arm.get().getGridPosition()) {
                case Low:
                    break;
                case Mid:
                    switch (Gripper.get().getHeldGamePiece()) {
                        case Cone:
                            System.out.println("start cone mid place");
                            new MidConeNode().schedule();
                            break;
                        case Cube:
                            System.out.println("start cube mid place");
                            new MidCubeNode().schedule();
                            break;
                        default:
                            break;
                    }
                    break;
                case High:
                    break;
            }
        }

        if (stopAutomationTrigger.getAsBoolean() || !isNotBeingTeleoperated) {
            resetAutomation();
            return;
        }
    }

    private void resetAutomation() {
        isAutomating = false;
    }

    public boolean isAutomating() {
        return isAutomating;
    }
    //#endregion
}
