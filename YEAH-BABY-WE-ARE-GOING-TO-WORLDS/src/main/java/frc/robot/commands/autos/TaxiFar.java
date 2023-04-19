package frc.robot.commands.autos;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Cat5Actions;
import frc.robot.commands.DrivePercentAngleSeconds;

public class TaxiFar extends SequentialCommandGroup {
    public TaxiFar(Cat5Actions actions) {
        addCommands(
            new DrivePercentAngleSeconds(actions.drivetrain, 0.12, 0, 7.5)
        );
    }
}
