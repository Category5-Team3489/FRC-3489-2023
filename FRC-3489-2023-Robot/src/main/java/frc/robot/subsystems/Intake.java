// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.IntakeConstants;
import frc.robot.general.LedColor;

public class Intake extends SubsystemBase {
    private final Leds leds;

    private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(IntakeConstants.RightIntakeMotor);
    private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(IntakeConstants.LeftIntakeMotor);

    public Intake(Leds leds) {
        this.leds = leds;

        register();
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

    public void slowPlacePiece() {
        rightMotor.set(-IntakeConstants.SlowPlaceSpeed);
        leftMotor.set(IntakeConstants.SlowPlaceSpeed);
        leds.setSolidColor(LedColor.Blue);
    }

    public void stopIntake() {
        rightMotor.stopMotor();
        leftMotor.stopMotor();
        leds.stopLeds();
    }
}
