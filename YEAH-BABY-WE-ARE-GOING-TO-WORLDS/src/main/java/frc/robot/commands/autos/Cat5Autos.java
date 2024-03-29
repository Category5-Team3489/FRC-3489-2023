package frc.robot.commands.autos;

import java.util.HashMap;
import java.util.function.Supplier;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Cat5;
import frc.robot.data.shuffleboard.Cat5ShuffleboardTab;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class Cat5Autos {
    // State
    private final HashMap<String, Supplier<Command>> autos = new HashMap<String, Supplier<Command>>();
    private final SendableChooser<String> autoChooser = new SendableChooser<String>();
    private String currentAuto = "";

    public void addAuto(Supplier<Command> auto) {
        String name = auto.get().getName();

        Supplier<Command> wrappedAuto = () -> {
            return auto.get()
                .beforeStarting(() -> {
                    Cat5.print("Running auto: \"" + name + "\""); 
                })
                .finallyDo(interrupted -> {
                    if (interrupted) {
                        Cat5.print("Interrupted auto: \"" + name + "\""); 
                    }
                    else {
                        Cat5.print("Completed auto: \"" + name + "\""); 
                    }
                });
        };

        autos.put(name, wrappedAuto);

        if (autos.size() == 1) {
            autoChooser.setDefaultOption(name, name);
        }
        else {
            autoChooser.addOption(name, name);
        }
    }

    public void addSelectorWidget() {
        Cat5ShuffleboardTab.Auto.get().add("Auto Selector", autoChooser)
            .withSize(2, 1);
    }

    public Command getAutonomousCommand() {
        currentAuto = autoChooser.getSelected();

        if (currentAuto == null) {
            currentAuto = "";
        }

        Command autoCommand = autos.get(currentAuto).get();

        if (autoCommand != null) {
            return autoCommand;
        }

        return print("Auto command was null");
    }
}
