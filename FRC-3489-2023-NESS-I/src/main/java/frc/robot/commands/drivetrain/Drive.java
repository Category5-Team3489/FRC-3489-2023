package frc.robot.commands.drivetrain;

import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Inputs;
import frc.robot.Robot;
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

            Drivetrain.get().driveFieldRelative(0, 0, 0, 0, false);
            return;
        }

        double maxVelocityMetersPerSecond = Drivetrain.get().maxVelocityConfig.getMaxVelocityMetersPerSecond();
        double maxAngularVelocityRadiansPerSecond = Drivetrain.get().maxVelocityConfig.getMaxAngularVelocityRadiansPerSecond();

        double speedLimiter = Cat5Inputs.getDriveSpeedLimiterPercent();

        double xPercent = Cat5Inputs.getDriveXPercent();
        if (speedLimiter == 1.0) {
            xPercent = xRateLimiter.calculate(xPercent);
        }
        else {
            xRateLimiter.reset(0);
        }
        double xMetersPerSecond = xPercent * maxVelocityMetersPerSecond;

        double yPercent = Cat5Inputs.getDriveYPercent();
        if (speedLimiter == 1.0) {
            yPercent = yRateLimiter.calculate(yPercent);
        }
        else {
            yRateLimiter.reset(0);
        }
        double yMetersPerSecond = yPercent * maxVelocityMetersPerSecond;

        if (xMetersPerSecond == 0 && yMetersPerSecond == 0) {
            int pov = Cat5Inputs.getDrivePovAngle();
            if (pov != -1) {
                pov += 90;

                xPercent = Math.sin(Math.toRadians(pov));
                yPercent = Math.cos(Math.toRadians(pov));

                xMetersPerSecond = xPercent * PovSpeedMetersPerSecond;
                yMetersPerSecond = yPercent * PovSpeedMetersPerSecond;
            }
        }

        double omegaPercent = Cat5Inputs.getDriveOmegaPercent();
        double omegaRadiansPerSecond = omegaPercent * maxAngularVelocityRadiansPerSecond;

        if (omegaRadiansPerSecond == 0) {
            double leftHeadingAdjustmentPercent = Cat5Inputs.getDriveLeftHeadingAdjustmentPercent();
            double rightHeadingAdjustmentPercent = Cat5Inputs.getDriveRightHeadingAdjustmentPercent();
            double headingAdjustmentPercent = leftHeadingAdjustmentPercent - rightHeadingAdjustmentPercent;
            double headingAdjustmentDegrees = headingAdjustmentPercent * HeadingAdjustmentMaxDegreesPerSecond * Robot.kDefaultPeriod;
            Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, speedLimiter, null, headingAdjustmentDegrees, null);
        }
        else {
            Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, omegaRadiansPerSecond, speedLimiter, false);
        }
    }

    @Override
    public void end(boolean interrupted) {
        xRateLimiter.reset(0);
        yRateLimiter.reset(0);
    }
}