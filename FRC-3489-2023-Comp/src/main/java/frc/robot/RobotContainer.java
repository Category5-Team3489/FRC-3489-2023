// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.OperatorConstants;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Cat5Subsystem;
import frc.robot.subsystems.ColorSensor;
import frc.robot.subsystems.DriverCamera;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.NavX2;
import frc.robot.subsystems.PoseEstimator;

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
                throw new RuntimeException("Attempted to register subsystem \"" + cat5Subsystem.getClass().getSimpleName() + "\" twice");
            }
        }
        cat5Subsystems.add(cat5Subsystem);
        System.out.println("Registered subsystem \"" + cat5Subsystem.getClass().getSimpleName() + "\"");
    }
    //#endregion

    // Controllers
    public final CommandXboxController xbox = new CommandXboxController(OperatorConstants.XboxPort);
    public final CommandJoystick man = new CommandJoystick(OperatorConstants.ManPort);

    private RobotContainer() {
        instance = this;
        cat5Subsystems = new ArrayList<Cat5Subsystem<?>>();

        // Look here for fixes to common problems:
        // If shuffleboard layout doesn't show up, check size
        // If subsystem isn't working, call Subsystem.get() here
        // Check type if layout.add, or tab.add fails
        // If command doesnt run when disabled, set ignore disabled true
        // When building commands, always put .withName last

        // Initialize subsystems
        Drivetrain.get();
        NavX2.get();
        Arm.get();
        ColorSensor.get();
        Gripper.get();
        PoseEstimator.get();
        DriverCamera.get();
        
        configureBindings();
    }

    private void configureBindings() {}

    public Command getAutonomousCommand() {
        // return Commands.sequence(
        //     Commands.runOnce(() -> {
        //         Drivetrain.get().driveCommand.setTargetAngle(Rotation2d.fromDegrees(0));
        //     }),
        //     new PursuePose(new Pose2d(2.0, 2.0, Rotation2d.fromDegrees(0)))
        // );
        
        return Commands.print("No autonomous command set");
    }
}
