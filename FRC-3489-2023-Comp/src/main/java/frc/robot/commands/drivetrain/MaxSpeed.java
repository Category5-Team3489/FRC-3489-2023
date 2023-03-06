package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class MaxSpeed extends CommandBase {
    public MaxSpeed() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void execute() {
        Drivetrain.get().setFrontLeftPercentAngle(1, 0);
        Drivetrain.get().setFrontRightPercentAngle(1, 0);
        Drivetrain.get().setBackLeftPercentAngle(1, 0);
        Drivetrain.get().setBackRightPercentAngle(1, 0);
    }
}
