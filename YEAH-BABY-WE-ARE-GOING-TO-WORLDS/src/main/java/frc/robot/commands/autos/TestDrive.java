package frc.robot.commands.autos;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Cat5Actions;
import frc.robot.commands.DriveMeters;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.math.geometry.Rotation2d;

public class TestDrive extends SequentialCommandGroup {
    public TestDrive(Cat5Actions actions) {
        addCommands(
            new DriveMeters(actions.drivetrain, actions.odometry, 0, 0, 0, 2.5, 0.1, 90)
        );
    }
}
