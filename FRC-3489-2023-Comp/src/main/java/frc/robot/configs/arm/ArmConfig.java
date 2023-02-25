package frc.robot.configs.arm;

import java.util.function.BooleanSupplier;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import frc.robot.configs.Cat5Config;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class ArmConfig extends Cat5Config {
    public final BooleanSupplier isFullFunctionalityEnabled;

    public ArmConfig() {
        var layout = getLayout(Cat5ShuffleboardTab.Arm, BuiltInLayouts.kList)
            .withSize(2, 3);

        var enableFullFunctionalityEntry = layout.add("Enable Full Functionality", false)
            .withWidget(BuiltInWidgets.kToggleSwitch)
            .getEntry();
        isFullFunctionalityEnabled = () -> enableFullFunctionalityEntry.getBoolean(false);
    }
}
