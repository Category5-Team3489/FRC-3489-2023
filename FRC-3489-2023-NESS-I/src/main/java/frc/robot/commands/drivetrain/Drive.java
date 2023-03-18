package frc.robot.commands.drivetrain;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Drivetrain;

import static frc.robot.Constants.DrivetrainConstants.*;

public class Drive extends CommandBase {
    // State
    private SlewRateLimiter xRateLimiter = new SlewRateLimiter(XYRateLimiterPercentPerSecond);
    private SlewRateLimiter yRateLimiter = new SlewRateLimiter(XYRateLimiterPercentPerSecond);
    
    public Drive() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void execute() {
        if (!DriverStation.isTeleopEnabled()) {
            xRateLimiter.reset(0);
            yRateLimiter.reset(0);

            Drivetrain.get().resetTargetHeading();

            Drivetrain.get().driveFieldRelative(0, 0, 0);
            return;
        }

        double maxVelocityMetersPerSecond = Drivetrain.get().maxVelocityConfig.getMaxVelocityMetersPerSecond();
        double maxAngularVelocityRadiansPerSecond = Drivetrain.get().maxVelocityConfig.getMaxAngularVelocityRadiansPerSecond();

        double speedLimiter = RobotContainer.get().getDriveSpeedLimiterPercent();

        double xPercent = RobotContainer.get().getDriveXPercent();
        if (speedLimiter == 1.0) {
            xPercent = xRateLimiter.calculate(xPercent);
        }
        else {
            xRateLimiter.reset(0);
        }
        double xMetersPerSecond = xPercent * maxVelocityMetersPerSecond;

        double yPercent = RobotContainer.get().getDriveYPercent();
        if (speedLimiter == 1.0) {
            yPercent = yRateLimiter.calculate(yPercent);
        }
        else {
            yRateLimiter.reset(0);
        }
        double yMetersPerSecond = yPercent * maxVelocityMetersPerSecond;

        if (xMetersPerSecond == 0 && yMetersPerSecond == 0) {
            int pov = RobotContainer.get().getDrivePovAngle();
            if (pov != -1) {
                pov += 90;

                xPercent = Math.sin(Math.toRadians(pov));
                yPercent = Math.cos(Math.toRadians(pov));

                xMetersPerSecond = xPercent * PovSpeedMetersPerSecond;
                yMetersPerSecond = yPercent * PovSpeedMetersPerSecond;
            }
        }

        double omegaPercent = RobotContainer.get().getDriveOmegaPercent();
        double omegaRadiansPerSecond = omegaPercent * maxAngularVelocityRadiansPerSecond;

        if (omegaRadiansPerSecond == 0) {
            double leftHeadingAdjustmentPercent = RobotContainer.get().getDriveLeftHeadingAdjustmentPercent();
            double rightHeadingAdjustmentPercent = RobotContainer.get().getDriveRightHeadingAdjustmentPercent();

            double headingAdjustmentPercent = (-leftHeadingAdjustmentPercent) + rightHeadingAdjustmentPercent;
            double headingAdjustmentDegrees = headingAdjustmentPercent * HeadingAdjustmentMaxDegreesPerSecond * Robot.kDefaultPeriod;
            Drivetrain.get().adjustTargetHeading(Rotation2d.fromDegrees(headingAdjustmentDegrees));

            Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, speedLimiter);
        }
        else {
            Drivetrain.get().resetTargetHeading();

            Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, omegaRadiansPerSecond, speedLimiter);
        }
    }

    @Override
    public void end(boolean interrupted) {
        xRateLimiter.reset(0);
        yRateLimiter.reset(0);

        Drivetrain.get().resetTargetHeading();
    }
}