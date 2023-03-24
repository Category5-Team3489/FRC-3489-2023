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
    private static double ProportionalGain = 0.22;//0.18
    private static double MaxStrafeMetersPerSecond = 0.5;
    private static double MaxDistanceMetersPerSecond = 0.75;
    private static double StrafeToleranceDegrees = 1.25;
    private static double DistanceToleranceDegrees = 1.25 / 1.5;//1.25/2
    private static Rotation2d TargetAngle = Rotation2d.fromDegrees(180);
    private static double SpeedLimiter = 0.5;
    private static double MaxOmegaDegreesPerSecond = 90;
    private static double TargetXSetpointDegrees = -4.84;
    private static double TargetYSetpointDegrees = -4.85;

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
            yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -MaxStrafeMetersPerSecond, MaxStrafeMetersPerSecond);
        }
        else {
            yMetersPerSecond = 0;
        }

        double targetY = Limelight.get().getTargetY();
        if (!Double.isNaN(targetY)) {
            xMetersPerSecond = distanceController.calculate(targetY, TargetYSetpointDegrees);
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

        Limelight.get().log();
    }
}
