package frc.robot.commands.automation;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class HighConeNode extends CommandBase {
    private static double ProportionalGain = 0.18;
    private static double MaxStrafeMetersPerSecond = 0.5;
    private static double MaxDistanceMetersPerSecond = 0.75;
    private static double StrafeToleranceDegrees = 1;
    private static double DistanceToleranceDegrees = 1;
    private static Rotation2d TargetAngle = Rotation2d.fromDegrees(180);
    private static double SpeedLimiter = 0.5;
    private static double MaxOmegaDegreesPerSecond = 90;
    private static double TargetXSetpointDegrees = -3.64;
    private static double TargetYSetpointDegrees = 21.49;

    private PIDController strafeController = new PIDController(ProportionalGain, 0, 0);
    private PIDController distanceController = new PIDController(ProportionalGain, 0, 0);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    
    public HighConeNode() {

    }

    @Override
    public void initialize() {
        Limelight.get().setDesiredPipeline(LimelightConstants.MidRetroreflectivePipeline);

        strafeController.setTolerance(StrafeToleranceDegrees);
        distanceController.setTolerance(DistanceToleranceDegrees);

        Drivetrain.get().driveCommand.setAutomationXSupplier(() -> xMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationYSupplier(() -> yMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(() -> SpeedLimiter);
        Drivetrain.get().driveCommand.setAutomationMaxOmegaSupplier(() -> MaxOmegaDegreesPerSecond);

        Drivetrain.get().driveCommand.setTargetAngle(TargetAngle);

        System.out.println("Mid cone node init");
    }

    @Override
    public void execute() {
        if (!Drivetrain.get().driveCommand.isAutomationAllowed()) {
            cancel();
            return;
        }

        if (!Limelight.get().isActivePipeline(LimelightConstants.MidRetroreflectivePipeline)) {
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
            xMetersPerSecond = -distanceController.calculate(targetY, TargetYSetpointDegrees);
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
        if (interrupted) {
            Drivetrain.get().driveCommand.stopAutomation();
        }

        Drivetrain.get().driveCommand.setAutomationXSupplier(null);
        Drivetrain.get().driveCommand.setAutomationYSupplier(null);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(null);
        Drivetrain.get().driveCommand.setAutomationMaxOmegaSupplier(null);

        System.out.println("Mid cone node end");
    }
}
