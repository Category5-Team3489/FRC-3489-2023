package frc.robot.commands.arm;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Arm;

public class GotoHome extends CommandBase {
    public GotoHome() {
        addRequirements(Arm.get());
    }

    @Override
    public void execute() {
        Arm.get().gotoHome();
    }
}
