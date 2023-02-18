package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.shuffleboard.Cat5Shuffleboard;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Drivetrain.DrivetrainMode;

public class DrivetrainDiagnostics extends Diagnostics<Drivetrain>{    
    private double frontLeftMaxVelocity;
    private double frontRightMaxVelocity;
    private double backLeftMaxVelocity;
    private double backRightMaxVelocity;
    private double averageMaxVelocity;
    
    public DrivetrainDiagnostics(Drivetrain subsystem) {
        super(subsystem);
        
        ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("Drive Train Diagnostic")
            .withSize(2, 4);
        diagnosticLayout.add("Test Max Speeds", getTestMaxSpeedsCommand());
        diagnosticLayout.addDouble("Front Left Velocity", () -> frontLeftMaxVelocity);
        diagnosticLayout.addDouble("Front Right Velocity", () -> frontRightMaxVelocity);
        diagnosticLayout.addDouble("Back Left Velocity", () -> backLeftMaxVelocity);
        diagnosticLayout.addDouble("Back Right Velocity", () -> backRightMaxVelocity);
        diagnosticLayout.addDouble("Average Velocity", () -> averageMaxVelocity);

        // double maxVelocityMetersPerSecond = Preferences.getDouble(PreferencesKeys.DrivetrainMaxVelocityMetersPerSecond, subsystem.theoreticalMaxVelocityMetersPerSecond);
    }

    private void testMaxSpeeds() {
        subsystem.setMode(DrivetrainMode.External);

        subsystem.setPercentAngle(1, 0);

        frontLeftMaxVelocity = subsystem.frontLeftModule.getDriveVelocity();
        frontRightMaxVelocity = subsystem.frontRightModule.getDriveVelocity();
        backLeftMaxVelocity = subsystem.backLeftModule.getDriveVelocity();
        backRightMaxVelocity = subsystem.backLeftModule.getDriveVelocity();

        averageMaxVelocity = (frontLeftMaxVelocity + frontRightMaxVelocity + backLeftMaxVelocity + backRightMaxVelocity) / 4.0;

        // System.out.println("Front Left: " + frontLeftMaxVelocity);
        // System.out.println("Front Right: " + frontRightMaxVelocity);
        // System.out.println("Back Left: " + backLeftMaxVelocity);
        // System.out.println("Back Right: " + backRightMaxVelocity);
    }

    private CommandBase getTestMaxSpeedsCommand () {
        return Commands.runEnd(
            () -> testMaxSpeeds(),
            () -> {
                subsystem.setMode(DrivetrainMode.ChassisSpeeds);
            }
        )
            .withName("Test Max Speeds");
    }
}
