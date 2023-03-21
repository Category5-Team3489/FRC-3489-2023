package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.drivetrain.DrivePercentAngleSeconds;
import frc.robot.commands.drivetrain.DriveRelativeMeters;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.subsystems.Drivetrain;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Autos {
    // Constants
    private static final String TaxiAuto = "Taxi";
    private static final String TaxiFarAuto = "TaxiFar";
    private static final String BalanceAuto = "Balance";
    private static final String BumpBalanceAuto = "BumpBalance";
    private static final String NothingAuto = "Nothing";
    private static final String DriveRelMetersAuto = "DriveRelMeters";

    // Shuffleboard
    private final SendableChooser<String> autoChooser = new SendableChooser<String>();
    private String currentAuto = "";

    public Autos() {
        //#region Shuffleboard
        autoChooser.setDefaultOption(TaxiAuto, TaxiAuto);
        autoChooser.setDefaultOption(TaxiFarAuto, TaxiFarAuto);
        autoChooser.addOption(BalanceAuto, BalanceAuto);
        autoChooser.addOption(BumpBalanceAuto, BumpBalanceAuto);
        autoChooser.addOption(NothingAuto, NothingAuto);
        autoChooser.addOption(DriveRelMetersAuto, DriveRelMetersAuto);
        
        Cat5ShuffleboardTab.Auto.get().add(autoChooser);
        //#endregion
    }
    
    //#region Public
    public Command getAutonomousCommand() {
        currentAuto = autoChooser.getSelected();

        if (currentAuto == null) {
            currentAuto = "";
        }

        Cat5Utils.time();
        System.out.println("Running selected auto: \"" + currentAuto + "\"");

        switch (currentAuto) {
            case TaxiAuto:
                return getTaxiAutoCommand();
            case TaxiFarAuto:
                return getTaxiFarAutoCommand();
            case BalanceAuto:
                return getBalanceAutoCommand();
            case BumpBalanceAuto:
                return getBumpBalanceAutoCommand();
            case NothingAuto:
                return getNothingAutoCommand();
            case DriveRelMetersAuto:
                return getDriveRelMetersCommand();
        }

        return print("Unknown auto selected, doing nothing");
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
            runOnce(() -> {
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
            runOnce(() -> {
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

    private Command getDriveRelMetersCommand() {
        return sequence(
            new DriveRelativeMeters(1, 0, 0, 0.25, 0.05),
            completed()
        );
    }
    //#endregion

    //#region Utils
    private Command completed() {
        return runOnce(() -> {
            Cat5Utils.time();
            System.out.println("Completed auto: \"" + currentAuto + "\"");
        });
    }
    //#endregion
}