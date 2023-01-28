package frc.robot.subsystems;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj2.command.SubsystemBase;


public class Leds extends SubsystemBase{
    public final AddressableLED led = new AddressableLED(1);
    public final AddressableLEDBuffer buffer = new AddressableLEDBuffer(64);

    public Leds() {
        register();
        
        led.setLength(buffer.getLength());
        led.setData(buffer);
        led.start();
    }

    public void setSolidColor(int r, int g, int b) {
        for (var i = 0; i < buffer.getLength(); i++) {
            buffer.setRGB(i, r, g, b);
        }
        led.setData(buffer);
        System.out.println("DSAINNIASINDAS");
    }

    
    public void stopLeds() {
        setSolidColor(0, 0, 0);
    }
}
