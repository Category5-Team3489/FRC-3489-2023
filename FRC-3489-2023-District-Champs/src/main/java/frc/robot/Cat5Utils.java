package frc.robot;

import java.util.Random;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.Timer;

public class Cat5Utils {
    public static final Random Rng = new Random();
    public static final DataLog Log = DataLogManager.getLog();

    public static void time() {
        System.out.print("[" + ((int)(Timer.getMatchTime() * 100)) / 100.0 + "s] [" + ((int)(Timer.getFPGATimestamp() * 100)) / 100.0 + "s]: ");
    }
}
