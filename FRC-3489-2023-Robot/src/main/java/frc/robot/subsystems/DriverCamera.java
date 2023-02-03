// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Triggers;
import frc.robot.Constants.CameraConstants;

public class DriverCamera extends SubsystemBase {
    private final ShuffleboardTab tab = Constants.getMainTab();
    private final GenericEntry servoEntry = tab.add("Camera Servo", 0.0).getEntry();
    
    private final Servo servo = new Servo(1);

    private int servoPositionIndex = CameraConstants.ServoStartingPositionIndex;
    private boolean servoPositionIndexDirectionReversed = false;

    public DriverCamera() {
        register();

        Runnable resetServoOnTeleopEnabled = () -> {
            servoPositionIndex = CameraConstants.ServoStartingPositionIndex;
            servoPositionIndexDirectionReversed = false;
            setServoAngle(CameraConstants.ServoPositions[CameraConstants.ServoStartingPositionIndex]);
        };

        Triggers.IsTeleopEnabled.onTrue(Commands.runOnce(resetServoOnTeleopEnabled, this));

        servoPositionIndex = CameraConstants.ServoStartingPositionIndex;

        ShuffleboardLayout diagnosticLayout = Constants.createDiagnosticLayout("Driver Camera");
        diagnosticLayout.withSize(2, 1);
        diagnosticLayout.add("Servo Diagnostic", getServoDiagnosticCommand());
    }

    public void indexServoPosition() {
        if (!servoPositionIndexDirectionReversed)
            servoPositionIndex++;
        else
            servoPositionIndex--;
            
        if (servoPositionIndex == CameraConstants.ServoPositions.length) {
            servoPositionIndexDirectionReversed = true;
            servoPositionIndex = CameraConstants.ServoPositions.length - 2;
        }
        else if (servoPositionIndex == -1) {
            servoPositionIndexDirectionReversed = false;
            servoPositionIndex = 1;
        }

        setServoAngle(CameraConstants.ServoPositions[servoPositionIndex]);
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
            setServoAngle(CameraConstants.ServoPositions[servoPositionIndex]);
        }, this)
        .withName("Run Servo Diagnostic");
    }
}
