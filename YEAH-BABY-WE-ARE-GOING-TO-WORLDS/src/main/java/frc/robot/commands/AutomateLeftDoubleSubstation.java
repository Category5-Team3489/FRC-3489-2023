package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5;
import frc.robot.enums.ArmState;
import frc.robot.enums.LimelightPipeline;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class AutomateLeftDoubleSubstation extends CommandBase {
    private static double ProportionalGain = 0.18;
    private static double MaxStrafeMetersPerSecond = 0.5;
    private static double MaxDistanceMetersPerSecond = 0.75;
    private static double StrafeToleranceDegrees = 1.5;
    private static double DistanceToleranceDegrees = 1.5;
    private static Rotation2d TargetAngle = Rotation2d.fromDegrees(0);
    private static double SpeedLimiter = 0.5;
    private static double MaxOmegaDegreesPerSecond = 90;
    private static double TargetXSetpointDegrees = -0.14;
    private static double TargetYSetpointDegrees = -11.49;

    private final Arm arm;
    private final Limelight limelight;
    private final Drivetrain drivetrain;
    private PIDController strafeController = new PIDController(ProportionalGain, 0, 0);
    private PIDController distanceController = new PIDController(ProportionalGain, 0, 0);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    
    public AutomateLeftDoubleSubstation(Arm arm, Limelight limelight, Drivetrain drivetrain) {
        this.arm = arm;
        this.limelight = limelight;
        this.drivetrain = drivetrain;
        
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        limelight.setDesiredPipeline(LimelightPipeline.Fiducial);

        strafeController.setTolerance(StrafeToleranceDegrees);
        distanceController.setTolerance(DistanceToleranceDegrees);

        Cat5.print(getName() + " init");
    }

    @Override
    public void execute() {
        if (arm.getState() != ArmState.DoubleSubstation) {
            arm.setState(ArmState.DoubleSubstation);
        }

        if (!limelight.isActivePipeline(LimelightPipeline.Fiducial)) {
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

        double targetY = limelight.getTargetY();
        if (!Double.isNaN(targetY)) {
            xMetersPerSecond = distanceController.calculate(targetY, TargetYSetpointDegrees);
            xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -MaxDistanceMetersPerSecond, MaxDistanceMetersPerSecond);
        }
        else {
            xMetersPerSecond = 0;
        }

        drivetrain.driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiter, TargetAngle, MaxOmegaDegreesPerSecond);
    }

    @Override
    public boolean isFinished() {
        return strafeController.atSetpoint() && distanceController.atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.brakeTranslation();
        
        Cat5.print(getName() + " end");

        limelight.printTargetData();
    }
}
