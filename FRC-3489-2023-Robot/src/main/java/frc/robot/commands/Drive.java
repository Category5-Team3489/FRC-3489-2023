// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.Cat5Math;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.NavX2;
import frc.robot.subsystems.Drivetrain.CenterOfRotation;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class Drive extends CommandBase {
    private final Drivetrain drivetrain;
    private final NavX2 navx;

    private final DoubleSupplier translationXSupplier;
    private final DoubleSupplier translationYSupplier;
    private final DoubleSupplier rotationSupplier;
    private final DoubleSupplier frontLeftCorSupplier;
    private final DoubleSupplier frontRightCorSupplier;
 
    public Drive(
        Drivetrain drivetrain,
        NavX2 navx,
        DoubleSupplier translationXSupplier,
        DoubleSupplier translationYSupplier,
        DoubleSupplier rotationSupplier,
        DoubleSupplier frontLeftCorSupplier,
        DoubleSupplier frontRightCorSupplier) {
        this.drivetrain = drivetrain;
        this.navx = navx;
        this.translationXSupplier = translationXSupplier;
        this.translationYSupplier = translationYSupplier;
        this.rotationSupplier = rotationSupplier;
        this.frontLeftCorSupplier = frontLeftCorSupplier;
        this.frontRightCorSupplier = frontRightCorSupplier;

        addRequirements(drivetrain);
    }

    @Override
    public void execute() {
        drivetrain.supplyChassisSpeeds(ChassisSpeeds.fromFieldRelativeSpeeds(
            translationXSupplier.getAsDouble(),
            translationYSupplier.getAsDouble(),
            rotationSupplier.getAsDouble(),
            navx.getRotation()
        ));

        double frontLeftCor = frontLeftCorSupplier.getAsDouble();
        double frontRightCor = frontRightCorSupplier.getAsDouble();

        if (frontLeftCor >= 0.05) {
            drivetrain.setCenterOfRotation(CenterOfRotation.FrontLeft, 5 * Cat5Math.inverseLerp(frontLeftCor, 0.05, 1));
        }
        else if (frontRightCor >= 0.05) {
            drivetrain.setCenterOfRotation(CenterOfRotation.FrontRight, 5 * Cat5Math.inverseLerp(frontRightCor, 0.05, 1));
        }
        else {
            drivetrain.setCenterOfRotation(CenterOfRotation.Center, 0);
        }
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.supplyChassisSpeeds(new ChassisSpeeds(0, 0, 0));
        drivetrain.setCenterOfRotation(CenterOfRotation.Center, 0);
    }
}
