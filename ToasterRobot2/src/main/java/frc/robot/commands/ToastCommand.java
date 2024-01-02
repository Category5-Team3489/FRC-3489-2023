package frc.robot.commands;

import java.lang.ModuleLayer.Controller;

import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Toast;

public class ToastCommand extends CommandBase {
    private final Toast m_toast;

    public ToastCommand(Toast toast) {
        m_toast = toast;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(toast);
    }

    //move the toast up or down depending on the boolean
    public void moveToast(boolean movement) {
        m_toast.leftSolenoid.set(movement);
        m_toast.rightSolenoid.set(movement);
    }

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {}

    @Override
    public boolean isFinished() {
        return false;
    }
}
