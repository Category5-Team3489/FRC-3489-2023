package frc.robot.commands.autos;

import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import frc.robot.Cat5Actions;
import frc.robot.commands.DriveMeters;
import frc.robot.enums.GamePiece;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import edu.wpi.first.math.geometry.Rotation2d;

public class TestDrive extends SequentialCommandGroup {
    /**
     * @param actions
     */
    public TestDrive(Cat5Actions actions) {
        addCommands(
            runOnce(() -> {
                actions.navx.setHeadingOffset(Rotation2d.fromDegrees(0));
                actions.gripper.setHeldGamePiece(GamePiece.Cone);
                actions.odometry.reset();
            }),
            new DriveMeters(actions.drivetrain, actions.odometry, 3, 0, 0, 0.5, 0.025, 90)
        );
    }
}
