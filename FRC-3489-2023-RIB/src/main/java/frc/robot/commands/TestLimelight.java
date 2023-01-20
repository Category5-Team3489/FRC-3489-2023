package frc.robot.commands;

import frc.robot.subsystems.Limelight;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.CommandBase;

public class TestLimelight extends CommandBase {
    private final Limelight limelight;
    private final ShuffleboardTab main = Shuffleboard.getTab("2023 RIB");

    public TestLimelight(Limelight limelight) {
        this.limelight = limelight;

        addRequirements(limelight);
    }

    @Override
    public void initialize() {
        main.addDouble("", limelight.)
    }

    @Override
    public void execute() {
        
    }

    @Override
    public void end(boolean interrupted) {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
