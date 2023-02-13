package frc.robot;

public class Cat5Math {
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

    public static double modifyAxis(double value) {
        value = deadband(value, 0.05);

        value = Math.copySign(value * value, value);
    
        return value;
    }

    public static double lerp(double a, double b, double t) {
        return a + t * (b - a);
    }
    public static double inverseLerp(double x, double a, double b) {
        return (x - a) / (b - a);
    }

    public static double offsetAngle(double angrad, double offsetangrad) {
		double a = angrad + offsetangrad;
		while (a < 0) {
			a += 2.0 * Math.PI;
		}
		while (a >= 2.0 * Math.PI) {
			a -= 2.0 * Math.PI;
		}
		if (a < 0) {
			a = 0;
		}
		if (a > 2.0 * Math.PI) {
			a = 2.0 * Math.PI;
		}
		return a;
	}
}
