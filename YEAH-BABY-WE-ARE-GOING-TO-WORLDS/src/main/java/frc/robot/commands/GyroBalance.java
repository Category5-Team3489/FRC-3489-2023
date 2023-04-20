package frc.robot.commands;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.NavX2;

public class GyroBalance extends CommandBase {
    // State
    private final NavX2 navx;
    private final Drivetrain drivetrain;
    private final Timer timer = new Timer();

    public GyroBalance(NavX2 navx, Drivetrain drivetrain) {
        this.navx = navx;
        this.drivetrain = drivetrain;
    }

    @Override
    public void initialize() {
        timer.restart();
    }

    @Override
    public void execute() {
        double percent = 0;
        double tilt = navx.getTilt();

        if (Math.abs(tilt) < 7) {
            timer.restart();
        }
        else if (timer.hasElapsed(1)) {
            if (!timer.hasElapsed(1.25)) {
                if (tilt < 0) {
                    percent = 0.12;
                }
                else {
                    percent = -0.12;
                }
            }
            else {
                timer.restart();
            }
        }

        drivetrain.drivePercentAngle(percent, 0);
    }
}
