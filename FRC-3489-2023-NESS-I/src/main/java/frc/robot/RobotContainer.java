// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.WristConstants.WristState;
import frc.robot.enums.ArmCommand;
import frc.robot.enums.GamePiece;
import frc.robot.enums.LedPattern;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Camera;
import frc.robot.subsystems.Cat5Subsystem;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;
import frc.robot.subsystems.Wrist;

import static edu.wpi.first.wpilibj2.command.Commands.*;
import static frc.robot.Cat5Inputs.*;

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

        // Initialize subsystems
        Camera.get();

        NavX2.get();
        Limelight.get();
        Drivetrain.get();

        Gripper.get();
        Wrist.get();
        Arm.get();

        Leds.get();

        Cat5Actions.get();

        configureBindings();
    }

    private void configureBindings() {
        //#region Automation
        Man.button(AutomateManButton)
            .debounce(ManButtonDebounceSeconds, DebounceType.kBoth)
            .onTrue(runOnce(() -> {
                Cat5Actions.get().scheduleAutomationCommand();
            }));
        //#endregion

        //#region ColorSensor and Gripper
        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(runOnce(() -> {
                GamePiece detected = Gripper.get().getDetectedGamePiece();
                Gripper.get().setHeldGamePiece(detected);

                Cat5Utils.time();
                System.out.println("Detected game piece: \"" + detected.toString() + "\" on enable, set as held game piece in gripper");
            }));
        //#endregion

        //#region Gripper and Arm
        //#endregion

        //#region NavX2
        Xbox.start()
            .onTrue(runOnce(() -> {
                NavX2.get().scheduleZeroYawCommand();
            }));
        //#endregion

        //#region Drivetrain
        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(runOnce(() -> {
                Drivetrain.get().resetTargetHeading();

                Cat5Utils.time();
                System.out.println("Reset target heading on enable");
            }));

        Xbox.leftStick()
            .whileTrue(Drivetrain.get().brakeTranslationCommand);
        Xbox.rightStick()
            .whileTrue(Drivetrain.get().brakeRotationCommand);
        
        Xbox.y()
            .onTrue(runOnce(() -> {
                Drivetrain.get().setTargetHeading(Rotation2d.fromDegrees(0));
            }));
        Xbox.b()
            .onTrue(runOnce(() -> {
                Drivetrain.get().setTargetHeading(Rotation2d.fromDegrees(-90));
            }));
        Xbox.a()
            .onTrue(runOnce(() -> {
                Drivetrain.get().setTargetHeading(Rotation2d.fromDegrees(-180));
            }));
        Xbox.x()
            .onTrue(runOnce(() -> {
                Drivetrain.get().setTargetHeading(Rotation2d.fromDegrees(-270));
            }));
        //#endregion

        //#region Gripper
        Man.button(GripperStopManButton)
            .onTrue(runOnce(() -> {
                Gripper.get().scheduleStopCommand();
            }));
        Man.button(GripperIntakeManButton)
            .onTrue(runOnce(() -> {
                Gripper.get().scheduleIntakeCommand();
            }));
        Man.button(GripperOuttakeManButton)
            .onTrue(runOnce(() -> {
                Gripper.get().scheduleOuttakeCommand();
            }));
        //#endregion

        //#region Wrist
        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(runOnce(() -> {
                Wrist.get().setState(WristState.Carry);
            }));
        //#endregion

        //#region Arm
        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(runOnce(() -> {
                Arm.get().command(ArmCommand.ForceHome);
            }));

        Man.button(HomeManButton)
            .onTrue(runOnce(() -> {
                Cat5Actions.get().scheduleCarryCommand();
            }));

        Man.button(FloorManButton)
            .onTrue(runOnce(() -> {
                Cat5Actions.get().schedulePickupCommand();
            }));

        Man.button(LowManButton)
            .onTrue(runOnce(() -> {
                Cat5Actions.get().scheduleLowCommand();
            }));

        Man.button(MidManButton)
            .debounce(0.1, DebounceType.kBoth)
            .onTrue(runOnce(() -> {
                Cat5Actions.get().scheduleMidCommand(true);
            }));

        Man.button(HighManButton)
            .onTrue(runOnce(() -> {
                Cat5Actions.get().scheduleHighCommand();
            }));

        Man.button(DoubleSubstationManButton)
            .onTrue(runOnce(() -> {
                Cat5Actions.get().scheduleDoubleSubstationCommand();
            }));

        Man.button(WristPickupManButton)
            .onTrue(runOnce(() -> {
                Wrist.get().setState(WristState.Pickup);
            }));

        Man.button(WristCarryManButton)
            .onTrue(runOnce(() -> {
                Wrist.get().setState(WristState.Carry);
            }));
        //#endregion

        //#region Leds
        new Trigger(() -> DriverStation.isAutonomousEnabled())
            .onTrue(Leds.get().getCommand(LedPattern.Blue, 1.0, false));
        new Trigger(() -> DriverStation.isTeleopEnabled())
            .onTrue(Leds.get().getCommand(LedPattern.Green, 1.0, false));
        //#endregion
    }
}
