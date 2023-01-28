// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import frc.robot.commands.TestCommand;
import frc.robot.commands.TestLimelight;

public class TestSubsystem extends SubsystemBase {
    private final ShuffleboardTab tab = Shuffleboard.getTab("RIB");
    private GenericEntry testEntry = tab.add("Test", false).getEntry();
    
    public TestSubsystem() {
    
        register();

        /*
        run(() -> System.out.println("bbbbbbbbbbbbbb"))
            .withTimeout(5)
            .andThen(() -> System.out.println("ccccccccccccccc"))
            .ignoringDisable(true)
            .schedule();
        */

        /*
        new TestCommand(this)
            .withInterruptBehavior(InterruptionBehavior.kCancelIncoming)
            .ignoringDisable(true)
            .withTimeout(5)
            .schedule();

        run(() -> System.out.println("Other command was interupted!"))
          .ignoringDisable(true);
          */


        // testing what startEnd does with a timeout
        /*
        Runnable startAction = () -> {
            System.out.println("Start Action");
            testEntry.setBoolean(true);

        };
        Runnable endAction = () -> {
            // what do we want this to do?
            System.out.println("End Action");
            testEntry.setBoolean(false);
        };

        Commands.startEnd(startAction, endAction, this)
            .ignoringDisable(true)
            .withTimeout(5)
            .schedule();
            */

        new WaitCommand(2).andThen(() -> System.out.println("AAAAAAAAAAAAA"))
            .alongWith(new WaitCommand(4).andThen(() -> System.out.println("BBBBBBBBBBBBBB")))
            .ignoringDisable(true)
            .schedule();
    }
}
