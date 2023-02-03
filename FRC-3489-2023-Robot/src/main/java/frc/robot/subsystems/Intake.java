// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import frc.robot.Constants;
import frc.robot.Constants.IntakeConstants;
import frc.robot.general.LedColor;

public class Intake extends SubsystemBase {
    private final ShuffleboardTab tab = Constants.getMainTab();
    private final GenericEntry intakeEntry = tab.add("Intake", 0.0).getEntry();

    private final Leds leds;

    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(IntakeConstants.RightIntakeMotor);
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(IntakeConstants.LeftIntakeMotor);

    public Intake(Leds leds) {
        this.leds = leds;

        register();

        ShuffleboardLayout diagnosticLayout = Constants.createDiagnosticLayout("Intake")
            .withSize(2, 1);
        diagnosticLayout.add("Intake Diagnostic", getIntakeDiagnosticCommand());
    }

    public void intake() {
        rightMotor.set(IntakeConstants.IntakeSpeed);
        leftMotor.set(-IntakeConstants.IntakeSpeed);
        leds.setSolidColor(LedColor.Green);
        intakeEntry.setDouble(rightMotor.get());
    }

    public void placePiece() {
        rightMotor.set(-IntakeConstants.IntakeSpeed);
        leftMotor.set(IntakeConstants.IntakeSpeed);
        leds.setSolidColor(LedColor.Yellow);
        intakeEntry.setDouble(rightMotor.get());
    }

    public void slowPlacePiece() {
        rightMotor.set(-IntakeConstants.SlowPlaceSpeed);
        leftMotor.set(IntakeConstants.SlowPlaceSpeed);
        leds.setSolidColor(LedColor.Blue);
        intakeEntry.setDouble(rightMotor.get());
    }

    public void stopIntake() {
        rightMotor.stopMotor();
        leftMotor.stopMotor();
        leds.stopLeds();
        intakeEntry.setDouble(0); // stopMotor() doesnt update cached speed that rightMotor.get() returns
    }

    public CommandBase getIntakeDiagnosticCommand() {
        return Commands.race(
            Commands.run(() -> intake(), this),
            new WaitCommand(3)
        )
        .finallyDo((boolean interrupted) -> {
            stopIntake();
        })
        .andThen(new WaitCommand(3))
        .andThen(Commands.race(
            Commands.run(() -> placePiece(), this),
            new WaitCommand(3)
        ))
        .finallyDo((boolean interrupted) -> {
            stopIntake();
        })
        .withName("Run Intake Diagnostic");
    }
}