// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.CameraConstants;
import frc.robot.Constants.DrivetrainConstants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.Constants.LinearSlideConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.Drive;
import frc.robot.general.Utils;
import frc.robot.subsystems.DriverCamera;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.ExampleSubsystem;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.LinearSlide;
import frc.robot.subsystems.NavX2;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
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
    private final ExampleSubsystem exampleSubsystem = new ExampleSubsystem();
    private final Drivetrain drivetrain = new Drivetrain();
    private final NavX2 navx = new NavX2();
    private final LinearSlide linearSlide = new LinearSlide();
    private final Leds leds = new Leds();
    private final Intake intake = new Intake(leds);
    private final DriverCamera driverCamera = new DriverCamera();

    // Driver Controller
    private final CommandXboxController xbox = new CommandXboxController(OperatorConstants.XboxPort);
    // Manipulator Controller
    private final CommandJoystick man = new CommandJoystick(OperatorConstants.ManPort);

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        drivetrain.setDefaultCommand(new Drive(
            drivetrain,
            navx,
            () -> -Utils.modifyAxis(xbox.getRawAxis(1)) * Drivetrain.MaxVelocityMetersPerSecond * DrivetrainConstants.TranslationModifier,
            () -> -Utils.modifyAxis(xbox.getRawAxis(0)) * Drivetrain.MaxVelocityMetersPerSecond * DrivetrainConstants.TranslationModifier,
            () -> -Utils.modifyAxis(xbox.getRawAxis(2)) * Drivetrain.MaxAngularVelocityRadiansPerSecond * DrivetrainConstants.RotationModifier
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
        // Drivetrain Bindings
        xbox.button(DrivetrainConstants.NavXZeroYawButton).onTrue(new InstantCommand(() -> navx.zeroYaw()));

        // TODO FIX
        // Linear Slide Bindings
        man.button(LinearSlideConstants.StopButton)
            .onTrue(Commands.runOnce(() -> linearSlide.stop()));

        man.button(LinearSlideConstants.GotoBottomButton)
            .onTrue(Commands.runOnce(() -> linearSlide.gotoPercentExtended(0)));

        xbox.button(9)
            .onTrue(Commands.runOnce(() -> linearSlide.gotoPercentExtended(0.5)));

        xbox.button(8)
            .onTrue(Commands.runOnce(() -> linearSlide.gotoPercentExtended(1)));

        // Intake Bindings
        man.button(IntakeConstants.IntakeButton)
            .whileTrue(Commands.run(() -> intake.intake(), intake))
            .onFalse(Commands.runOnce(() -> intake.stopIntake(), intake));

        man.button(IntakeConstants.PlacePieceButton)
            .whileTrue(Commands.run(() -> intake.placePiece(), intake))
            .onFalse(Commands.runOnce(() -> intake.stopIntake(), intake));

        man.button(IntakeConstants.SlowPlaceButton)
            .whileTrue(Commands.run(() -> intake.slowPlacePiece(), intake))
            .onFalse(Commands.runOnce(() -> intake.stopIntake(), intake));

        // Camera Bindings
        xbox.button(CameraConstants.CameraServoButton)
            .onTrue(Commands.runOnce(() -> driverCamera.indexServoPosition(), driverCamera));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        // An example command will be run in autonomous
        return Autos.exampleAuto(exampleSubsystem);
    }
}