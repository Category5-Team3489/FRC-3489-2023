// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.net.PortForwarder;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {
    private RobotContainer robotContainer;

    private Command autonomousCommand;

    @Override
    public void robotInit() {
        // No warnings for joysticks during simulations
        DriverStation.silenceJoystickConnectionWarning(isSimulation());

        // Allow limelight dashboard access through roboRIO usb
        for (int port = 5800; port <= 5805; port++) {
            PortForwarder.add(port, "limelight.local", port);
            PortForwarder.add(port, "10.34.89.11", port);
        }

        // No live window loop overruns
        LiveWindow.setEnabled(false);
        LiveWindow.disableAllTelemetry();

        // Configure logging
        DataLogManager.logNetworkTables(false);
        var dataLog = DataLogManager.getLog();
        DriverStation.startDataLog(dataLog, true);

        robotContainer = new RobotContainer(this, dataLog);
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();

        robotContainer.update(Timer.getFPGATimestamp());
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
    public void disabledExit() {
        robotContainer.disabledExit();
    }

    @Override
    public void autonomousInit() {
        autonomousCommand = robotContainer.getAutonomousCommand();

        if (autonomousCommand != null) {
            autonomousCommand.schedule();
        }

        robotContainer.autonomousInit();
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

        robotContainer.teleopInit();
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
