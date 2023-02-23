package frc.robot;

import edu.wpi.first.math.MathUtil;

public class Cat5Utils {
    public static double wrapAngle(double angleRadians) {
        return MathUtil.inputModulus(angleRadians, 0, 2.0 * Math.PI);
	}
}
