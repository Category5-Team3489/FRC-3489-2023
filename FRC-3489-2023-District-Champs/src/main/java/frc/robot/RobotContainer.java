// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.subsystems.Cat5Subsystem;

public class RobotContainer {
    //#region Singleton
    private static RobotContainer instance = new RobotContainer();

    public static RobotContainer get() {
        return instance;
    }
    //#endregion
    
    //#region Cat 5 Subsystems
    private static List<Cat5Subsystem<?>> cat5Subsystems;

    public static void registerCat5Subsystem(Cat5Subsystem<?> cat5Subsystem) {
        for (Cat5Subsystem<?> subsystem : cat5Subsystems) {
            if (subsystem.getClass().getSimpleName() == cat5Subsystem.getClass().getSimpleName()) {
                Cat5Utils.time();
                DriverStation.reportError("Attempted to register subsystem \"" + cat5Subsystem.getClass().getSimpleName() + "\" twice", true);
            }
        }

        cat5Subsystems.add(cat5Subsystem);
        
        Cat5Utils.time();
        System.out.println("Registered subsystem \"" + cat5Subsystem.getClass().getSimpleName() + "\"");
    }
    //#endregion
    
    private RobotContainer() {
        instance = this;
        cat5Subsystems = new ArrayList<Cat5Subsystem<?>>();

        // Subsystem initialization

        configureBindings();
    }

    private void configureBindings() {}

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}
