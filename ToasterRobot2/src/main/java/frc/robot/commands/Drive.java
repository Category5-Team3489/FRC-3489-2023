package frc.robot.commands;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.DriveTrain;

public class Drive extends CommandBase{
  private final DriveTrain driveTrain;
  private final XboxController xbox;

  public Drive(DriveTrain driveTrain, XboxController xbox) {
      this.driveTrain = driveTrain;
      this.xbox = xbox;

      // Use addRequirements() here to declare subsystem dependencies.
      addRequirements(driveTrain);
    }

    private DifferentialDrive differentialDrive;


  @Override
  public void execute() {

    differentialDrive = new DifferentialDrive(driveTrain.frontLeft, driveTrain.frontRight);

    double leftinput = xbox.getLeftY();
    double rightinput = xbox.getRightY();

    
    differentialDrive.tankDrive(leftinput, rightinput);
  }
    
}
