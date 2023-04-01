package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Utils;
import frc.robot.enums.LimelightPipeline;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class MidConeNode extends CommandBase {
    private static double ProportionalGain = 0.2; // 0.25, 0.3,0.2, 0.1 too fast ///// 0.05
    private static double MaxStrafeMetersPerSecond = 0.5; // 0.5, // TODO Revert back to 0.5??? /// 0.75
    private static double MaxDistanceMetersPerSecond = 1; // 0.75
    private static double StrafeToleranceDegrees = 0.27; // 0.5, 0.25, 0.2 too low
    private static double DistanceToleranceDegrees = 0.27; // 0.5, 0.25, 0.2 too low
    private static Rotation2d TargetAngle = Rotation2d.fromDegrees(180);
    private static double SpeedLimiter = 0.5;
    private static double MaxOmegaDegreesPerSecond = 90;
    private static double TargetXSetpointDegrees = -3.09;
    private static double TargetYSetpointDegrees = -5.18;
    public static double FeedforwardMetersPerSecond = 0.05; // 0.15, 0.05, 0.02 too slow

    private PIDController strafeController = new PIDController(ProportionalGain, 0, 0);
    private PIDController distanceController = new PIDController(ProportionalGain, 0, 0);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    
    public MidConeNode() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void initialize() {
        Limelight.get().setDesiredPipeline(LimelightPipeline.MidRetroreflective);

        strafeController.setTolerance(StrafeToleranceDegrees);
        distanceController.setTolerance(DistanceToleranceDegrees);

        Cat5Utils.time();
        System.out.println(getName() + " init");
    }

    @Override
    public void execute() {
        if (!Limelight.get().isActivePipeline(LimelightPipeline.MidRetroreflective)) {
            Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, 0, MaxOmegaDegreesPerSecond);
            return;
        }

        double targetX = Limelight.get().getTargetX();
        if (!Double.isNaN(targetX)) {
            yMetersPerSecond = -strafeController.calculate(targetX, TargetXSetpointDegrees);
            yMetersPerSecond += Cat5Utils.getSign(yMetersPerSecond) * FeedforwardMetersPerSecond;
            yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -MaxStrafeMetersPerSecond, MaxStrafeMetersPerSecond);
        }
        else {
            yMetersPerSecond = 0;
        }

        double targetY = Limelight.get().getTargetY();
        if (!Double.isNaN(targetY)) {
            xMetersPerSecond = distanceController.calculate(targetY, TargetYSetpointDegrees);
            xMetersPerSecond += Cat5Utils.getSign(xMetersPerSecond) * FeedforwardMetersPerSecond;
            xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -MaxDistanceMetersPerSecond, MaxDistanceMetersPerSecond);
        }
        else {
            xMetersPerSecond = 0;
        }

        Drivetrain.get().driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, 0, MaxOmegaDegreesPerSecond);
    }

    @Override
    public boolean isFinished() {
        return strafeController.atSetpoint() && distanceController.atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        Drivetrain.get().brakeTranslation();

        Cat5Utils.time();
        System.out.println(getName() + " end");

        Limelight.get().printTargetingData();
    }
}
