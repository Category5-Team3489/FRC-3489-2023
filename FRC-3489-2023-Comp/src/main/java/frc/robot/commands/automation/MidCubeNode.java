package frc.robot.commands.automation;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class MidCubeNode extends CommandBase {
    private PIDController strafeController = new PIDController(0.18, 0, 0);
    private PIDController distanceController = new PIDController(0.18, 0, 0);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    
    public MidCubeNode() {
        strafeController.setTolerance(1.5);
        distanceController.setTolerance(1.5);
    }

    @Override
    public void initialize() {
        Limelight.get().setDesiredPipeline(LimelightConstants.FiducialPipeline);

        Drivetrain.get().driveCommand.setTargetAngle(Rotation2d.fromDegrees(180));
        Drivetrain.get().driveCommand.setAutomationXSupplier(() -> xMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationYSupplier(() -> yMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(() -> 0.5);

        System.out.println("Mid cube node init");
    }

    @Override
    public void execute() {
        if (!Drivetrain.get().driveCommand.isAutomationAllowed()) {
            cancel();
            return;
        }

        if (!Limelight.get().isActivePipeline(LimelightConstants.FiducialPipeline)) {
            return;
        }

        double targetX = Limelight.get().getTargetX();
        if (!Double.isNaN(targetX)) {
            yMetersPerSecond = -strafeController.calculate(targetX, -3.54);
            yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -0.5, 0.5);
        }
        else {
            yMetersPerSecond = 0;
        }

        double targetY = Limelight.get().getTargetY();
        if (!Double.isNaN(targetY)) {
            xMetersPerSecond = distanceController.calculate(targetY, -14.7);
            xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -0.75, 0.75);
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
        System.out.println("Mid cube node end");

        xMetersPerSecond = 0;
        yMetersPerSecond = 0;
    }
}
