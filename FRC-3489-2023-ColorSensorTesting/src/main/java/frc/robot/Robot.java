// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.util.Color;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  private final I2C.Port i2cPort = I2C.Port.kOnboard;
  private final ColorSensorV3 m_colorSensor = new ColorSensorV3(i2cPort);

  private final ShuffleboardTab tab = Shuffleboard.getTab("Testing");
  private final GenericEntry r = tab.add("Red", -1.0).getEntry();
  private final GenericEntry g = tab.add("Green", -1.0).getEntry();
  private final GenericEntry b = tab.add("Blue", -1.0).getEntry();
  private final GenericEntry c = tab.add("Confidence", -1.0).getEntry();
  private final GenericEntry color = tab.add("Color", "").getEntry();
  private final GenericEntry e = tab.add("E", "").getEntry();
  private final GenericEntry f = tab.add("F", "").getEntry();
  private final GenericEntry gg = tab.add("G", 0.0).getEntry();
  private final GenericEntry p = tab.add("p", 0.0).getEntry();

  private final Color kBlueTarget = new Color(0.356689453125, 0.215087890625, 0.428466796875);
  private final Color kGreenTarget = new Color(0.197, 0.561, 0.240);
  private final Color kRedTarget = new Color(0.561, 0.232, 0.114);
  private final Color kYellowTarget = new Color(0.552001953125, 0.373046875, 0.075439453125);

  private final ColorMatch m_colorMatcher = new ColorMatch();

  private final Translation3d cube = new Translation3d(0.356689453125, 0.215087890625, 0.428466796875);
  private final Translation3d cone = new Translation3d(0.552001953125, 0.373046875, 0.075439453125);
  private final Translation3d nothing = new Translation3d(0.48974609375, 0.29443359375, 0.216064453125);

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_colorMatcher.addColorMatch(kBlueTarget);
    m_colorMatcher.addColorMatch(kGreenTarget);
    m_colorMatcher.addColorMatch(kRedTarget);
    m_colorMatcher.addColorMatch(kYellowTarget);   
    // m_colorMatcher.setConfidenceThreshold(0); // 0 to 1
  }

  @Override
  public void robotPeriodic() {
    Color detectedColor = m_colorSensor.getColor();
    r.setDouble(detectedColor.red);
    g.setDouble(detectedColor.green);
    b.setDouble(detectedColor.blue);
    p.setDouble(m_colorSensor.getProximity());

    Translation3d current = new Translation3d(detectedColor.red, detectedColor.green, detectedColor.blue);
    double coneDist = current.getDistance(cone);
    double nothingDist = current.getDistance(nothing);
    double cubeDist = current.getDistance(cube);

    if (coneDist < cubeDist)
    {
      f.setString("Cone");
    }
    else
    {
      f.setString("Cube");
    }
    gg.setDouble(nothingDist);
    
    if (coneDist < nothingDist)
    {
      // cone
      if (coneDist < cubeDist)
      {
        // cone
        e.setString("Cone");
      }
      else
      {
        // cube
        e.setString("Cube");
      }
    }
    else
    {
      if (nothingDist < cubeDist)
      {
        // nothing
        e.setString("Nothing");
      }
      else
      {
        // cube
        e.setString("Cube");
      }
    }

    String colorString;
    ColorMatchResult match = m_colorMatcher.matchClosestColor(detectedColor);

    if (match.color == kBlueTarget) {
      colorString = "Blue";
    } else if (match.color == kRedTarget) {
      colorString = "Red";
    } else if (match.color == kGreenTarget) {
      colorString = "Green";
    } else if (match.color == kYellowTarget) {
      colorString = "Yellow";
    } else {
      colorString = "Unknown";
    }

    c.setDouble(match.confidence);
    color.setString(colorString);

    System.out.println(detectedColor.toHexString());
  }

  @Override
  public void autonomousInit() {}

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void teleopInit() {}

  @Override
  public void teleopPeriodic() {}

  @Override
  public void disabledInit() {}

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
}
