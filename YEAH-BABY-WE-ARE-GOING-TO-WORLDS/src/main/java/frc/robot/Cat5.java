package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public final class Cat5 {
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
}
