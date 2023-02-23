package frc.robot.configs.drivetrain;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configs.Cat5Config;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.DrivetrainConstants.*;

public class MaxVelocityConfig extends Cat5Config {
    private static final String MaxVelocityMetersPerSecondPreferencesKey = "Drivetrain/MaxVelocityMetersPerSecond";

    private double maxVelocityMetersPerSecond;

    public final DoubleSupplier getMaxVelocityMetersPerSecond = () -> maxVelocityMetersPerSecond;
    public final DoubleSupplier getMaxAngularVelocityRadiansPerSecond = () -> maxVelocityMetersPerSecond / Math.hypot(WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0);

    public MaxVelocityConfig() {
        maxVelocityMetersPerSecond = Preferences.getDouble(MaxVelocityMetersPerSecondPreferencesKey, TheoreticalMaxVelocityMetersPerSecond);

        var layout = getLayout(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.addDouble("Max Velocity", getMaxVelocityMetersPerSecond);
        layout.addDouble("Max Angular Velocity", getMaxAngularVelocityRadiansPerSecond);

        var setMaxVelocityEntry = layout.add("Set Max Velocity", maxVelocityMetersPerSecond).getEntry();
        layout.add(Commands.runOnce(() -> {
            maxVelocityMetersPerSecond = setMaxVelocityEntry.getDouble(maxVelocityMetersPerSecond);
            Preferences.setDouble(MaxVelocityMetersPerSecondPreferencesKey, maxVelocityMetersPerSecond);
        })
            .withName("Save")
            .ignoringDisable(true)
        );
    }
}
