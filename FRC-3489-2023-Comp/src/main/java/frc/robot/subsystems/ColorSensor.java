package frc.robot.subsystems;

import com.revrobotics.ColorMatch;
import com.revrobotics.ColorMatchResult;
import com.revrobotics.ColorSensorV3;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Cat5Subsystem;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class ColorSensor extends Cat5Subsystem<Gripper>{
    private static ColorSensor instance;

    public static ColorSensor get() {
        if (instance == null) {
            instance = new ColorSensor();
        }

        return instance;
    }

    private final I2C.Port i2cPort = I2C.Port.kOnboard;
    private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);

    private GenericEntry r;
    private GenericEntry g;
    private GenericEntry b;
    private GenericEntry c;
    private GenericEntry color;
    private GenericEntry e;
    private GenericEntry f;
    private GenericEntry gg;
    private GenericEntry p;

    private final Color kBlueTarget = new Color(0.356689453125, 0.215087890625, 0.428466796875);
    private final Color kGreenTarget = new Color(0.197, 0.561, 0.240);
    private final Color kRedTarget = new Color(0.561, 0.232, 0.114);
    private final Color kYellowTarget = new Color(0.552001953125, 0.373046875, 0.075439453125);

    private final ColorMatch m_colorMatcher = new ColorMatch();

    private final Translation3d cube = new Translation3d(0.356689453125, 0.215087890625, 0.428466796875);
    private final Translation3d cone = new Translation3d(0.552001953125, 0.373046875, 0.075439453125);
    private final Translation3d nothing = new Translation3d(0.48974609375, 0.29443359375, 0.216064453125);

    public ColorSensor() {
        super(null);

        m_colorMatcher.addColorMatch(kBlueTarget);
        m_colorMatcher.addColorMatch(kGreenTarget);
        m_colorMatcher.addColorMatch(kRedTarget);
        m_colorMatcher.addColorMatch(kYellowTarget);   
    }

    @Override
    protected void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.add("Subsystem Info", this);

        r = layout.add("Red", -1.0).getEntry();
        g = layout.add("Green", -1.0).getEntry();
        b = layout.add("Blue", -1.0).getEntry();
        c = layout.add("Confidence", -1.0).getEntry();
        color = layout.add("Color", "").getEntry();
        e = layout.add("E", "").getEntry();
        f = layout.add("F", "").getEntry();
        gg = layout.add("G", 0.0).getEntry();
        p = layout.add("p", 0.0).getEntry();
    }

    public void colorSensor() {
        Color detectedColor = colorSensor.getColor();
        r.setDouble(detectedColor.red);
        g.setDouble(detectedColor.green);
        b.setDouble(detectedColor.blue);
        p.setDouble(colorSensor.getProximity());
    
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
    
}
