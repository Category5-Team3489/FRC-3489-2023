// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.GripperConstants;
import frc.robot.Constants.LedsConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.enums.LedPattern;
import frc.robot.subsystems.Camera;
import frc.robot.subsystems.Cat5Subsystem;
import frc.robot.subsystems.ColorSensor;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.NavX2;

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
    private final CommandXboxController xbox = new CommandXboxController(OperatorConstants.XboxPort);
    private final CommandJoystick man = new CommandJoystick(OperatorConstants.ManPort);
    
    public RobotContainer() {
        instance = this;
        cat5Subsystems = new ArrayList<Cat5Subsystem<?>>();

        // Initialize subsystems
        Camera.get();

        NavX2.get();
        // TODO Limelight
        // TODO Drivetrain
        // TODO PoseEstimator

        ColorSensor.get();
        Gripper.get();
        // TODO Arm

        Leds.get();

        configureBindings();
    }

    private void configureBindings() {
        xbox.start()
            .onTrue(NavX2.get().zeroYawCommand);

        new Trigger(() -> DriverStation.isAutonomousEnabled())
            .onTrue(Leds.get().getCommand(LedPattern.Blue, 1.0, false));
        new Trigger(() -> DriverStation.isTeleopEnabled())
            .onTrue(Leds.get().getCommand(LedPattern.Green, 1.0, false));
        man.axisLessThan(LedsConstants.GamePieceIndicatorManAxis, -LedsConstants.GamePieceIndicatorThreshold)
            .whileTrue(Leds.get().getCommand(LedPattern.BlueViolet, Double.MAX_VALUE, true));
        man.axisGreaterThan(LedsConstants.GamePieceIndicatorManAxis, LedsConstants.GamePieceIndicatorThreshold)
            .whileTrue(Leds.get().getCommand(LedPattern.Yellow, Double.MAX_VALUE, true));
        //Gripper
        man.button(GripperConstants.OuttakeManButton)
            .onTrue(Commands.runOnce(() -> Gripper.get().outtakeCommand()));
        man.button(GripperConstants.StopManButton)
            .onTrue(Commands.runOnce(() -> Gripper.get().stopCommand.schedule()));
        man.button(GripperConstants.IntakeManButton)
            .onTrue(Commands.runOnce(() -> Gripper.get().intakeCommand.schedule()));

    }

    // Try out DataLogManager
    // https://docs.wpilib.org/en/stable/docs/software/telemetry/datalog.html
    // Combine with shuffleboard logging stuff?
}
