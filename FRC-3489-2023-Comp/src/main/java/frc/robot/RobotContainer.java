// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.DriveToRelativePose;
import frc.robot.commands.automation.MidConeNode;
import frc.robot.commands.automation.MidCubeNode;
import frc.robot.enums.GridPosition;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Cat5Subsystem;
import frc.robot.subsystems.ColorSensor;
import frc.robot.subsystems.DriverCamera;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.Limelight;
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
        NavX2.get();
        Limelight.get();
        Drivetrain.get();
        PoseEstimator.get();

        ColorSensor.get();
        Gripper.get();
        Arm.get();

        //DriverCamera.get();
        
        Leds.get();
        
        configureBindings();

        // ShuffleboardLayout layout = Cat5ShuffleboardTab.Auto.get().getLayout("Auto", BuiltInLayouts.kList);
        
        // layout.add("Auto 1", Commands.runOnce(() -> getAutonomousCommand()));
        // layout.add("Place Cone Auto", Commands.runOnce(() -> getPlaceConeAutoCommand()));
    }


    private void configureBindings() {}

    public Command getAutonomousCommand() {
        return Commands.sequence(
            new DriveToRelativePose(new Pose2d(0, 1, Rotation2d.fromDegrees(180)), 0.5)
        );
    }

    public Command getPlaceConeAutoCommand() {
        return Commands.sequence(
            // Commands.runOnce(() -> {
            //     Gripper.get().lowOuttakeConeCommand.schedule();
            // }),
            Commands.runOnce(() -> {
                Arm.get().setTargetAngleDegrees(ArmConstants.FloorAngleDegrees, IdleMode.kBrake);
            }),
            Commands.waitSeconds(1),
            Commands.runOnce(() -> {
                Gripper.get().intakeCommand.schedule();
            }),
            Commands.waitSeconds(1.0),
            Commands.runOnce(() -> {
                Arm.get().setTargetAngleDegrees(ArmConstants.AboveMidConeAngleDegrees, IdleMode.kBrake);
                Arm.get().setGridPosition(GridPosition.Mid);
            }),
            Commands.waitSeconds(3),
            new MidConeNode(),
            new WaitCommand(1),
            Commands.runOnce(() -> {
                Arm.get().setTargetAngleDegrees(ArmConstants.OnMidConeAngleDegrees, IdleMode.kBrake);
            }),
            Commands.waitSeconds(0.5),
            Commands.runOnce(() -> {
                Gripper.get().midOuttakeConeCommand.schedule();
            })
            // new DriveToRelativePose(new Pose2d(0, 1, Rotation2d.fromDegrees(0)), 1),
            // Commands.runOnce(() -> {
            //     Arm.get().setTargetAngleDegrees(ArmConstants.FloorAngleDegrees, IdleMode.kBrake);
            // }),
            // Commands.waitSeconds(2),
            // new DriveToRelativePose(new Pose2d(-3, 0, Rotation2d.fromDegrees(0)), 2)
        );
    }
}
