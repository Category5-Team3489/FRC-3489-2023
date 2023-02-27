package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class Brake extends CommandBase {
    public Brake() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void execute() {
        Drivetrain.get().setFrontLeftPercentAngle(0, Math.toRadians(45 + 90));
        Drivetrain.get().setFrontRightPercentAngle(0, Math.toRadians(45));
        Drivetrain.get().setBackLeftPercentAngle(0, Math.toRadians(45));
        Drivetrain.get().setBackRightPercentAngle(0, Math.toRadians(45 + 90));
    }
}
