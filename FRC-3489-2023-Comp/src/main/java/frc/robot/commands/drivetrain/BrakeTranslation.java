package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class BrakeTranslation extends CommandBase {
    public BrakeTranslation() {
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
