// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class NavX2 extends SubsystemBase {
    private final AHRS navx = new AHRS(SPI.Port.kMXP);

    public final Trigger IsCalibrated = new Trigger(() -> !navx.isCalibrating());
    
    public NavX2() {

    }

    public void zeroYaw() {
        navx.zeroYaw();
        System.out.println("[NavX2] zeroed yaw");
    }

    public Rotation2d getRotation() {
        // if (navx.isMagnetometerCalibrated()) {
        //     return Rotation2d.fromDegrees(navx.getFusedHeading());
        // }

        return Rotation2d.fromDegrees(360.0 - navx.getYaw());
    }
}
