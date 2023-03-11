package frc.robot.commands.automation;

import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Constants.LimelightConstants;
import frc.robot.commands.DriveToRelativePose;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class HighCubeNode extends CommandBase {
    // private final DriveToRelativePose command = null;
    
    // public HighCubeNode() {

    // }

    // @Override
    // public void initialize() {
    //     Limelight.get().setDesiredPipeline(LimelightConstants.FiducialPipeline);
    // }

    // @Override
    // public void execute() {
    //     if (!Drivetrain.get().driveCommand.isAutomationAllowed()) {
    //         cancel();
    //         return;
    //     }

    //     if (!Limelight.get().isActivePipeline(LimelightConstants.FiducialPipeline)) {
    //         return;
    //     }

    //     Pose3d campose = Limelight.get().getCampose();
    //     if (campose == null) {
    //         return;
    //     }

        
    // }

    // @Override
    // public boolean isFinished() {
    //     return false;
    // }

    // @Override
    // public void end(boolean interrupted) {

    // }
}
