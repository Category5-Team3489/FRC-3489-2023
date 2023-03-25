package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Utils;
import frc.robot.enums.LimelightPipeline;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class CenterFiducialWithHeading extends CommandBase {
    private static double ProportionalGain = 0.18;
    private static double MaxStrafeMetersPerSecond = 0.1;
    private static Rotation2d TargetAngle = Rotation2d.fromDegrees(180);
    private static double SpeedLimiter = 1.0;
    private static double MaxOmegaDegreesPerSecond = 90;
    public static double FeedforwardMetersPerSecond = 0.3;

    private PIDController strafeController = new PIDController(ProportionalGain, 0, 0);

    private final double xMetersPerSecond;
    private double yMetersPerSecond = 0;
    private final double seconds;
    private final Timer timer = new Timer();
    
    public CenterFiducialWithHeading(double xMetersPerSecond, double seconds) {
        addRequirements(Drivetrain.get());

        this.xMetersPerSecond = xMetersPerSecond;
        this.seconds = seconds;
    }

    @Override
    public void initialize() {
        Limelight.get().setDesiredPipeline(LimelightPipeline.Fiducial);

        Cat5Utils.time();
        System.out.println(getName() + " init");

        timer.restart();
    }

    @Override
    public void execute() {
        if (!Limelight.get().isActivePipeline(LimelightPipeline.Fiducial)) {
            Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, 0, MaxOmegaDegreesPerSecond);
            return;
        }

        double targetX = Limelight.get().getTargetX();
        if (!Double.isNaN(targetX)) {
            yMetersPerSecond = -strafeController.calculate(targetX, 0);
            yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -MaxStrafeMetersPerSecond, MaxStrafeMetersPerSecond);
        }
        else {
            yMetersPerSecond = 0;
        }

        Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, 0, MaxOmegaDegreesPerSecond);
    }

    @Override
    public boolean isFinished() {
        return timer.hasElapsed(seconds);
    }

    @Override
    public void end(boolean interrupted) {
        Drivetrain.get().brakeTranslation();
        
        Cat5Utils.time();
        System.out.println(getName() + " end");

        Limelight.get().log();
    }
}
