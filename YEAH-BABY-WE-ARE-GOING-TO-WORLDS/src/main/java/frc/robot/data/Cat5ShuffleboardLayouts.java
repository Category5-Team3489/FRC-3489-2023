package frc.robot.data;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public class Cat5ShuffleboardLayouts {
    public final ShuffleboardLayout vitals = Cat5ShuffleboardTab.Main.get().getLayout("Vitals", BuiltInLayouts.kList);
}
