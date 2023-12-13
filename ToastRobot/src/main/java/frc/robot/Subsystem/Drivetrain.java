package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;


public class Drivetrain extends TimedRobot {

  /*
  https://docs.wpilib.org/en/stable/docs/software/commandbased/what-is-command-based.html
  https://github.com/Category5-Team3489/FRC-3489-2023/tree/main/YEAH-BABY-WE-ARE-GOING-TO-WORLDS/src/main/java/frc/robot
  */

  private XboxController meController = new XboxController(0);

  private WPI_TalonSRX frontLeft = new WPI_TalonSRX(1);
  private WPI_TalonSRX frontRight = new WPI_TalonSRX(2);
  private WPI_TalonSRX backLeft = new WPI_TalonSRX(3);
  private WPI_TalonSRX backRight = new WPI_TalonSRX(4);

  private DifferentialDrive differentialDrive;

  @Override
  public void robotInit() {
    differentialDrive = new DifferentialDrive(frontLeft, frontRight);
    frontRight.setInverted(true);
    backRight.setInverted(true);
    backLeft.follow(frontLeft);
    backRight.follow(frontRight);
  }

  @Override
  public void robotPeriodic() {}
  
  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    double leftinput = meController.getLeftY();
    double rightinput = meController.getRightY();
    
    differentialDrive.tankDrive(leftinput, rightinput);
  }
}
