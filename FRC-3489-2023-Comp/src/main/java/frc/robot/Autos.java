package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Constants.ArmConstants;
import frc.robot.commands.automation.MidConeNode;
import frc.robot.enums.GridPosition;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Gripper;

import static edu.wpi.first.wpilibj2.command.Commands.*;

import com.revrobotics.CANSparkMax.IdleMode;

public class Autos {
    public static Command getConeThenTaxiAuto() {
        return sequence(
            print("Started auto: Cone, Taxi"),
            runOnce(() -> {
                Arm.get().setTargetAngleDegrees(GridPosition.Low, ArmConstants.FloorAngleDegrees, IdleMode.kBrake);
            }),
            waitSeconds(1),
            runOnce(() -> {
                Gripper.get().intakeCommand.schedule();
            }),
            waitSeconds(1.0),
            runOnce(() -> {
                Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.AboveMidConeAngleDegrees, IdleMode.kBrake);
            }),
            waitSeconds(3),
            new MidConeNode(),
            waitSeconds(1),
            runOnce(() -> {
                Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.OnMidConeAngleDegrees, IdleMode.kBrake);
            }),
            waitSeconds(0.5),
            runOnce(() -> {
                Gripper.get().midOuttakeConeCommand.schedule();
            })
        );
    }

    public static Command getConeThenBalanceAuto() {
        return sequence(
            print("Started auto: Cone, Balance")
        );
    }
}
