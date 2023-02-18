// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.RobotContainer;
import frc.robot.subsystems.Drivetrain.DrivetrainMode;

public final class Autos {
    public static CommandBase testAuto(RobotContainer c) {
        return Commands.sequence(
            Commands.print("[Auto] Begin"),
            Commands.runOnce(() -> c.drivetrain.setMode(DrivetrainMode.External)),
            Commands.print("[Auto] Step 1"),
            Commands.run(() -> c.drivetrain.setPercentAngle(0.25, Math.toRadians(0)))
                .withTimeout(4),
            Commands.print("[Auto] Step 2"),
            Commands.run(() -> c.drivetrain.setPercentAngle(0.25, Math.toRadians(90)))
                .withTimeout(4),
            Commands.print("[Auto] Step 3"),
            Commands.run(() -> c.drivetrain.setPercentAngle(0.25, Math.toRadians(180)))
                .withTimeout(4),
            Commands.print("[Auto] Step 4"),
            Commands.run(() -> c.drivetrain.setPercentAngle(0.25, Math.toRadians(270)))
                .withTimeout(4),
            Commands.print("[Auto] End"),
            Commands.runOnce(() -> c.drivetrain.setPercentAngle(0, Math.toRadians(0))),
            Commands.runOnce(() -> c.drivetrain.setMode(DrivetrainMode.ChassisSpeeds))
        );
    }

    public static CommandBase testTestAuto(RobotContainer c) {
        return Commands.sequence(
            Commands.print("[Auto] Begin"),
            Commands.runOnce(() -> c.drivetrain.setMode(DrivetrainMode.External)),
            Commands.print("[Auto] Step 1"),
            Commands.run(() -> c.drivetrain.setPercentAngle(0.25, Math.toRadians(0)))
                .withTimeout(4),
            Commands.print("[Auto] Step 2"),
            Commands.run(() -> c.drivetrain.setPercentAngle(-0.25, Math.toRadians(0)))
                .withTimeout(4),
            Commands.print("[Auto] End"),
            Commands.runOnce(() -> c.drivetrain.setPercentAngle(0, Math.toRadians(0))),
            Commands.runOnce(() -> c.drivetrain.setMode(DrivetrainMode.ChassisSpeeds))
        );
    }
}
