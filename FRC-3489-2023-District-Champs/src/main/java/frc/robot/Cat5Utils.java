package frc.robot;

import edu.wpi.first.wpilibj.Timer;

public class Cat5Utils {
    public static void time() {
        System.out.print("[" + ((int)(Timer.getMatchTime() * 100)) / 100.0 + "s] [" + ((int)(Timer.getFPGATimestamp() * 100)) / 100.0 + "s]: ");
    }
}
