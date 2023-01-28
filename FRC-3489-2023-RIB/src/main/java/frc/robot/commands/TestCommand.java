// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands;

import frc.robot.subsystems.TestSubsystem;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TestCommand extends CommandBase {
    public TestCommand(TestSubsystem subsystem) {
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
