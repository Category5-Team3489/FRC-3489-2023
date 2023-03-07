package frc.robot.commands.automation;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class MidCubeNode extends CommandBase {
    private PIDController strafeController = new PIDController(0.12, 0, 0);
    private PIDController distanceController = new PIDController(0.12, 0, 0);
    
    public MidCubeNode() {
        
    }

    @Override
    public void initialize() {
        Limelight.get().setDesiredPipeline(LimelightConstants.FiducialPipeline);

        System.out.println("Mid cube node init");
    }

    @Override
    public void execute() {
        // if (!Limelight.get().isActivePipeline(LimelightConstants.FiducialPipeline)) {
        //     return;
        // }

        double targetX = Limelight.get().getTargetX();
        if (!Double.isNaN(targetX)) {
            double yMetersPerSecond = -strafeController.calculate(targetX, -3.54);
            yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -0.5, 0.5);
            Drivetrain.get().driveCommand.setYMetersPerSecond(yMetersPerSecond);   
        }

        double targetY = Limelight.get().getTargetY();
        if (!Double.isNaN(targetY)) {
            double xMetersPerSecond = distanceController.calculate(targetY, -14.7);
            xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -0.75, 0.75);
            Drivetrain.get().driveCommand.setXMetersPerSecond(xMetersPerSecond);
        }
    }

    @Override
    public boolean isFinished() {
        return !Drivetrain.get().driveCommand.isAutomating();
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("Mid cube node end");
    }
}
