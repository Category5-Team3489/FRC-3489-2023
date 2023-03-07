package frc.robot.commands.automation;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class MidConeNode extends CommandBase {
    private PIDController strafeController = new PIDController(0.12, 0, 0);
    private PIDController distanceController = new PIDController(0.12, 0, 0);

    private double xMetersPerSecond = 0;
    private double yMetersPerSecond = 0;
    
    public MidConeNode() {
        // keep tx on target with x movements
        // find good z position with deadreckoning from april tags

        // could try to extract z position and cone node state info from limelight data, but would be complicated
        // target area?, then would have to worry about partially covered tape on cone nodes
        // go with simpler deadreckoning from april tags first

        // you can figure out cone node availablilityy with target area and ty

        // different pipelines with different cropping for top and bottom cone nodes??????!!??!?! YES

        // https://docs.limelightvision.io/en/latest/networktables_api.html#advanced-usage-with-raw-contours

        strafeController.setTolerance(1);
        distanceController.setTolerance(1);
    }

    @Override
    public void initialize() {
        Limelight.get().setDesiredPipeline(LimelightConstants.MidRetroreflectivePipeline);

        Drivetrain.get().driveCommand.setTargetAngle(Rotation2d.fromDegrees(180));
        Drivetrain.get().driveCommand.setAutomationXSupplier(() -> xMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationYSupplier(() -> yMetersPerSecond);
        Drivetrain.get().driveCommand.setAutomationSpeedLimiterSupplier(() -> 0.5);

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
            yMetersPerSecond = -strafeController.calculate(targetX, -3.9);
            yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -0.5, 0.5);
        }
        else {
            yMetersPerSecond = 0;
        }

        double targetY = Limelight.get().getTargetY();
        if (!Double.isNaN(targetY)) {
            xMetersPerSecond = distanceController.calculate(targetY, -6.6);
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
        System.out.println("Mid cone node end");

        xMetersPerSecond = 0;
        yMetersPerSecond = 0;
    }
}
