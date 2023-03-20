package frc.robot.commands.drivetrain;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.subsystems.Drivetrain;

public class DrivePercentAngleSeconds extends CommandBase {
    // State
    private final double percent;
    private final double angleDegrees;
    private final double seconds;
    private final Timer timer = new Timer();

    public DrivePercentAngleSeconds(double percent, double angleDegrees, double seconds) {
        addRequirements(Drivetrain.get());

        this.percent = percent;
        this.angleDegrees = angleDegrees;
        this.seconds = seconds;

        timer.restart();
    }

    @Override
    public void execute() {
        Drivetrain.get().drivePercentAngle(percent, angleDegrees);
    }

    @Override
    public boolean isFinished() {
        return timer.hasElapsed(seconds);
    }

    @Override
    public void end(boolean interrupted) {
        Drivetrain.get().drivePercentAngle(0, angleDegrees);
    }
}
