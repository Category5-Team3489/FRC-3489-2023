package frc.robot.commands.placement;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5;
import frc.robot.enums.LimelightPipeline;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class HighConeNode extends CommandBase {
    private static double ProportionalGain = 0.18;
    private static double MaxStrafeMetersPerSecond = 0.5;
    private static double StrafeToleranceDegrees = 1;
    private static Rotation2d TargetAngle = Rotation2d.fromDegrees(180);
    private static double SpeedLimiter = 0.5;
    private static double MaxOmegaDegreesPerSecond = 180; // 90
    private static double TargetXSetpointDegrees = -3.857;
    private static double WallSpeedMetersPerSecond = -0.5;
    private static double WallTimeoutSeconds = 2;

    private Timer wallTimer = new Timer();

    private final Limelight limelight;
    private final Drivetrain drivetrain;
    private PIDController strafeController = new PIDController(ProportionalGain, 0, 0);
    private SlewRateLimiter distanceRateLimiter = new SlewRateLimiter(5);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;

    private boolean hasHitStrafeSetpoint = false;

    public HighConeNode(Limelight limelight, Drivetrain drivetrain) {
        this.limelight = limelight;
        this.drivetrain = drivetrain;

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        limelight.setDesiredPipeline(LimelightPipeline.HighRetroreflective);

        strafeController.setTolerance(StrafeToleranceDegrees);

        Cat5.print(getName() + " init");
    }

    @Override
    public void execute() {
        if (!limelight.isActivePipeline(LimelightPipeline.HighRetroreflective)) {
            drivetrain.driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, MaxOmegaDegreesPerSecond);
            return;
        }

        double targetX = limelight.getTargetX();
        if (!Double.isNaN(targetX)) {
            yMetersPerSecond = -strafeController.calculate(targetX, TargetXSetpointDegrees);
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

        drivetrain.driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, MaxOmegaDegreesPerSecond);
    }

    @Override
    public boolean isFinished() {
        return wallTimer.hasElapsed(WallTimeoutSeconds);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.brakeTranslation();

        Cat5.print(getName() + " end");

        limelight.printTargetData();
    }
}
