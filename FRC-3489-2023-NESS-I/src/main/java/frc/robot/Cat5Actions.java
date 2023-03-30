package frc.robot;

import frc.robot.Constants.WristConstants.WristState;
import frc.robot.commands.HighConeNode;
import frc.robot.commands.HighCubeNode;
import frc.robot.commands.MidConeNode;
import frc.robot.commands.MidCubeNode;
import frc.robot.enums.ArmCommand;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Wrist;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Cat5Actions {
    //#region Singleton
    private static Cat5Actions instance = new Cat5Actions();

    public static Cat5Actions get() {
        return instance;
    }
    //#endregion

    private Cat5Actions() {
        instance = this;
    }

    //#region Public
    public void scheduleLowCommand() {
        sequence(
            runOnce(() -> {
                Arm.get().command(ArmCommand.Low);
            }),
            waitSeconds(0.4),
            runOnce(() -> {
                Wrist.get().setState(WristState.Pickup);
            })
        ).schedule();
    }

    public void scheduleMidCommand(boolean shouldPlace) {
        sequence(
            runOnce(() -> {
                if (!shouldPlace) {
                    Arm.get().command(ArmCommand.None);
                }

                Arm.get().command(ArmCommand.Mid);
            }),
            waitSeconds(0.4),
            runOnce(() -> {
                Wrist.get().setState(WristState.Carry);
            })
        ).schedule();
    }

    public void scheduleHighCommand() {
        sequence(
            runOnce(() -> {
                Arm.get().command(ArmCommand.High);
            }),
            waitSeconds(0.4),
            runOnce(() -> {
                Wrist.get().setState(WristState.Carry);
            })
        ).schedule();
    }

    public void scheduleDoubleSubstationCommand() {
        sequence(
            runOnce(() -> {
                Arm.get().command(ArmCommand.DoubleSubstation);
            }),
            waitSeconds(0.4),
            runOnce(() -> {
                Wrist.get().setState(WristState.Pickup);
                Gripper.get().scheduleIntakeCommand();
            })
        ).schedule();
    }

    public void schedulePickupCommand() {
        sequence(
            runOnce(() -> {
                Arm.get().command(ArmCommand.Floor);
            }),
            waitSeconds(0.4),
            runOnce(() -> {
                Wrist.get().setState(WristState.Pickup);
                Gripper.get().scheduleIntakeCommand();
            })
        ).schedule();
    }

    public void scheduleCarryCommand() {
        Arm.get().command(ArmCommand.Home);
        Wrist.get().setState(WristState.Carry);
        Gripper.get().scheduleStopCommand();
    }

    public void scheduleAutomationCommand() {
        switch (Arm.get().getGridPosition()) {
            case Low:
                Cat5Utils.time();
                System.out.println("Low automation not implemented");
                break;
            case Mid:
                switch (Gripper.get().getHeldGamePiece()) {
                    case Cone:
                        sequence(
                            new MidConeNode(),
                            waitSeconds(0.5), // 1
                            runOnce(() -> {
                                Arm.get().command(ArmCommand.None);
                                Arm.get().command(ArmCommand.ScoreMidCone);
                            }),
                            waitSeconds(0.5),
                            runOnce(() -> {
                                Gripper.get().scheduleOuttakeCommand();
                            })
                        ).schedule();

                        Cat5Utils.time();
                        System.out.println("Mid cone automation");
                        break;
                    case Cube:
                        sequence(
                            new MidCubeNode(),
                            waitSeconds(0.5),
                            runOnce(() -> {
                                Gripper.get().scheduleOuttakeCommand();
                            })
                        ).schedule();

                        Cat5Utils.time();
                        System.out.println("Mid cube automation");
                        break;
                    default:
                        break;
                }
                break;
            case High:
                switch (Gripper.get().getHeldGamePiece()) {
                    case Cone:
                        sequence(
                            new HighConeNode(),
                            waitSeconds(0.5), // 1
                            runOnce(() -> {
                                Gripper.get().scheduleOuttakeCommand();
                            })
                        ).schedule();

                        Cat5Utils.time();
                        System.out.println("High cone automation");
                        break;
                    case Cube:
                        sequence(
                            new HighCubeNode(),
                            waitSeconds(0.5), // 1
                            runOnce(() -> {
                                Gripper.get().scheduleOuttakeCommand();
                            })
                        ).schedule();

                        Cat5Utils.time();
                        System.out.println("High cube automation");
                        break;
                    default:
                        break;
                }
                break;
        }
    }
    //#endregion
}