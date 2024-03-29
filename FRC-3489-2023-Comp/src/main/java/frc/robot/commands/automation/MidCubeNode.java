package frc.robot.commands.automation;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class MidCubeNode extends CommandBase {
    private static double ProportionalGain = 0.18;
    private static double MaxStrafeMetersPerSecond = 0.5;
    private static double MaxDistanceMetersPerSecond = 0.75;
    private static double StrafeToleranceDegrees = 1.5;
    private static double DistanceToleranceDegrees = 1.5;
    private static Rotation2d TargetAngle = Rotation2d.fromDegrees(180);
    private static double SpeedLimiter = 0.5;
    private static double MaxOmegaDegreesPerSecond = 90;
    private static double TargetXSetpointDegrees = -4.16;
    private static double TargetYSetpointDegrees = -11.57;

    private PIDController strafeController = new PIDController(ProportionalGain, 0, 0);
    private PIDController distanceController = new PIDController(ProportionalGain, 0, 0);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    
    public MidCubeNode() {
        // keep tx on target with x movements
        // find good z position with deadreckoning from april tags

        // could try to extract z position and cone node state info from limelight data, but would be complicated
        // target area?, then would have to worry about partially covered tape on cone nodes
        // go with simpler deadreckoning from april tags first

        // you can figure out cone node availablilityy with target area and ty

        // different pipelines with different cropping for top and bottom cone nodes??????!!??!?! YES

        // https://docs.limelightvision.io/en/latest/networktables_api.html#advanced-usage-with-raw-contours

        // TODO May want timer for how long robot has been at setpoint for and only finish after 0.25 sec or so
    }

    @Override
    public void initialize() {
        Limelight.get().setDesiredPipeline(LimelightConstants.FiducialPipeline);

        strafeController.setTolerance(StrafeToleranceDegrees);
        distanceController.setTolerance(DistanceToleranceDegrees);

        Drivetrain.get().driveCommand.setAutomationXSupplier(() -> xMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationYSupplier(() -> yMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(() -> SpeedLimiter);
        Drivetrain.get().driveCommand.setAutomationMaxOmegaSupplier(() -> MaxOmegaDegreesPerSecond);

        Drivetrain.get().driveCommand.setTargetAngle(TargetAngle);

        System.out.println("Mid cube node init");
    }

    @Override
    public void execute() {
        if (!Limelight.get().isActivePipeline(LimelightConstants.FiducialPipeline)) {
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
    }

    @Override
    public boolean isFinished() {
        return strafeController.atSetpoint() && distanceController.atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        Drivetrain.get().driveCommand.setAutomationXSupplier(null);
        Drivetrain.get().driveCommand.setAutomationYSupplier(null);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(null);
        Drivetrain.get().driveCommand.setAutomationMaxOmegaSupplier(null);

        System.out.println("Mid cube node end");
    }
}
