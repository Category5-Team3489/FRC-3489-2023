// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.Constants.LedConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.Autos;
import frc.robot.commands.Drive;
import frc.robot.constants.DrivetrainConstants;
import frc.robot.diagnostics.DrivetrainDiagnostics;
import frc.robot.diagnostics.MotorCurrentDiagnostics;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;
import frc.robot.subsystems.PowerDistributionHub;
//import frc.robot.subsystems.PoseEstimator;
import frc.robot.subsystems.Drivetrain.DrivetrainMode;
import frc.robot.subsystems.Leds.LedState;
import edu.wpi.first.wpilibj.DriverStation;
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
    public final Drivetrain drivetrain = new Drivetrain();
    public final NavX2 navx = new NavX2();
    public final Limelight limelight = new Limelight();
    //public final PoseEstimator poseEstimator = new PoseEstimator(drivetrain, navx, limelight);
    // public final Gripper gripper = new Gripper();
    public final Arm arm = new Arm();
    // public final DriverCamera driverCamera = new DriverCamera();
    public final Leds leds = new Leds();
    public final PowerDistributionHub powerDistributionHub = new PowerDistributionHub();

    // Constants
    public final DrivetrainConstants drivetrainConstants = new DrivetrainConstants(drivetrain);
    // public final ArmConstants armConstants = new ArmConstants(arm);
    // Diagnostics
    public final DrivetrainDiagnostics drivetrainDiagnostics = new DrivetrainDiagnostics(drivetrain);
    public final MotorCurrentDiagnostics motorCurrentDiagnostics = new MotorCurrentDiagnostics(powerDistributionHub);

    // Controllers
    public final CommandXboxController xbox = new CommandXboxController(OperatorConstants.XboxPort);
    public final CommandJoystick man = new CommandJoystick(OperatorConstants.ManPort);

    /** The container for the robot. Contains subsystems, OI devices, and commands. */
    public RobotContainer() {
        drivetrain.setDefaultCommand(new Drive(
            drivetrain,
            navx,
            () -> -Cat5Math.modifyAxis(xbox.getLeftY()) * DrivetrainConstants.GetMaxVelocityMetersPerSecond.getAsDouble(),
            () -> -Cat5Math.modifyAxis(xbox.getLeftX()) * DrivetrainConstants.GetMaxVelocityMetersPerSecond.getAsDouble(),
            () -> -Cat5Math.modifyAxis(xbox.getRightX()) * DrivetrainConstants.GetMaxAngularVelocityRadiansPerSecond.getAsDouble(),
            () -> xbox.getLeftTriggerAxis(),
            () -> xbox.getRightTriggerAxis()
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
        xbox.back()
            .onTrue(Commands.runOnce(() -> navx.zeroYaw(), navx));

        // Drivetrain Bindings
        xbox.rightStick()
            .whileTrue(Commands.runOnce(() -> drivetrain.setMode(DrivetrainMode.Brake)))
            .onFalse(Commands.runOnce(() -> drivetrain.setMode(DrivetrainMode.ChassisSpeeds)));
        
        // xbox.button(DrivetrainConstants.SetCorFrontLeftButton)
        //     .onTrue(Commands.runOnce(() -> drivetrain.setCenterOfRotation(ModulePosition.FrontLeft)))
        //     .onFalse(Commands.runOnce(() -> drivetrain.setCenterOfRotation(ModulePosition.None)));
        // xbox.button(DrivetrainConstants.SetCorFrontRightButton)
        //     .onTrue(Commands.runOnce(() -> drivetrain.setCenterOfRotation(ModulePosition.FrontRight)))
        //     .onFalse(Commands.runOnce(() -> drivetrain.setCenterOfRotation(ModulePosition.None)));

        // Camera Bindings
        // xbox.button(DriverCameraConstants.IndexServoPositionXboxButton)
        //     .onTrue(Commands.runOnce(() -> driverCamera.indexServoPosition(), driverCamera));

        //Arm Testing
        xbox.button(1)
            .whileTrue(Commands.runOnce(() -> arm.manualArm(0.2), arm));
        xbox.button(2)
            .whileTrue(Commands.runOnce(() -> arm.manualArm(-0.2), arm));
        man.axisGreaterThan(1, 0)
            .whileTrue(Commands.runOnce(() -> arm.manualArm(man.getY()), arm));
        man.axisLessThan(1, 0)
            .whileTrue(Commands.runOnce(() -> arm.manualArm(man.getY()), arm));

        //LED Bindings
        xbox.button(1)
            .onTrue(Commands.runOnce(() -> leds.setLeds(LedState.NeedCone), leds));
        xbox.button(2)
            .onTrue(Commands.runOnce(() -> leds.setLeds(LedState.NeedCube), leds));
        xbox.button(3)
            .onTrue(Commands.runOnce(() -> leds.setLeds(LedState.Red), leds));
        xbox.button(4)
            .onTrue(Commands.runOnce(() -> leds.setLeds(LedState.DarkRed), leds));
        // xbox.button(3)
        //     .onTrue(Commands.runOnce(() -> leds.setLeds(LedState.Off), leds));
        

        // Drivetrain Bindings
        // xbox.button(DrivetrainConstants.ChargingStationButton)
        //     .whileTrue(Commands.runOnce(() -> drivetrain.setChargingAngle(), drivetrain));
    }

    /**
     * Use this to pass the autonomous command to the main {@link Robot} class.
     *
     * @return the command to run in autonomous
     */
    public Command getAutonomousCommand() {
        return Autos.testTestAuto(this);
    }
}