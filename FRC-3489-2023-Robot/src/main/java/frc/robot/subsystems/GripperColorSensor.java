package frc.robot.subsystems;

import com.revrobotics.ColorSensorV3;
import com.revrobotics.ColorSensorV3.LEDPulseFrequency;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.shuffleboard.Cat5Shuffleboard;

public class GripperColorSensor {
    private final ColorSensorV3 sensor = new ColorSensorV3(I2C.Port.kOnboard);

    private final ShuffleboardLayout layout = Cat5Shuffleboard.createMainLayout("Gripper Color Sensor");

    public GripperColorSensor() {
        // sensor.getProximity()
        // sensor.getRawColor()
    }
}
