package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class BrakeRotation extends CommandBase {
    public BrakeRotation() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void execute() {
        Drivetrain.get().brakeRotation();
    }
}
