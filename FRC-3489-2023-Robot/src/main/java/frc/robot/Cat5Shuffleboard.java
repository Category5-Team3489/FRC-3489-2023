package frc.robot;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;

public class Cat5Shuffleboard {
    public static ShuffleboardTab getMainTab() {
        return Shuffleboard.getTab("Main");
    }
    public static final ShuffleboardLayout createMainLayout(String title) {
        return getMainTab()
            .getLayout(title, BuiltInLayouts.kList);
    }

    public static ShuffleboardTab getConstantsTab() {
        return Shuffleboard.getTab("Constants");
    }
    public static final ShuffleboardLayout createConstantsLayout(String title) {
        return getDiagnosticTab()
            .getLayout(title, BuiltInLayouts.kList);
    }

    public static ShuffleboardTab getDiagnosticTab() {
        return Shuffleboard.getTab("Diagnostic");
    }
    public static final ShuffleboardLayout createDiagnosticLayout(String title) {
        return getDiagnosticTab()
            .getLayout(title, BuiltInLayouts.kList);
    }
}
