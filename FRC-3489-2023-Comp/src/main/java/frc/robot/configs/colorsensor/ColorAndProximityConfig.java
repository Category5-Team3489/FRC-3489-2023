package frc.robot.configs.colorsensor;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Commands;

import frc.robot.Cat5Utils;
import frc.robot.configs.Cat5Config;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.subsystems.ColorSensor;

public class ColorAndProximityConfig extends Cat5Config {
    private static final String ConeColorPreferencesKey = "ColorSensor/ConeColor";
    private static final String CubeColorPreferencesKey = "ColorSensor/ConeColor";
    private static final String DetectionProximityPreferencesKey = "ColorSensor/DetectionProximity";

    private Color coneColor;
    private Color cubeColor;
    private int detectionProximity;

    public final Supplier<Color> getConeColor = () -> coneColor;
    public final Supplier<Color> getCubeColor = () -> cubeColor;
    public final Supplier<Integer> getDetectionProximity = () -> detectionProximity;

    public ColorAndProximityConfig() {
        load();

        var layout = getLayout(Cat5ShuffleboardTab.ColorSensor, BuiltInLayouts.kList);

        layout.addString("Cone Color", () -> coneColor.toHexString());
        layout.addString("Cube Color", () -> cubeColor.toHexString());
        layout.addString("Detection Proximity", () -> Integer.toString(detectionProximity));

        layout.add(Commands.runOnce(() -> {
            coneColor = ColorSensor.get().getColor();
            Preferences.setString(ConeColorPreferencesKey, coneColor.toHexString());
        })
            .withName("Save Cone Color")
            .ignoringDisable(true)
        );

        layout.add(Commands.runOnce(() -> {
            cubeColor = ColorSensor.get().getColor();
            Preferences.setString(CubeColorPreferencesKey, cubeColor.toHexString());
        })
            .withName("Save Cube Color")
            .ignoringDisable(true)
        );

        var setDetectionProximityEntry = layout.add("Set Detection Proximity", detectionProximity).getEntry();
        layout.add(Commands.runOnce(() -> {
            detectionProximity = (int)setDetectionProximityEntry.getInteger(detectionProximity);
            Preferences.setDouble(DetectionProximityPreferencesKey, detectionProximity);
        })
            .withName("Save Detection Proximity")
            .ignoringDisable(true)
        );
    }

    private void load() {
        coneColor = Cat5Utils.hex2Rgb(Preferences.getString(ConeColorPreferencesKey, "#000000"));
        cubeColor = Cat5Utils.hex2Rgb(Preferences.getString(CubeColorPreferencesKey, "#000000"));
        detectionProximity = Preferences.getInt(DetectionProximityPreferencesKey, 512);
    }
}
