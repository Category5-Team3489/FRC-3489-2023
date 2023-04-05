package frc.robot;

import java.util.HashMap;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Cat5Autos {
    // State
    private final HashMap<String, Supplier<Command>> autos = new HashMap<String, Supplier<Command>>();
    private final SendableChooser<String> autoChooser = new SendableChooser<String>();
    private String currentAuto = "";

    public Cat5Autos() {
        // Auto declarations

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

    //#region Public
    public Command getAutonomousCommand() {
        currentAuto = autoChooser.getSelected();

        if (currentAuto == null) {
            currentAuto = "null";
        }

        Cat5Utils.time();
        System.out.println("Selected auto running: \"" + currentAuto + "\"");

        Command autoCommand = autos.get(currentAuto).get();

        if (autoCommand != null) {
            return autoCommand
                .handleInterrupt(() -> {
                    Cat5Utils.time();
                    System.out.println("Auto interrupted: \"" + currentAuto + "\"");
                })
                .andThen(() -> {
                    Cat5Utils.time();
                    System.out.println("Auto completed: \"" + currentAuto + "\"");
                });
        }

        return print("Selected auto was null");
    }
    //#endregion
}
