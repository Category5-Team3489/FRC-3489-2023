package frc.robot.commands.arm;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Arm;

public class GotoTarget extends CommandBase {
    public GotoTarget() {
        addRequirements(Arm.get());
    }

    @Override
    public void execute() {
        boolean isTrackingTarget = Arm.get().isTrackingTarget();
        if (isTrackingTarget) {
            double targetAngleDegrees = Arm.get().getTargetAngleDegrees();
            Arm.get().gotoAngleDegrees(targetAngleDegrees);
        }
        else {
            Arm.get().brake();
        }
    }
}
