package frc.robot;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Autos {
    public static final String TaxiAuto = "Taxi";
    public static final String BalanceAuto = "Balance";
    public static final String BumpBalanceAuto = "BumpBalance";
    public static final String NothingAuto = "Nothing";

    private final SendableChooser<String> autoChooser = new SendableChooser<String>();

    public Autos() {
        var autoTab = Cat5ShuffleboardTab.Auto.get();
        autoChooser.setDefaultOption(TaxiAuto, TaxiAuto);
        autoChooser.addOption(BalanceAuto, BalanceAuto);
        autoChooser.addOption(BumpBalanceAuto, BumpBalanceAuto);
        autoChooser.addOption(NothingAuto, NothingAuto);
        autoTab.add(autoChooser);
    }
    
    public Command getAutonomousCommand() {
        switch (autoChooser.getSelected()) {
            case TaxiAuto:
                return getTaxiAutoCommand();
            case BalanceAuto:
                return getBalanceAutoCommand();
            case BumpBalanceAuto:
                return getBumpBalanceAutoCommand();
            case NothingAuto:
                return getNothingAutoCommand();
        }

        return print("No auto selected, doing nothing");
    }

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