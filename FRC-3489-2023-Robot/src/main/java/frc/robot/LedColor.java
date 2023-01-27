package frc.robot;

import edu.wpi.first.wpilibj.AddressableLEDBuffer;

public class LedColor {
    public static final LedColor Off = LedColor.rgb(0, 0, 0);
    public static final LedColor White = LedColor.rgb(255, 255, 255);

    private boolean isHSV;
    private int a;
    private int b;
    private int c;

    private LedColor(boolean isHSV, int a, int b, int c) {
        this.isHSV = isHSV;
        this.a = a;
        this.b = b;
        this.c = c;
    }

    /**
     * Sets a specific led in the buffer.
     *
     * @param r the r value [0-255]
     * @param g the g value [0-255]
     * @param b the b value [0-255]
     */
    public static LedColor rgb(int r, int g, int b) {
        return new LedColor(false, r, g, b);
    }

    /**
     * Sets a specific led in the buffer.
     *
     * @param h the h value [0-180)
     * @param s the s value [0-255]
     * @param v the v value [0-255]
     */
    public static LedColor hsv(int h, int s, int v) {
        return new LedColor(true, h, s, v);
    }

    public void apply(int i, AddressableLEDBuffer buffer) {
        if (!isHSV) {
            buffer.setRGB(i, a, b, c);
        }
        else {
            buffer.setHSV(i, a, b, c);
        }
    }
}
