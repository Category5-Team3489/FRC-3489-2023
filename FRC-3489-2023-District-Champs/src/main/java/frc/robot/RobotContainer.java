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
    
    //#region Subsystems
    private static List<Cat5Subsystem<?>> subsystems;

    public static void registerSubsystem(Cat5Subsystem<?> newSubsystem) {
        for (Cat5Subsystem<?> subsystem : subsystems) {
            if (subsystem.getClass().getSimpleName() == newSubsystem.getClass().getSimpleName()) {
                Cat5Utils.time();
                DriverStation.reportError("Attempted to register subsystem \"" + newSubsystem.getClass().getSimpleName() + "\" twice", true);
            }
        }

        subsystems.add(newSubsystem);
        Cat5Utils.time();
        System.out.println("Registered subsystem \"" + newSubsystem.getClass().getSimpleName() + "\"");
    }
    //#endregion

    private RobotContainer() {
        instance = this;
        subsystems = new ArrayList<Cat5Subsystem<?>>();
        
        configureBindings();
    }

    private void configureBindings() {}

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}
