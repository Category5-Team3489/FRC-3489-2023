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

//Adjust the toast position to up or down based on the button pressed
    @Override
    public void execute() {
        int povValue = xbox.getPOV();
        if(povValue == 0) {
            toast.moveToast(true);
        }if (povValue == 180) {
            toast.moveToast(false);
        }
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
