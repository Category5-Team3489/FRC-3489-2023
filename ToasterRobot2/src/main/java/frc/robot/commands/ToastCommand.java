package frc.robot.commands;

import java.lang.ModuleLayer.Controller;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;
import frc.robot.subsystems.Toast;

public class ToastCommand extends CommandBase {
    private final Toast toast;
    private final XboxController xbox;

    public ToastCommand(Toast toast, XboxController xbox) {
        this.toast = toast;
        this.xbox = xbox;
        
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(toast);
    }

    //move the toast up or down depending on the boolean
    

    @Override
    public void initialize() {
    }

    @Override
    public void execute() {
        if(xbox.getYButtonPressed()) {
            toast.moveToast(true);
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
