package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.util.Color;

public class Cat5Utils {
    public static double linearAxis(double value, double deadband) {
        value = deadband(value, deadband);
        return value;
    }
    public static double quadraticAxis(double value, double deadband) {
        value = deadband(value, deadband);
        value = Math.copySign(value * value, value);
        return value;
    }
    public static double deadband(double value, double deadband) {
        if (Math.abs(value) > deadband) {
            if (value > 0.0) {
                return (value - deadband) / (1.0 - deadband);
            } else {
                return (value + deadband) / (1.0 - deadband);
            }
        } else {
            return 0.0;
        }
    }
    
    public static Color hex2Rgb(String colorStr) {
        return new Color(
            Integer.valueOf(colorStr.substring(1, 3), 16),
            Integer.valueOf(colorStr.substring(3, 5), 16),
            Integer.valueOf(colorStr.substring(5, 7), 16)
        );
    }

    public static double wrapAngle(double angleRadians) {
        return MathUtil.inputModulus(angleRadians, 0, 2.0 * Math.PI);
	}

    public static Pose2d lerpUnclamped(Pose2d startValue, Pose2d endValue, double t) {
        return startValue.plus((endValue.minus(startValue)).times(t));
    }
    public static double lerpUnclamped(double a, double b, double t) {
        return a + t * (b - a);
    }
    public static double inverseLerpUnclamped(double x, double a, double b) {
        return (x - a) / (b - a);
    }
}
