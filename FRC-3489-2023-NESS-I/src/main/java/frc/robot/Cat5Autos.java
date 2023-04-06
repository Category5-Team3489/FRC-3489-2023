package frc.robot;

import java.util.HashMap;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.commands.drivetrain.DrivePercentAngleSeconds;
import frc.robot.commands.drivetrain.DriveRelativeMeters;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.NavX2;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Cat5Autos {
    // State
    private final HashMap<String, Supplier<Command>> autos = new HashMap<String, Supplier<Command>>();
    private final SendableChooser<String> autoChooser = new SendableChooser<String>();
    private String currentAuto = "";

    public Cat5Autos() {
        // Place mid, taxi
        // Place mid, balance
        // Place mid

        addAuto("Taxi", () -> getTaxiAutoCommand());
        addAuto("TaxiFar", () -> getTaxiFarAutoCommand());
        addAuto("Balance", () -> getBalanceAutoCommand());
        addAuto("BumpBalance", () -> getBumpBalanceAutoCommand());
        addAuto("Nothing", () -> getNothingAutoCommand());
        addAuto("DriveRelMeters", () -> getDriveRelMetersAutoCommand());
        addAuto("TeamUmizoomi", () -> getTeamUmizoomiAutoCommand());
        addAuto("PlaceMidConeThenPickup", () -> getPlaceMidConeThenPickupAutoCommand());

        Cat5ShuffleboardTab.Auto.get().add(autoChooser);
    }

    private void addAuto(String name, Supplier<Command> auto) {
        autos.put(name, auto);

        if (autos.size() == 1) {
            autoChooser.setDefaultOption(name, name);
        }
        else {
            autoChooser.addOption(name, name);
        }
    }

    private Command completed() {
        return runOnce(() -> {
            Cat5Utils.time();
            System.out.println("Completed auto: \"" + currentAuto + "\"");
        });
    }
    
    //#region Public
    public Command getAutonomousCommand() {
        currentAuto = autoChooser.getSelected();

        if (currentAuto == null) {
            currentAuto = "";
        }

        Cat5Utils.time();
        System.out.println("Running selected auto: \"" + currentAuto + "\"");

        Command autoCommand = autos.get(currentAuto).get();

        if (autoCommand != null) {
            return autoCommand;
        }

        return Commands.print("Auto command was null");
    }
    //#endregion

    //#region Autos
    private Command getTaxiAutoCommand() {
        return sequence(
            new DrivePercentAngleSeconds(0.12, 0, 5),
            completed()
        );
    }

    private Command getTaxiFarAutoCommand() {
        return sequence(
            new DrivePercentAngleSeconds(0.12, 0, 7.5),
            completed()
        );
    }

    private Command getBalanceAutoCommand() {
        return sequence(
            new DrivePercentAngleSeconds(0.12, 0, 6.75),
            run(() -> {
                Drivetrain.get().brakeTranslation();
            }),
            completed()
        );
    }

    private Command getBumpBalanceAutoCommand() {
        return sequence(
            new DrivePercentAngleSeconds(-0.4, 0, 0.125),
            new DrivePercentAngleSeconds(0.4, 0, 0.125),
            new DrivePercentAngleSeconds(0, 0, 4),
            new DrivePercentAngleSeconds(0.12, 0, 6.75),
            run(() -> {
                Drivetrain.get().brakeTranslation();
            }),
            completed()
        );
    }

    private Command getNothingAutoCommand() {
        return sequence(
            completed()
        );
    }

    private Command getDriveRelMetersAutoCommand() {
        return sequence(
            // new DriveRelativeMeters(0, 1, 0, 0.25, 0.05),
            // new DriveRelativeMeters(0, 0, 0, 0.25, 0.05),
            // new DriveRelativeMeters(0, 1, 0, 0.25, 0.05),
            // new DriveRelativeMeters(0, 1, 0, 0.25, 0.05),
            completed()
        );
    }

    private Command getPlaceMidConeAutoCommand() {
        return sequence(
            print("Start and wait"),
            runOnce(() -> {
                NavX2.get().setOffset(Rotation2d.fromDegrees(180));
            }),
            print("Move arm to mid position"),
            runOnce(() -> {
                Cat5Actions.get().scheduleMidCommand(false);
            }),
            print("Wait for arm to raise some"),
            waitSeconds(1),
            print("Start auto placement"),
            runOnce(() -> {
                Cat5Actions.get().scheduleAutomationCommand();
            }),
            print("Wait until drive command is active again"),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            waitSeconds(0.3), // 1
            print("Start drive rel meters"),
            runOnce(() -> {
                new DriveRelativeMeters(0, 0.5, -180, 4.0, 0.05, NavX2.get().getRotation().getDegrees()).schedule();
            }),
            runOnce(() -> {
                Cat5Actions.get().scheduleCarryCommand();
            }),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            })
        );
    }

    private Command getPlaceMidConeThenPickupAutoCommand() {
        return sequence(
            print("Start and wait"),
            runOnce(() -> {
                NavX2.get().setOffset(Rotation2d.fromDegrees(180));
            }),
            print("Move arm to mid position"),
            runOnce(() -> {
                Cat5Actions.get().scheduleMidCommand(false);
            }),
            print("Wait for arm to raise some"),
            waitSeconds(1),
            print("Start auto placement"),
            runOnce(() -> {
                Cat5Actions.get().scheduleAutomationCommand();
            }),
            print("Wait until drive command is active again"),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            waitSeconds(0.3), // 1
            print("Start drive rel meters"),
            runOnce(() -> {
                new DriveRelativeMeters(0, 4.5, -180, 4.0, 0.05, NavX2.get().getRotation().getDegrees()).schedule();
            }),
            runOnce(() -> {
                Cat5Actions.get().scheduleCarryCommand();
            }),
            // waitUntil(() -> {
            //     return Drivetrain.get().isDriveCommandActive();
            // }),
            // runOnce(() -> {
            //     Commands.run(() -> {
            //         Drivetrain.get().driveFieldRelative(0, 0, 1.0, Rotation2d.fromDegrees(0), 0, NavX2.get().getRotation().getDegrees());
            //     }, Drivetrain.get())
            //     .withTimeout(1)
            //     .schedule();
            // }),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            runOnce(() -> {
                new DriveRelativeMeters(0.37, 0, 0, 0.3, 0.05, NavX2.get().getRotation().getDegrees()).schedule();
            }),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            runOnce(() -> {
                Cat5Actions.get().schedulePickupCommand();
            }),
            waitSeconds(1.0),
            runOnce(() -> {
                new DriveRelativeMeters(0, 0.75, 0, 0.4, 0.05, NavX2.get().getRotation().getDegrees()).schedule();
            }),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            runOnce(() -> {
                Cat5Actions.get().scheduleCarryCommand();
            }),
            runOnce(() -> {
                new DriveRelativeMeters(0, -2.0, -180, 4.0, 0.05, NavX2.get().getRotation().getDegrees()).schedule();
            }),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            completed()
        );
    }

    private Command getTeamUmizoomiAutoCommand() {
        return sequence(
            print("Start and wait"),
            runOnce(() -> {
                NavX2.get().setOffset(Rotation2d.fromDegrees(180));
            }),
            print("Move arm to mid position"),
            runOnce(() -> {
                Cat5Actions.get().scheduleHighCommand();
            }),
            print("Wait for arm to raise some"),
            waitSeconds(1.5), // 1
            print("Start auto placement"),
            runOnce(() -> {
                Cat5Actions.get().scheduleAutomationCommand();
            }),
            print("Wait until drive command is active again"),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            waitSeconds(0.3), // 1
            print("Start drive rel meters"),
            runOnce(() -> {
                // new DriveRelativeMeters(0, 5.3, 0, 0.75, 0.05, -180).schedule();
                new DriveRelativeMeters(0, 5.08, -180, 4.0, 0.05, -180).schedule();
            }),
            print("REEEEEEEEEEEEEEEEEEEEEEEEEEE"),
            runOnce(() -> {
                Cat5Actions.get().scheduleCarryCommand();
            }),
            print("OOOOOOOOOOOOOOOOOOOOOOO"),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            print("AAAAAAAAAAAAAAAAAA"),
            runOnce(() -> {
                Commands.run(() -> {
                    // -10.5
                    Drivetrain.get().driveFieldRelative(0, 0, 1.0, Rotation2d.fromDegrees(-9), 0, Double.valueOf(180));
                }, Drivetrain.get())
                .withTimeout(1)
                .schedule();
            }),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            runOnce(() -> {
                Cat5Actions.get().schedulePickupCommand();
                // Cat5Actions.get().schedulePickupCommand();
            }),
            waitSeconds(2),
            runOnce(() -> {
                Commands.run(() -> {
                    Drivetrain.get().drivePercentAngle(0.15, 0); // 0.1
                }, Drivetrain.get())
                .withTimeout(1) // 1.5
                .schedule();
            }),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            runOnce(() -> {
                Cat5Actions.get().scheduleCarryCommand();
            }),
            waitSeconds(0.75), // 1
            runOnce(() -> {
                // 0.5, -4.75
                // 0.5, -4.6
                new DriveRelativeMeters(0.75, -4.6, -180, 4.0, 0.05, -9).schedule();
            }),
            waitUntil(() -> {
                return Drivetrain.get().isDriveCommandActive();
            }),
            runOnce(() -> {
                Cat5Actions.get().scheduleMidCommand(false);
            }),
            waitSeconds(1),
            runOnce(() -> {
                Cat5Actions.get().scheduleAutomationCommand();
            }),
            // waitUntil(() -> {
            //     return Drivetrain.get().isDriveCommandActive();
            // }),
            // runOnce(() -> {
            //     // new DriveRelativeMeters(0, 5.3, 0, 0.75, 0.05, -180).schedule();
            //     new DriveRelativeMeters(0, -3, -180, 4.0, 0.05, -180).schedule();
            // }),
            // new DrivePercentAngleSeconds(-0.12, 0, 5),
            // new DriveRelativeMeters(0, 5, -180, 1.0, 0.05),
            // new DriveRelativeMeters(0, 1, 0, 1.0, 0.05),
            completed()
        );
    }

    // run(() -> {
    //     Drivetrain.get().drivePercentAngle(0.2, 90);
    // }, Drivetrain.get()),
    //#endregion
}