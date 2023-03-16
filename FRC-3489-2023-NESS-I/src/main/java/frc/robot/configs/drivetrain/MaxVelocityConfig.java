package frc.robot.configs.drivetrain;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import frc.robot.configs.Cat5Config;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static edu.wpi.first.wpilibj2.command.Commands.*;
import static frc.robot.Constants.DrivetrainConstants.*;

public class MaxVelocityConfig extends Cat5Config {
    // Constants
    private static final String MaxVelocityMetersPerSecondPreferencesKey = "Drivetrain/MaxVelocityMetersPerSecond";

    // State
    private double maxVelocityMetersPerSecond;

    public MaxVelocityConfig() {
        maxVelocityMetersPerSecond = Preferences.getDouble(MaxVelocityMetersPerSecondPreferencesKey, TheoreticalMaxVelocityMetersPerSecond);

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.addDouble("Max Velocity (m per s)", () -> getMaxVelocityMetersPerSecond());
        layout.addDouble("Max Angular Velocity (rad per s)", () -> getMaxAngularVelocityRadiansPerSecond());

        var setMaxVelocityEntry = layout.add("Set Max Velocity (m per s)", maxVelocityMetersPerSecond).getEntry();
        layout.add(runOnce(() -> {
            maxVelocityMetersPerSecond = setMaxVelocityEntry.getDouble(maxVelocityMetersPerSecond);
            Preferences.setDouble(MaxVelocityMetersPerSecondPreferencesKey, maxVelocityMetersPerSecond);
        })
            .ignoringDisable(true)
            .withName("Save")
        );
        //#endregion
    }

    //#region Public
    public double getMaxVelocityMetersPerSecond() {
        return maxVelocityMetersPerSecond;
    }

    public double getMaxAngularVelocityRadiansPerSecond() {
        return maxVelocityMetersPerSecond / Math.hypot(WheelsLeftToRightMeters / 2.0, WheelsFrontToBackMeters / 2.0);
    }
    //#endregion
}