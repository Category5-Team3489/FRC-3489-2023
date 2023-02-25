package frc.robot.commands.arm;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Arm;

public class GotoTarget extends CommandBase {
    @Override
    public void execute() {
        boolean isTrackingTarget = Arm.get().isTrackingTarget();
        if (isTrackingTarget) {
            double targetAngle = Arm.get().getTargetAngleDegrees();
            Arm.get().gotoAngleDegrees(targetAngle);
        }
        else {
            Arm.get().brake();
        }
    }
}
