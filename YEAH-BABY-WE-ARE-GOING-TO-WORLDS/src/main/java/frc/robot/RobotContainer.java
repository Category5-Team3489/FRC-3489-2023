// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.HashSet;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.data.Cat5ShuffleboardLayouts;
import frc.robot.subsystems.Camera;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;

public class RobotContainer {
    // State
    public final Robot robot;
    public final DataLog dataLog;
    public final Cat5ShuffleboardLayouts layouts;

    //#region Subsystem Init
    private HashSet<String> subsystems = new HashSet<String>();

    public void initSubsystem(String name) {
        boolean isUnique = subsystems.add(name);
        if (!isUnique) {
            Cat5.error(name + " subsystem initialized more than once!", false);
        }
        else {
            Cat5.print("Initializing " + name + " subsystem...");
        }
    }
    //#endregion

    // Subsystems
    @SuppressWarnings("unused")
    private final Camera camera;
    @SuppressWarnings("unused")
    private final NavX2 navx;
    @SuppressWarnings("unused")
    private final Limelight limelight;

    public RobotContainer(Robot robot, DataLog dataLog) {
        this.robot = robot;
        this.dataLog = dataLog;
        layouts = new Cat5ShuffleboardLayouts();

        Cat5.print("Initializing...");
        camera = new Camera(this);
        navx = new NavX2(this);
        limelight = new Limelight(this);
        Cat5.print("Initialization complete!");

        configureBindings();
    }

    private void configureBindings() {

    }

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }
}
