// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.CameraConstants;
import frc.robot.Constants.NavX2Constants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Drive;
import frc.robot.subsystems.V4Bar;
import frc.robot.subsystems.DriverCamera;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.NavX2;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
    // The robot's subsystems and commands are defined here...
    private final Drivetrain drivetrain = new Drivetrain();
    private final Gripper gripper = new Gripper();
    private final V4Bar v4bar = new V4Bar();
    private final NavX2 navx = new NavX2();
    private final DriverCamera driverCamera = new DriverCamera();
    private final Leds leds = new Leds();

    // Driver Controller
    private final CommandXboxController xbox = new CommandXboxController(OperatorConstants.XboxPort);
    // Manipulator Controller
    private final CommandJoystick man = new CommandJoystick(OperatorConstants.ManPort);

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        drivetrain.setDefaultCommand(new Drive(
            drivetrain,
            navx,
            () -> -Cat5Math.modifyAxis(xbox.getRawAxis(1)) * Drivetrain.MaxVelocityMetersPerSecond,
            () -> -Cat5Math.modifyAxis(xbox.getRawAxis(0)) * Drivetrain.MaxVelocityMetersPerSecond,
            () -> -Cat5Math.modifyAxis(xbox.getRawAxis(2)) * Drivetrain.MaxAngularVelocityRadiansPerSecond
        ));

        // Configure the bindings
        configureBindings();
    }

    /**
     * Use this method to define your trigger->command mappings. Triggers can be created via the
     * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
     * predicate, or via the named factories in {@link
     * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
     * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
     * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
     * joysticks}.
     */
    private void configureBindings() {
        // NavX2 Bindings
        xbox.button(NavX2Constants.ZeroYawXboxButton)
            .onTrue(Commands.runOnce(() -> navx.zeroYaw(), navx));

        // Camera Bindings
        xbox.button(CameraConstants.IndexServoPositionXboxButton)
            .onTrue(Commands.runOnce(() -> driverCamera.indexServoPosition(), driverCamera));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An example command will be run in autonomous
        return null; // Autos.exampleAuto(exampleSubsystem);
    }
}