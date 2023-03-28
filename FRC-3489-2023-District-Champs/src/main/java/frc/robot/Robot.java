// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.net.PortForwarder;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import frc.robot.data.Cat5Datapoint;

public class Robot extends TimedRobot {
    private Command autonomousCommand;
    private Cat5Autos autos;

    private DoubleLogEntry timeLog = new DoubleLogEntry(DataLogManager.getLog(), "/time");
    private DoubleLogEntry sinLog = new DoubleLogEntry(DataLogManager.getLog(), "/sin");

    @Override
    public void robotInit() {
        DriverStation.silenceJoystickConnectionWarning(isSimulation());

        for (int port = 5800; port <= 5805; port++) {
            PortForwarder.add(port, "limelight.local", port);
        }

        LiveWindow.setEnabled(false);
        LiveWindow.disableAllTelemetry();

        DriverStation.startDataLog(DataLogManager.getLog());

        RobotContainer.get();
        autos = new Cat5Autos();
    }

    Cat5Datapoint<Double> e = new Cat5Datapoint<Double>(null, null, 2, 4);

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();

        timeLog.append(Timer.getFPGATimestamp());
        sinLog.append(Math.sin(Timer.getFPGATimestamp()));

        e.update(Timer.getFPGATimestamp());
    }

    @Override
    public void simulationInit() {}

    @Override
    public void simulationPeriodic() {}

    @Override
    public void disabledInit() {}

    @Override
    public void disabledPeriodic() {}

    @Override
    public void disabledExit() {}

    @Override
    public void autonomousInit() {
        autonomousCommand = autos.getAutonomousCommand();

        if (autonomousCommand != null) {
            autonomousCommand.schedule();
        }
    }

    @Override
    public void autonomousPeriodic() {}

    @Override
    public void autonomousExit() {}

    @Override
    public void teleopInit() {
        if (autonomousCommand != null) {
            autonomousCommand.cancel();
        }
    }

    @Override
    public void teleopPeriodic() {}

    @Override
    public void teleopExit() {}

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {}

    @Override
    public void testExit() {}
}
