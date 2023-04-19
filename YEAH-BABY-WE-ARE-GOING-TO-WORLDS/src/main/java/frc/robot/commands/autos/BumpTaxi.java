package frc.robot.commands.autos;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Cat5Actions;
import frc.robot.commands.DrivePercentAngleSeconds;

public class BumpTaxi extends SequentialCommandGroup {
    public BumpTaxi(Cat5Actions actions) {
        addCommands(
            // new DrivePercentAngleSeconds(actions.drivetrain, -0.4, 0, 0.125),
            // new DrivePercentAngleSeconds(actions.drivetrain, 0.4, 0, 0.125),
            // new DrivePercentAngleSeconds(actions.drivetrain, 0, 0, 4),
            // new DrivePercentAngleSeconds(actions.drivetrain, 0.12, 0, 6.75)

            new DrivePercentAngleSeconds(actions.drivetrain, -0.4, 0, 0.125),
            new DrivePercentAngleSeconds(actions.drivetrain, 0.4, 0, 0.125),
            new DrivePercentAngleSeconds(actions.drivetrain, 0, 0, 4),
            new DrivePercentAngleSeconds(actions.drivetrain, 0.12, 0, 9)
        );
    }
}
