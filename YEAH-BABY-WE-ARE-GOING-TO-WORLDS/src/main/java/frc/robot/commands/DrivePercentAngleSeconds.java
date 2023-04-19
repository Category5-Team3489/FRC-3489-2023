package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class DrivePercentAngleSeconds extends CommandBase {
    // State
    private final Drivetrain drivetrain;
    private final double percent;
    private final double angleDegrees;
    private final double seconds;
    private final Timer timer = new Timer();

    public DrivePercentAngleSeconds(Drivetrain drivetrain, double percent, double angleDegrees, double seconds) {
        this.drivetrain = drivetrain;
        this.percent = percent;
        this.angleDegrees = angleDegrees;
        this.seconds = seconds;

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        timer.restart();
    }

    @Override
    public void execute() {
        drivetrain.drivePercentAngle(percent, angleDegrees);
    }

    @Override
    public boolean isFinished() {
        return timer.hasElapsed(seconds);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.drivePercentAngle(0, angleDegrees);
    }
}
