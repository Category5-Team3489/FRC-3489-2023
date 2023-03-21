package frc.robot.configs.colorsensor;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Commands;

import frc.robot.Cat5Utils;
import frc.robot.Constants;
import frc.robot.configs.Cat5Config;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.subsystems.ColorSensor;

public class ColorAndProximityConfig extends Cat5Config {
    // Constants
    private static final String ConeColorPreferencesKey = "ColorSensor/ConeColor";
    private static final String CubeColorPreferencesKey = "ColorSensor/CubeColor";
    private static final String DetectionProximityPreferencesKey = "ColorSensor/DetectionProximity";

    // State
    private Color coneColor;
    private Color cubeColor;
    private int detectionProximity;

    public ColorAndProximityConfig() {
        coneColor = Cat5Utils.hexToColor(Preferences.getString(ConeColorPreferencesKey, "#000000"));
        cubeColor = Cat5Utils.hexToColor(Preferences.getString(CubeColorPreferencesKey, "#000000"));
        detectionProximity = Preferences.getInt(DetectionProximityPreferencesKey, 512);

        if (!Constants.IsDebugShuffleboardEnabled) {
            return;
        }

        //#region Shuffleboard
        var layout = getLayout(Cat5ShuffleboardTab.ColorSensor, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.addString("Cone Color", () -> coneColor.toHexString());
        layout.addString("Cube Color", () -> cubeColor.toHexString());
        layout.addString("Detection Proximity", () -> Integer.toString(detectionProximity));

        layout.add(Commands.runOnce(() -> {
            coneColor = ColorSensor.get().getColor();
            Preferences.setString(ConeColorPreferencesKey, coneColor.toHexString());
        })
            .ignoringDisable(true)
            .withName("Save Cone Color")
        );

        layout.add(Commands.runOnce(() -> {
            cubeColor = ColorSensor.get().getColor();
            Preferences.setString(CubeColorPreferencesKey, cubeColor.toHexString());
        })
            .ignoringDisable(true)
            .withName("Save Cube Color")
        );

        var setDetectionProximityEntry = layout.add("Set Detection Proximity", detectionProximity).getEntry();
        layout.add(Commands.runOnce(() -> {
            detectionProximity = (int)setDetectionProximityEntry.getInteger(detectionProximity);
            Preferences.setDouble(DetectionProximityPreferencesKey, detectionProximity);
        })
            .ignoringDisable(true)
            .withName("Save Detection Proximity")
        );
        //#endregion
    }

    //#region Public
    public Color getConeColor() {
        return coneColor;
    }

    public Color getCubeColor() {
        return cubeColor;
    }

    public int getDetectionProximity() {
        return detectionProximity;
    }
    //#endregion
}
