// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.NavX2;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class Drive extends CommandBase {
    private final Drivetrain drivetrain;
    private final NavX2 navx;

    private final DoubleSupplier translationXSupplier;
    private final DoubleSupplier translationYSupplier;
    private final DoubleSupplier rotationSupplier;

    public Drive(
        Drivetrain drivetrain,
        NavX2 navx,
        DoubleSupplier translationXSupplier,
        DoubleSupplier translationYSupplier,
        DoubleSupplier rotationSupplier) {
        this.drivetrain = drivetrain;
        this.navx = navx;
        this.translationXSupplier = translationXSupplier;
        this.translationYSupplier = translationYSupplier;
        this.rotationSupplier = rotationSupplier;

        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        drivetrain.drive(ChassisSpeeds.fromFieldRelativeSpeeds(
            translationXSupplier.getAsDouble(),
            translationYSupplier.getAsDouble(),
            rotationSupplier.getAsDouble(),
            navx.getRotation()
        ));
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.drive(new ChassisSpeeds(0, 0, 0));
    }
}
