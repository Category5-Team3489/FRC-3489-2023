package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class Brake extends CommandBase {
    public Brake() {
        addRequirements(Drivetrain.get());
    }
}
