// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.LedColor;
import frc.robot.Constants.IntakeConstants;

public class Intake extends SubsystemBase {

    public final WPI_TalonSRX rightMotor = new WPI_TalonSRX(IntakeConstants.RightIntakeMotor);
    public final WPI_TalonSRX leftMotor = new WPI_TalonSRX(IntakeConstants.LeftIntakeMotor);

    Leds leds;

    public Intake() {
        register();

        setDefaultCommand(Commands.run(() -> stopIntake(), this));
    }

    public void intake() {
        rightMotor.set(IntakeConstants.IntakeSpeed);
        leftMotor.set(-IntakeConstants.IntakeSpeed);
        leds.setSolidColor(LedColor.Green);
    }

    public void placePiece() {
        rightMotor.set(-IntakeConstants.IntakeSpeed);
        leftMotor.set(IntakeConstants.IntakeSpeed);
        leds.setSolidColor(LedColor.Yellow);
    }

    public void SlowPlacePiece() {
        rightMotor.set(-IntakeConstants.SlowPlaceSpeed);
        leftMotor.set(IntakeConstants.SlowPlaceSpeed);
        leds.setSolidColor(LedColor.Blue);
    }

    public void stopIntake() {
        rightMotor.stopMotor();
        leftMotor.stopMotor();
        leds.stopLeds();
    }

    @Override
    public void periodic() {

    }

}
