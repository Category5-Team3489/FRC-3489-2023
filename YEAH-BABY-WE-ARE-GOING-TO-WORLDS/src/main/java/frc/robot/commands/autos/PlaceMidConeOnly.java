package frc.robot.commands.autos;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Cat5Actions;
import frc.robot.commands.DriveMeters;
import frc.robot.enums.GamePiece;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;

public class PlaceMidConeOnly extends SequentialCommandGroup {
    public PlaceMidConeOnly(Cat5Actions actions) {
        addCommands(
            runOnce(() -> {
                actions.navx.setHeadingOffset(Rotation2d.fromDegrees(180));
                actions.gripper.setHeldGamePiece(GamePiece.Cone);
                actions.odometry.resetWithPosition(0, 0);
            }), 
            actions.armMid(),
            waitSeconds(2),
            actions.automation(),
            actions.waitUntilDriving(),
            runOnce(() -> {
                actions.armHome(true).schedule();
                new DriveMeters(actions.drivetrain, actions.odometry, 0, 3, 180, 2.5, 0.1, 90).schedule();
            }),
            actions.waitUntilDriving()
            //new DriveMeters(actions.drivetrain, actions.odometry, 0, 0, 0, 0, 0, 0)
        );
    }
}
