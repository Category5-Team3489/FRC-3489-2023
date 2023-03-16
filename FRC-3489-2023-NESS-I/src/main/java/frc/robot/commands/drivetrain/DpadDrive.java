package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class DpadDrive extends CommandBase {
    public DpadDrive() {
        addRequirements(Drivetrain.get());
    }

    //#region Public
    public void supplyDirection(int directionDegrees) {

    }
    //#endregion
}