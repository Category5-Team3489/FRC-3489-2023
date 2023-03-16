package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Autos {
    // Constants
    private static final String TaxiAuto = "Taxi";
    private static final String BalanceAuto = "Balance";
    private static final String BumpBalanceAuto = "BumpBalance";
    private static final String NothingAuto = "Nothing";

    // State
    private final SendableChooser<String> autoChooser = new SendableChooser<String>();

    public Autos() {
        autoChooser.setDefaultOption(TaxiAuto, TaxiAuto);
        autoChooser.addOption(BalanceAuto, BalanceAuto);
        autoChooser.addOption(BumpBalanceAuto, BumpBalanceAuto);
        autoChooser.addOption(NothingAuto, NothingAuto);

        Cat5ShuffleboardTab.Auto.get().add(autoChooser);
    }
    
    //#region Public
    public Command getAutonomousCommand() {
        String selectedAuto = autoChooser.getSelected();

        System.out.println("Constructed selected auto: \"" + selectedAuto + "\"");

        switch (selectedAuto) {
            case TaxiAuto:
                return getTaxiAutoCommand();
            case BalanceAuto:
                return getBalanceAutoCommand();
            case BumpBalanceAuto:
                return getBumpBalanceAutoCommand();
            case NothingAuto:
                return getNothingAutoCommand();
        }

        return print("Unknown auto selected, doing nothing");
    }
    //#endregion

    private Command getTaxiAutoCommand() {
        return print("Taxi auto selected, not implemented, doing nothing");
    }

    private Command getBalanceAutoCommand() {
        return print("Balance auto selected, not implemented, doing nothing");
    }

    private Command getBumpBalanceAutoCommand() {
        return print("Bump balance auto selected, not implemented, doing nothing");
    }

    private Command getNothingAutoCommand() {
        return print("Nothing auto selected, doing nothing");
    }
}