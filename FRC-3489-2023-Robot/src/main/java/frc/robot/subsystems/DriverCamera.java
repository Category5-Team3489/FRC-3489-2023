// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.util.function.BooleanConsumer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;
import frc.robot.Constants.CameraConstants;

public class DriverCamera extends SubsystemBase {
    private final ShuffleboardTab tab = Constants.getMainTab();
    public final GenericEntry servoEntry = tab.add("Camera Servo", 0.0).getEntry();
    public final ComplexWidget servoDiagnosticWidget = Constants.DiagnosticCommands
        .add("Servo Diagnostic", getServoDiagnosticCommand());
    
    private final Servo servo = new Servo(1);

    private int servoPositionIndex = CameraConstants.ServoStartingPositionIndex;
    private boolean servoPositionIndexDirectionReversed = false;

    // TODO Reset servo position on teleop enable???

    public DriverCamera() {
        register();

        BooleanConsumer initServo = (boolean interrupted) -> {
            setServoAngle(CameraConstants.ServoPositions[CameraConstants.ServoStartingPositionIndex]);
        };

        // Set servo angle to default when enabled into teleop mode for first time
        Commands.waitUntil(() -> DriverStation.isEnabled())
            .ignoringDisable(true)
            .finallyDo(initServo)
            .schedule();

        servoPositionIndex = CameraConstants.ServoStartingPositionIndex;
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
    private CommandBase getServoDiagnosticCommand() {
        Timer timer = new Timer();
        return Commands.sequence(
            Commands.runOnce(() -> {
                servoDiagnosticServoPositionIndexAtStart = servoPositionIndex;
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
            setServoAngle(CameraConstants.ServoPositions[servoPositionIndex]);
        }, this)
        .withName("Run Servo Diagnostic");
    }
}
