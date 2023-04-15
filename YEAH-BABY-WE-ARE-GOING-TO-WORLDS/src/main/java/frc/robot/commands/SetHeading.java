package frc.robot.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;

public class SetHeading extends CommandBase {
    // State
    private final Drivetrain drivetrain;
    private final Rotation2d targetHeading;
    private final double degreesPerSecond;
    private final double aroundTargetHeadingSeconds;
    private final Timer timer = new Timer();

    public SetHeading(Drivetrain drivetrain, Rotation2d targetHeading, double degreesPerSecond, double aroundTargetHeadingSeconds) {
        this.drivetrain = drivetrain;
        this.targetHeading = targetHeading;
        this.degreesPerSecond = degreesPerSecond;
        this.aroundTargetHeadingSeconds = aroundTargetHeadingSeconds;
        
        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        timer.stop();
        timer.reset();
    }

    @Override
    public void execute() {
        drivetrain.driveFieldRelative(0, 0, 1.0, targetHeading, degreesPerSecond);
    
        if (drivetrain.isAroundTargetHeading()) {
            timer.start();
        }
    }

    @Override
    public boolean isFinished() {
        return timer.hasElapsed(aroundTargetHeadingSeconds);
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.brakeTranslation();
    }
}
