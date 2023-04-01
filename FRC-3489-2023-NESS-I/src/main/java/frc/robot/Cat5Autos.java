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
        addAuto("Taxi", () -> getTaxiAutoCommand());
        addAuto("TaxiFar", () -> getTaxiFarAutoCommand());
        addAuto("Balance", () -> getBalanceAutoCommand());
        addAuto("BumpBalance", () -> getBumpBalanceAutoCommand());
        addAuto("Nothing", () -> getNothingAutoCommand());
        addAuto("DriveRelMeters", () -> getDriveRelMetersAutoCommand());
        addAuto("TeamUmizoomi", () -> getTeamUmizoomiAutoCommand());

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

    private Command getTeamUmizoomiAutoCommand() {
        return sequence(
            print("Start and wait"),
            runOnce(() -> {
                NavX2.get().setOffset(Rotation2d.fromDegrees(180));
            }),
            waitSeconds(2),
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
            print("Start drive rel meters"),
            runOnce(() -> {
                new DriveRelativeMeters(0, 3, -180, 0.75, 0.05, 180).schedule();
            }),
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