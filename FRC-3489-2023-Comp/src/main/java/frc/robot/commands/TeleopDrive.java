package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class TeleopDrive extends CommandBase {
    public TeleopDrive() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void execute() {
        if (DriverStation.isAutonomous()) {
            // set drive motors to zero speed
            return;
        }
    }
}
