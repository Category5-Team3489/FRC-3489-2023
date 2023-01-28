// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.TestSubsystem;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TestCommand extends CommandBase {
    private final TestSubsystem s;

    public TestCommand(TestSubsystem subsystem) {
        s = subsystem;

        addRequirements(subsystem);
    }

    @Override
    public void initialize() {
        System.out.println("initialize");
        t.start();
    }

    Timer t = new Timer();

    @Override
    public void execute() {
        System.out.println("execute");

        if (!DriverStation.isTeleopEnabled())
        {
            return;
        }

        if (!s.orchestra.isPlaying())
        {
                    s.orchestra.play();
        }

        // s.testFalcon.set(ControlMode.PercentOutput, 0.15);
        

        // s.testFalcon.set(0.3);

        /*
        if (t.advanceIfElapsed(5))
        {
            cancel();
        }
        */
    }

    @Override
    public void end(boolean interrupted) {
        System.out.println("End: " + interrupted);
    }
}
