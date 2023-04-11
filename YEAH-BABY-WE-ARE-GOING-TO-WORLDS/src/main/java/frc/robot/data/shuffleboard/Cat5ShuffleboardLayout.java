package frc.robot.data.shuffleboard;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public enum Cat5ShuffleboardLayout {
    Vitals(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList),
    Offsets_Config(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList);

    private final Cat5ShuffleboardTab tab;
    private final LayoutType type;
    
    private Cat5ShuffleboardLayout(Cat5ShuffleboardTab tab, LayoutType type) {
        this.tab = tab;
        this.type = type;
    }

    public ShuffleboardLayout create() {
        return tab.get().getLayout(toString(), type);
    }
}
