package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.DriveToRelativePose;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Autos {
    public static Command getTaxiAuto() {
        return sequence(
            new DriveToRelativePose(0, 3, 0, 0.6, 0.5, 90)
        );
    }

    public static Command getSidewaysThenTaxiAuto() {
        return sequence(
            new DriveToRelativePose(0, 0, 90, 1, 0.05, 90),
            waitSeconds(3),
            new DriveToRelativePose(0, 5, 90, 1, 0.05, 90)
        );
    }

    public static Command getConeThenTaxiAuto() {
        return sequence(
            // print("Started auto: Cone, Taxi"),
            // runOnce(() -> {
            //     Arm.get().setTargetAngleDegrees(GridPosition.Low, ArmConstants.FloorAngleDegrees, IdleMode.kBrake);
            // }),
            // waitSeconds(1),
            // runOnce(() -> {
            //     Gripper.get().intakeCommand.schedule();
            // }),
            // waitSeconds(1.0),
            // runOnce(() -> {
            //     Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.AboveMidConeAngleDegrees, IdleMode.kBrake);
            // }),
            // waitSeconds(3),
            // new MidConeNode(),
            // waitSeconds(1),
            // runOnce(() -> {
            //     Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.OnMidConeAngleDegrees, IdleMode.kBrake);
            // }),
            // waitSeconds(0.5),
            // runOnce(() -> {
            //     Gripper.get().midOuttakeConeCommand.schedule();
            // })
        );
    }

    public static Command getConeThenBalanceAuto() {
        return sequence(
            print("Started auto: Cone, Balance")
        );
    }
}
