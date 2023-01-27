// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class TestSubsystem extends SubsystemBase {
    public TestSubsystem() {
        register();
    }


    boolean ran = false;

    @Override
    public void periodic() {
        if (DriverStation.isTeleopEnabled() && !ran)
        {
            System.out.println("DSADASSDSA");
            ran = true;
            run(() -> System.out.println("aaaaaaaaaaaaaa")).(() -> System.out.println("CANCELED A"), this).schedule();
            run(() -> System.out.println("bbbbbbbbbbbbbb")).schedule();
        }
    }
}
