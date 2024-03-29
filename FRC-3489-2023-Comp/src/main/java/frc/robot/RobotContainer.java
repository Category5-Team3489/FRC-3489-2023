// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;

import frc.robot.Constants.AutoConstants;
import frc.robot.Constants.CameraConstants;
import frc.robot.Constants.OperatorConstants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Cat5Subsystem;
import frc.robot.subsystems.ColorSensor;
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

    // Shuffleboard
    private final SendableChooser<String> autoChooser = new SendableChooser<String>();

    private RobotContainer() {
        instance = this;
        cat5Subsystems = new ArrayList<Cat5Subsystem<?>>();

        try {
            UsbCamera camera = CameraServer.startAutomaticCapture(0);
            VideoSink server = CameraServer.getServer();
            
            camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
            camera.setResolution(CameraConstants.PixelWidth, CameraConstants.PixelHeight);
            camera.setFPS(CameraConstants.FPS);

            Cat5ShuffleboardTab.Main.get().add(server.getSource());
        } catch (Exception e) {
            System.out.println("Error while initializing camera");
        }

        // Initialize subsystems
        NavX2.get();
        Limelight.get();
        Drivetrain.get();
        PoseEstimator.get();

        ColorSensor.get();
        Gripper.get();
        Arm.get();
        
        Leds.get();
        
        configureBindings();

        var autoTab = Cat5ShuffleboardTab.Auto.get();
        autoChooser.setDefaultOption(AutoConstants.TaxiAuto, AutoConstants.TaxiAuto);
        autoChooser.addOption(AutoConstants.BalanceAuto, AutoConstants.BalanceAuto);
        autoChooser.addOption(AutoConstants.BumpBalanceAuto, AutoConstants.BumpBalanceAuto);
        autoChooser.addOption(AutoConstants.NothingAuto, AutoConstants.NothingAuto);
        // autoChooser.addOption(AutoConstants.SidewaysThenTaxiAuto, AutoConstants.SidewaysThenTaxiAuto);
        // autoChooser.addOption(AutoConstants.ConeThenTaxiAuto, AutoConstants.ConeThenTaxiAuto);
        // autoChooser.addOption(AutoConstants.ConeThenBalanceAuto, AutoConstants.ConeThenBalanceAuto);
        autoTab.add(autoChooser);
    }

    private void configureBindings() {}

    public Command getAutonomousCommand() {
        switch (autoChooser.getSelected()) {
            case AutoConstants.TaxiAuto:
                return new FunctionalCommand(() -> {
                    // onInit
                    Drivetrain.get().driveCommand.setDisabled();
                }, () -> {
                    // onExecute
                    Drivetrain.get().setFrontLeftPercentAngle(0.12, 0);
                    Drivetrain.get().setFrontRightPercentAngle(0.12, 0);
                    Drivetrain.get().setBackLeftPercentAngle(0.12, 0);
                    Drivetrain.get().setBackRightPercentAngle(0.12, 0);
                }, (interrupted) -> {
                    Drivetrain.get().setFrontLeftPercentAngle(0, 0);
                    Drivetrain.get().setFrontRightPercentAngle(0, 0);
                    Drivetrain.get().setBackLeftPercentAngle(0, 0);
                    Drivetrain.get().setBackRightPercentAngle(0, 0);
                }, () -> {
                    return false;
                })
                    .withTimeout(5);
            case AutoConstants.BalanceAuto:
                return new FunctionalCommand(() -> {
                    // onInit
                    Drivetrain.get().driveCommand.setDisabled();
                }, () -> {
                    // onExecute
                    Drivetrain.get().setFrontLeftPercentAngle(0.12, 0);
                    Drivetrain.get().setFrontRightPercentAngle(0.12, 0);
                    Drivetrain.get().setBackLeftPercentAngle(0.12, 0);
                    Drivetrain.get().setBackRightPercentAngle(0.12, 0);
                }, (interrupted) -> {
                    Drivetrain.get().setFrontLeftPercentAngle(0, Math.toRadians(-45));
                    Drivetrain.get().setFrontRightPercentAngle(0, Math.toRadians(45));
                    Drivetrain.get().setBackLeftPercentAngle(0, Math.toRadians(45));
                    Drivetrain.get().setBackRightPercentAngle(0, Math.toRadians(-45));
                }, () -> {
                    return false;
                })
                    .withTimeout(6.75); // 5
            case AutoConstants.BumpBalanceAuto:
                return Commands.sequence(
                    new FunctionalCommand(() -> {
                        // onInit
                        Drivetrain.get().driveCommand.setDisabled();
                    }, () -> {
                        // onExecute
                        Drivetrain.get().setFrontLeftPercentAngle(-0.4, 0);
                        Drivetrain.get().setFrontRightPercentAngle(-0.4, 0);
                        Drivetrain.get().setBackLeftPercentAngle(-0.4, 0);
                        Drivetrain.get().setBackRightPercentAngle(-0.4, 0);
                    }, (interrupted) -> {
                        Drivetrain.get().setFrontLeftPercentAngle(0.4, 0);
                        Drivetrain.get().setFrontRightPercentAngle(0.4, 0);
                        Drivetrain.get().setBackLeftPercentAngle(0.4, 0);
                        Drivetrain.get().setBackRightPercentAngle(0.4, 0);
                    }, () -> {
                        return false;
                    })
                        .withTimeout(0.125),
                    
                    Commands.waitSeconds(0.125),

                    Commands.runOnce(() -> {
                        Drivetrain.get().setFrontLeftPercentAngle(0, 0);
                        Drivetrain.get().setFrontRightPercentAngle(0, 0);
                        Drivetrain.get().setBackLeftPercentAngle(0, 0);
                        Drivetrain.get().setBackRightPercentAngle(0, 0);
                    }),

                    Commands.waitSeconds(4),
                    
                    new FunctionalCommand(() -> {
                        // onInit
                        Drivetrain.get().driveCommand.setDisabled();
                    }, () -> {
                        // onExecute
                        Drivetrain.get().setFrontLeftPercentAngle(0.12, 0);
                        Drivetrain.get().setFrontRightPercentAngle(0.12, 0);
                        Drivetrain.get().setBackLeftPercentAngle(0.12, 0);
                        Drivetrain.get().setBackRightPercentAngle(0.12, 0);
                    }, (interrupted) -> {
                        Drivetrain.get().setFrontLeftPercentAngle(0, Math.toRadians(-45));
                        Drivetrain.get().setFrontRightPercentAngle(0, Math.toRadians(45));
                        Drivetrain.get().setBackLeftPercentAngle(0, Math.toRadians(45));
                        Drivetrain.get().setBackRightPercentAngle(0, Math.toRadians(-45));
                    }, () -> {
                        return false;
                    })
                        .withTimeout(6.75)
                );
            case AutoConstants.NothingAuto:
                return Commands.print("Nothing auto selected, doing nothing");
        }

        return Commands.print("Unknown auto selected, doing nothing");

        // return new AutoDrive(0, 3, 0, 0.6, 0.5, 90);

        // switch (autoChooser.getSelected()) {
        //     case AutoConstants.TaxiAuto:
        //         return Autos.getTaxiAuto();
        //     case AutoConstants.SidewaysThenTaxiAuto:
        //         return Autos.getSidewaysThenTaxiAuto();
        //     case AutoConstants.ConeThenTaxiAuto:
        //         return Autos.getConeThenTaxiAuto();
        //     case AutoConstants.ConeThenBalanceAuto:
        //         return Autos.getConeThenBalanceAuto();
        // }
        
        // return Commands.print("No autonomous command selected");
    }
}
