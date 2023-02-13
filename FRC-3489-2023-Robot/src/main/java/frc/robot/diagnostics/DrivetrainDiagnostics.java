package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.shuffleboard.Cat5Shuffleboard;
import frc.robot.subsystems.Drivetrain;


public class DrivetrainDiagnostics extends Diagnostics<Drivetrain>{

    
    public double frontLeftMaxVelocity;
    public double frontRightMaxVelocity;
    public double backLeftMaxVelocity;
    public double backRightMaxVelocity;
    public double averageMaxVelocity;
    public static double maxVelocityMetersPerSecond;
    
    public DrivetrainDiagnostics(Drivetrain subsystem) {
        super(subsystem);
        
        ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("Drive Train Diagnostic")
            .withSize(2, 2);
        diagnosticLayout.add("Drive Train Max Speeds", testMaxSpeedDiagnostic());
        diagnosticLayout.addDouble("Front Left Max Velocity",() -> frontLeftMaxVelocity);
        diagnosticLayout.addDouble("Front Right Max Velocity",() -> frontRightMaxVelocity);
        diagnosticLayout.addDouble("Back Left Max Velocity",() -> backLeftMaxVelocity);
        diagnosticLayout.addDouble("Back Right Max Velocity",() -> backRightMaxVelocity);
        diagnosticLayout.addDouble("Average Max Velocity",() -> averageMaxVelocity);

        System.out.println(subsystem.maxVelocityMetersPerSecond);

        // double maxVelocityMetersPerSecond = Preferences.getDouble(PreferencesKeys.DrivetrainMaxVelocityMetersPerSecond, subsystem.theoreticalMaxVelocityMetersPerSecond);
    }

    private void testMaxSpeed() {
        subsystem.drivePercentAngle(1, 0);
        frontLeftMaxVelocity = subsystem.frontLeftModule.getDriveVelocity();
        frontRightMaxVelocity = subsystem.frontRightModule.getDriveVelocity();
        backLeftMaxVelocity = subsystem.backLeftModule.getDriveVelocity();
        backRightMaxVelocity = subsystem.backLeftModule.getDriveVelocity();

        averageMaxVelocity = (frontLeftMaxVelocity + frontRightMaxVelocity + backLeftMaxVelocity + backRightMaxVelocity) / 4;

        System.out.println("Front Left: " + frontLeftMaxVelocity);
        System.out.println("Front Right: " + frontRightMaxVelocity);
        System.out.println("Back Left: " + backLeftMaxVelocity);
        System.out.println("Back Right: " + backRightMaxVelocity);
    }

    private CommandBase testMaxSpeedDiagnostic () {
        return Commands.run(() -> testMaxSpeed(), subsystem)
        .withTimeout(5)
        .withName("Test Max Speeds");
    }
}
