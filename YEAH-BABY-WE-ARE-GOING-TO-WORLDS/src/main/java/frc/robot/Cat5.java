package frc.robot;

import java.util.Random;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public final class Cat5 {
    public static final Random Rng = new Random();
    
    public static void print(String message) {
        StringBuilder builder = new StringBuilder();
        builder.append("[" + ((int)((Timer.getMatchTime()) * 1000)) / 1000.0 + "s] [" + ((int)(Timer.getFPGATimestamp() * 1000)) / 1000.0 + "s]: ");
        builder.append(message);
        System.out.println(builder.toString());
    }

    public static void warning(String message, boolean printTrace) {
        StringBuilder builder = new StringBuilder();
        builder.append("[" + ((int)((Timer.getMatchTime()) * 1000)) / 1000.0 + "s] [" + ((int)(Timer.getFPGATimestamp() * 1000)) / 1000.0 + "s]: ");
        builder.append(message);
        DriverStation.reportWarning(builder.toString(), printTrace);
    }

    public static void error(String message, boolean printTrace) {
        StringBuilder builder = new StringBuilder();
        builder.append("[" + ((int)((Timer.getMatchTime()) * 1000)) / 1000.0 + "s] [" + ((int)(Timer.getFPGATimestamp() * 1000)) / 1000.0 + "s]: ");
        builder.append(message);
        DriverStation.reportError(builder.toString(), printTrace);
    }

    public static double getSign(double value) {
        if (value < 0) {
            return -1;
        }
        return 1;
    }

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

    public static double wrapAngle(double angleRadians) {
        return MathUtil.inputModulus(angleRadians, 0, 2.0 * Math.PI);
	}

    public static double lerpUnclamped(double a, double b, double t) {
        return a + t * (b - a);
    }
    public static Pose2d lerpUnclamped(Pose2d startValue, Pose2d endValue, double t) {
        return startValue.plus((endValue.minus(startValue)).times(t));
    }
    public static double inverseLerpUnclamped(double x, double a, double b) {
        return (x - a) / (b - a);
    }
}
