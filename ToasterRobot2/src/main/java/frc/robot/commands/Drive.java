package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class Drive extends CommandBase{
    private final DriveTrain m_driveTrain;

    public Drive(DriveTrain driveTrain) {
        m_driveTrain = driveTrain;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(driveTrain);
      }

      

      private DifferentialDrive differentialDrive;
      private XboxController Controller = new XboxController(0);
 

    @Override
    public void execute() {

    differentialDrive = new DifferentialDrive(m_driveTrain.frontLeft, m_driveTrain.frontRight);

    double leftinput = Controller.getLeftY();
    double rightinput = Controller.getRightY();

    
    differentialDrive.tankDrive(leftinput, rightinput);
  }
    
}
