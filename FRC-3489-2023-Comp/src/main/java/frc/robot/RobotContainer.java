// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.DefaultDrivetrain;
import frc.robot.commands.arm.GotoTarget;
import frc.robot.commands.arm.GotoHome;
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.PursuePose;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class RobotContainer {
    //#region Singleton
    private static RobotContainer instance;

    public static RobotContainer get() {
        if (instance == null) {
            instance = new RobotContainer();
        }

        return instance;
    }
    //#endregion

    //#region Cat 5 Subsystems
    private static List<Cat5Subsystem<?>> cat5Subsystems = new ArrayList<Cat5Subsystem<?>>();

    public static void registerCat5Subsystem(Cat5Subsystem<?> cat5Subsystem) {
        cat5Subsystems.add(cat5Subsystem);
    }
    //#endregion

    // Controllers
    public final CommandXboxController xbox = new CommandXboxController(OperatorConstants.XboxPort);
    public final CommandJoystick man = new CommandJoystick(OperatorConstants.ManPort);

    public RobotContainer() {
        var drivetrain = Drivetrain.get();
        var arm = Arm.get();

        drivetrain.setDefaultCommand(new DefaultDrivetrain());
        arm.setDefaultCommand(new GotoHome());
        new Trigger(() -> arm.isHomed())
            .whileTrue(new GotoTarget());

        configureBindings();
    }

    private void configureBindings() {}

    public Command getAutonomousCommand() {
        return Commands.print("No autonomous command configured");
    }

    public void initShuffleboard() {
        cat5Subsystems.forEach(Cat5Subsystem::initShuffleboard);
    }
}
