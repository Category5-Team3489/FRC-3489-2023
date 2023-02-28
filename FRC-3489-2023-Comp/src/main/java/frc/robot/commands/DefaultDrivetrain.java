package frc.robot.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Cat5Utils;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.Constants.DrivetrainConstants;
import frc.robot.subsystems.Drivetrain;

public class DefaultDrivetrain extends CommandBase {
    public DefaultDrivetrain() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void execute() {
        if (DriverStation.isAutonomous()) {
            Drivetrain.get().setFrontLeftPercentAngle(0, 0);
            Drivetrain.get().setFrontRightPercentAngle(0, 0);
            Drivetrain.get().setBackLeftPercentAngle(0, 0);
            Drivetrain.get().setBackRightPercentAngle(0, 0);
            return;
        }

        // double maxVelocityMetersPerSecond = Drivetrain.get().maxVelocityConfig.getMaxVelocityMetersPerSecond.getAsDouble();
        // double maxAngularVelocityRadiansPerSecond = Drivetrain.get().maxVelocityConfig.getMaxAngularVelocityRadiansPerSecond.getAsDouble();

        // double x = -RobotContainer.get().xbox.getLeftX();
        // x = Cat5Utils.quadraticAxis(x, DrivetrainConstants.XboxAxisDeadband);
        // x *= maxVelocityMetersPerSecond;
        // double y = -RobotContainer.get().xbox.getLeftY();
        // y = Cat5Utils.quadraticAxis(y, DrivetrainConstants.XboxAxisDeadband);
        // y *= maxVelocityMetersPerSecond;
        // double theta = -RobotContainer.get().xbox.getRightX();
        // theta = Cat5Utils.quadraticAxis(theta, DrivetrainConstants.XboxAxisDeadband);
        // theta *= maxAngularVelocityRadiansPerSecond;

        // double corLeft = RobotContainer.get().xbox.getLeftTriggerAxis();
        // if (corLeft < 0.05) {

        // }
        // else if (corLeft < 0.15) {

        // }
        // else {

        // }
        // double corRight = RobotContainer.get().xbox.getRightTriggerAxis();
        


        // Get input

        // Create chassis speeds
    }
}
