package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Utils;
import frc.robot.enums.LimelightPipeline;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class HighConeNodeE extends CommandBase {
    private static double ProportionalGain = 0.18;
    private static double MaxStrafeMetersPerSecond = 0.75;
    private static double StrafeToleranceDegrees = 1;
    private static Rotation2d TargetAngle = Rotation2d.fromDegrees(180);
    private static double SpeedLimiter = 0.5;
    private static double MaxOmegaDegreesPerSecond = 180; // 90
    private static double TargetXSetpointDegrees = -3.857;
    private static double WallSpeedMetersPerSecond = -0.5;
    private static double WallTimeoutSeconds = 2;
    public static double FeedforwardMetersPerSecond = 0.25; // 0.1

    private Timer wallTimer = new Timer();

    private PIDController strafeController = new PIDController(ProportionalGain, 0, 0);
    private SlewRateLimiter distanceRateLimiter = new SlewRateLimiter(5);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;

    private boolean hasHitStrafeSetpoint = false;

    public HighConeNodeE() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void initialize() {
        Limelight.get().setDesiredPipeline(LimelightPipeline.HighRetroreflective);

        strafeController.setTolerance(StrafeToleranceDegrees);

        Cat5Utils.time();
        System.out.println(getName() + " init");
    }

    @Override
    public void execute() {
        if (!Limelight.get().isActivePipeline(LimelightPipeline.HighRetroreflective)) {
            Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, 0, MaxOmegaDegreesPerSecond);
            return;
        }

        double targetX = Limelight.get().getTargetX();
        if (!Double.isNaN(targetX)) {
            yMetersPerSecond = -strafeController.calculate(targetX, TargetXSetpointDegrees);
            yMetersPerSecond = Cat5Utils.getSign(yMetersPerSecond) * FeedforwardMetersPerSecond;
            yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -MaxStrafeMetersPerSecond, MaxStrafeMetersPerSecond);
        }
        else {
            yMetersPerSecond = 0;
        }

        if (strafeController.atSetpoint() && !hasHitStrafeSetpoint) {
            hasHitStrafeSetpoint = true;

            wallTimer.restart();
        }

        if (hasHitStrafeSetpoint) {
            xMetersPerSecond = distanceRateLimiter.calculate(WallSpeedMetersPerSecond);
        }

        Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, 0, MaxOmegaDegreesPerSecond);
    }

    @Override
    public boolean isFinished() {
        return wallTimer.hasElapsed(WallTimeoutSeconds);
    }

    @Override
    public void end(boolean interrupted) {
        Drivetrain.get().brakeTranslation();

        Cat5Utils.time();
        System.out.println(getName() + " end");

        Limelight.get().printTargetingData();
    }
}
