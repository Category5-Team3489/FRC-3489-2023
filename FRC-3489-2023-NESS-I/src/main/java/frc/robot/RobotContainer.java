// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import com.revrobotics.CANSparkMax.IdleMode;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.ArmConstants;
import frc.robot.Constants.InputConstants;
import frc.robot.Constants.LedsConstants;
import frc.robot.enums.GamePiece;
import frc.robot.enums.GridPosition;
import frc.robot.enums.LedPattern;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Camera;
import frc.robot.subsystems.Cat5Subsystem;
import frc.robot.subsystems.ColorSensor;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;
import frc.robot.subsystems.Wrist;

import static edu.wpi.first.wpilibj2.command.Commands.*;
import static frc.robot.Inputs.*;

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

        ColorSensor.get();
        Gripper.get();
        Wrist.get();
        Arm.get();

        Leds.get();

        configureBindings();
    }

    private void configureBindings() {
        Xbox.start()
            .onTrue(NavX2.get().zeroYawCommand);

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

        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(runOnce(() -> {
                GamePiece detectedGamePiece = ColorSensor.get().getDetectedGamePiece();
                Gripper.get().setHeldGamePiece(ColorSensor.get().getDetectedGamePiece());

                Cat5Utils.time();
                System.out.println("Detected game piece: \"" + detectedGamePiece.toString() + "\" on enable, set as held game piece in gripper");
            }));

        Man.button(InputConstants.GripperStopManButton)
            .onTrue(runOnce(() -> {
                Gripper.get().scheduleStopCommand();
            }));
        Man.button(InputConstants.GripperIntakeManButton)
            .onTrue(runOnce(() -> {
                Gripper.get().scheduleIntakeCommand();
            }));
        Man.button(InputConstants.GripperOuttakeManButton)
            .onTrue(runOnce(() -> {
                Gripper.get().scheduleOuttakeCommand();
            }));

        new Trigger(() -> DriverStation.isEnabled())
            .onTrue(runOnce(() -> {
                Arm.get().setTargetAngleDegrees(GridPosition.Low, ArmConstants.MinAngleDegrees, IdleMode.kCoast);
            }));

        Man.button(ArmConstants.ForceHomeManButton)
            .onTrue(runOnce(() -> {
                Arm.get().forceHome();
            }));

        Man.button(ArmConstants.HomeManButton)
            .onTrue(runOnce(() -> {
                Arm.get().setTargetAngleDegrees(GridPosition.Low, ArmConstants.MinAngleDegrees, IdleMode.kCoast);
            }));

        Man.button(ArmConstants.DoubleSubstationButton)
            .onTrue(runOnce(() -> {
                Arm.get().setTargetAngleDegrees(GridPosition.High, ArmConstants.DoubleSubstationDegrees, IdleMode.kBrake);
            }));


        Man.button(ArmConstants.FloorManButton)
            .onTrue(runOnce(() -> {
                Arm.get().setTargetAngleDegrees(GridPosition.Low, ArmConstants.FloorAngleDegrees, IdleMode.kBrake);
            }));

        Man.button(ArmConstants.LowManButton)
            .onTrue(runOnce(() -> {
                GamePiece heldGamePiece = Gripper.get().getHeldGamePiece();
                switch (heldGamePiece) {
                    case Cone:
                        Arm.get().setTargetAngleDegrees(GridPosition.Low, ArmConstants.LowConeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Cube:
                        Arm.get().setTargetAngleDegrees(GridPosition.Low, ArmConstants.LowCubeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Unknown:
                        Arm.get().setTargetAngleDegrees(GridPosition.Low, ArmConstants.LowUnknownAngleDegrees, IdleMode.kBrake);
                        break;
                }
            }));

        Man.button(ArmConstants.MidManButton)
            .debounce(0.1, DebounceType.kBoth)
            .onTrue(runOnce(() -> {
                GamePiece heldGamePiece = Gripper.get().getHeldGamePiece();
                switch (heldGamePiece) {
                    case Cone:
                        if (Arm.get().getGridPosition() != GridPosition.Mid) {
                            Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.AboveMidConeAngleDegrees, IdleMode.kBrake);
                        }
                        else {
                            if (Arm.get().getTargetAngleDegrees() != ArmConstants.OnMidConeAngleDegrees) {
                                Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.OnMidConeAngleDegrees, IdleMode.kBrake);
                            }
                            else {
                                Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.AboveMidConeAngleDegrees, IdleMode.kBrake);
                            }
                        }
                        break;
                    case Cube:
                        Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.MidCubeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Unknown:
                        Arm.get().setTargetAngleDegrees(GridPosition.Mid, ArmConstants.MidUnknownAngleDegrees, IdleMode.kBrake);
                        break;
                }
            }));

        Man.button(ArmConstants.HighManButton)
            .onTrue(runOnce(() -> {
                GamePiece heldGamePiece = Gripper.get().getHeldGamePiece();
                switch (heldGamePiece) {
                    case Cone:
                        Arm.get().setTargetAngleDegrees(GridPosition.High, ArmConstants.HighConeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Cube:
                        Arm.get().setTargetAngleDegrees(GridPosition.High, ArmConstants.HighCubeAngleDegrees, IdleMode.kBrake);
                        break;
                    case Unknown:
                        Arm.get().setTargetAngleDegrees(GridPosition.High, ArmConstants.HighUnknownAngleDegrees, IdleMode.kBrake);
                        break;
                }
            }));

        new Trigger(() -> DriverStation.isAutonomousEnabled())
            .onTrue(Leds.get().getCommand(LedPattern.Blue, 1.0, false));
        new Trigger(() -> DriverStation.isTeleopEnabled())
            .onTrue(Leds.get().getCommand(LedPattern.Green, 1.0, false));
        Man.axisLessThan(LedsConstants.GamePieceIndicatorManAxis, -LedsConstants.GamePieceIndicatorThreshold)
            .whileTrue(Leds.get().getCommand(LedPattern.BlueViolet, Double.MAX_VALUE, true));
        Man.axisGreaterThan(LedsConstants.GamePieceIndicatorManAxis, LedsConstants.GamePieceIndicatorThreshold)
            .whileTrue(Leds.get().getCommand(LedPattern.Yellow, Double.MAX_VALUE, true));
    }

    // Try out DataLogManager
    // https://docs.wpilib.org/en/stable/docs/software/telemetry/datalog.html
    // Combine with shuffleboard logging stuff?
}
