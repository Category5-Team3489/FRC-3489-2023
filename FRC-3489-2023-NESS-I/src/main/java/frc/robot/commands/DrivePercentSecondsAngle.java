package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class DrivePercentSecondsAngle extends CommandBase {
    public DrivePercentSecondsAngle(double percent, double seconds, double angleDegrees) {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void execute() {
        
    }
}
