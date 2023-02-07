// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.function.DoubleSupplier;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
    private final ShuffleboardTab tab = Shuffleboard.getTab("Testing");
    private final DoubleSupplier range;

    private WPI_TalonSRX left = new WPI_TalonSRX(1);
    private WPI_TalonSRX right = new WPI_TalonSRX(3);

    private XboxController xbox = new XboxController(0);

    public Robot() {
        var rangeEntry = tab.add("Range", 0).getEntry();
        range = () -> rangeEntry.getDouble(0);
    }

    /**
     * This function is run when the robot is first started up and should be used for any
     * initialization code.
     */
    @Override
    public void robotInit() {}

    @Override
    public void robotPeriodic() {}

    @Override
    public void autonomousInit() {

    }

    @Override
    public void autonomousPeriodic() {}

    @Override
    public void teleopInit() {

    }

    @Override
    public void teleopPeriodic() {
        double speed = xbox.getRawAxis(1);
        double slow = xbox.getRawAxis(3);
        if (xbox.getRawButton(1)) {
            set(-range.getAsDouble());
        }
        else if (xbox.getRawButton(2)) {
            set(-0.75);
        }
        else if (xbox.getRawButton(3)) {
            set(range.getAsDouble());
        }
        else if (xbox.getRawButton(4)) {
            set(0.75);
        }
        else {
            if (Math.abs(speed) > 0.025) {
                set(-speed);
            }
            else if (Math.abs(slow) > 0.025) {
                set(-slow * 0.25);
            }
            else {
                set(0);
            }
        }
    }

    @Override
    public void disabledInit() {

    }

    @Override
    public void disabledPeriodic() {}

    @Override
    public void testInit() {}

    @Override
    public void testPeriodic() {}

    @Override
    public void simulationInit() {}

    @Override
    public void simulationPeriodic() {}

    long runs = 0;
    private void set(double speed) {
        left.set(speed);
        right.set(-speed);

        if (runs % 2 == 0) {
            System.out.println(speed);
        }

        runs++;
    }
}
