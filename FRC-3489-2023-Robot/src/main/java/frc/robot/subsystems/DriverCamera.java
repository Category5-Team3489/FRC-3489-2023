// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants.DriverCameraConstants;
import frc.robot.shuffleboard.Cat5Shuffleboard;

public class DriverCamera extends SubsystemBase {
    private final ShuffleboardTab tab = Cat5Shuffleboard.getMainTab();
    private final GenericEntry servoEntry = tab.add("Camera Servo", 0.0).getEntry();
    
    private final Servo servo = new Servo(DriverCameraConstants.ServoChannel);

    private int servoPositionIndex = DriverCameraConstants.ServoStartingPositionIndex;
    private boolean servoPositionIndexDirectionReversed = false;

    public DriverCamera() {
        register();

        Runnable resetServoOnTeleopEnabled = () -> {
            servoPositionIndex = DriverCameraConstants.ServoStartingPositionIndex;
            servoPositionIndexDirectionReversed = false;
            setServoAngle(DriverCameraConstants.ServoPositions[DriverCameraConstants.ServoStartingPositionIndex]);
        };

        new Trigger(() -> DriverStation.isTeleopEnabled())
            .onTrue(Commands.runOnce(resetServoOnTeleopEnabled, this));

        servoPositionIndex = DriverCameraConstants.ServoStartingPositionIndex;

        ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("Driver Camera");
        diagnosticLayout.withSize(2, 1);
        diagnosticLayout.add("Servo Diagnostic", getServoDiagnosticCommand());
    }

    public void indexServoPosition() {
        if (!servoPositionIndexDirectionReversed)
            servoPositionIndex++;
        else
            servoPositionIndex--;
            
        if (servoPositionIndex == DriverCameraConstants.ServoPositions.length) {
            servoPositionIndexDirectionReversed = true;
            servoPositionIndex = DriverCameraConstants.ServoPositions.length - 2;
        }
        else if (servoPositionIndex == -1) {
            servoPositionIndexDirectionReversed = false;
            servoPositionIndex = 1;
        }

        setServoAngle(DriverCameraConstants.ServoPositions[servoPositionIndex]);
    }

    private void setServoAngle(double angle) {
        servo.setAngle(angle);
        servoEntry.setDouble(angle);
    }

    private int servoDiagnosticServoPositionIndexAtStart;
    private boolean servoDiagnosticServoIndexDirectionReversedAtStart;
    private CommandBase getServoDiagnosticCommand() {
        Timer timer = new Timer();
        return Commands.sequence(
            Commands.runOnce(() -> {
                servoDiagnosticServoPositionIndexAtStart = servoPositionIndex;
                servoDiagnosticServoIndexDirectionReversedAtStart = servoPositionIndexDirectionReversed;
            }, this),
            Commands.run(() -> {
                timer.start();
                if (timer.advanceIfElapsed(0.5)) {
                    indexServoPosition();
                }
            })
        )
        .withTimeout(4)
        .andThen(() -> {
            servoPositionIndex = servoDiagnosticServoPositionIndexAtStart;
            servoPositionIndexDirectionReversed = servoDiagnosticServoIndexDirectionReversedAtStart;
            setServoAngle(DriverCameraConstants.ServoPositions[servoPositionIndex]);
        }, this)
        .withName("Run Servo Diagnostic");
    }
}
