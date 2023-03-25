package frc.robot;

import java.util.HashMap;
import java.util.function.Supplier;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.commands.CenterFiducialWithHeading;
import frc.robot.commands.drivetrain.DrivePercentAngleSeconds;
import frc.robot.commands.drivetrain.DriveRelativeMeters;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.subsystems.Drivetrain;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Autos {
    private final HashMap<String, Supplier<Command>> autos = new HashMap<String, Supplier<Command>>();
    private final SendableChooser<String> autoChooser = new SendableChooser<String>();
    private int i = 0;
    private String currentAuto = "";

    public Autos() {
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

        if (i == 0) {
            autoChooser.setDefaultOption(name, name);
        }
        else {
            autoChooser.addOption(name, name);
        }

        i++;
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
            new DriveRelativeMeters(1, 0, 0, 0.25, 0.05),
            completed()
        );
    }

    private Command getTeamUmizoomiAutoCommand() {
        return sequence(
            // Bump
            new DrivePercentAngleSeconds(-0.4, 0, 0.125),
            new DrivePercentAngleSeconds(0.4, 0, 0.125),
            // Drive away from wall to clear
            new DrivePercentAngleSeconds(0.12, 0, 1),
            // Flip towards april tag
            runEnd(() -> {
                // run
                Drivetrain.get().driveFieldRelative(0, 0, 1.0, Rotation2d.fromDegrees(180), 0, null);
            },
            () -> {
                // end
                Drivetrain.get().brakeTranslation();
            }, Drivetrain.get())
                .withTimeout(1.5),
            // center april tag and move away from wall
            new CenterFiducialWithHeading(0.6, 8),
            // Face opposing alliance
            runEnd(() -> {
                // run
                Drivetrain.get().driveFieldRelative(0, 0, 1.0, Rotation2d.fromDegrees(0), 0, null);
            },
            () -> {
                // end
                Drivetrain.get().brakeTranslation();
            }, Drivetrain.get())
                .withTimeout(1.5),
            // Begin pickup
            runOnce(() -> {
                RobotActions.get().schedulePickupCommand();
            }),
            // Drive and pickup
            runEnd(() -> {
                // run
                Drivetrain.get().driveFieldRelative(0.5, 0, 1.0, Rotation2d.fromDegrees(0), 0, null);
            },
            () -> {
                // end
                Drivetrain.get().brakeTranslation();
            }, Drivetrain.get())
                .withTimeout(1.5),
            // Carry
            runOnce(() -> {
                RobotActions.get().scheduleCarryCommand();
            }),
            // Face wall
            runEnd(() -> {
                // run
                Drivetrain.get().driveFieldRelative(0, 0, 1.0, Rotation2d.fromDegrees(180), 0, null);
            },
            () -> {
                // end
                Drivetrain.get().brakeTranslation();
            }, Drivetrain.get())
                .withTimeout(1.5),
            new CenterFiducialWithHeading(0.5, 1),

            completed()
        );
    }
    //#endregion
}