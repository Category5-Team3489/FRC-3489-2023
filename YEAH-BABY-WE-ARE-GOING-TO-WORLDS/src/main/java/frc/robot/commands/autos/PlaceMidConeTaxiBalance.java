package frc.robot.commands.autos;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Cat5Actions;
import frc.robot.commands.DriveMeters;
import frc.robot.commands.GyroBalance;
import frc.robot.enums.GamePiece;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.math.geometry.Rotation2d;

public class PlaceMidConeTaxiBalance extends SequentialCommandGroup {
    public PlaceMidConeTaxiBalance(Cat5Actions actions) {
        addCommands(
            runOnce(() -> {
                actions.navx.setHeadingOffset(Rotation2d.fromDegrees(180));
                actions.gripper.setHeldGamePiece(GamePiece.Cone);
                actions.odometry.reset();
            }),
            actions.armMid(),
            waitSeconds(2),
            actions.automation(),
            actions.waitForDriveCommand(),
            runOnce(() -> {
                actions.armHome().schedule();
                new DriveMeters(actions.drivetrain, actions.odometry, 0, 3, 180, 2.5, 0.1, 90).schedule();
            }),
            actions.waitForDriveCommand(),
            waitSeconds(0.5),
            runOnce(() -> {
                new DriveMeters(actions.drivetrain, actions.odometry, 0, 2.5, 180, 1.2, 0.05, 90).schedule();
            }),
            actions.waitForDriveCommand(),
            new GyroBalance(actions.navx, actions.drivetrain)
        );
    }
}
