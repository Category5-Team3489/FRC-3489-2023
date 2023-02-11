package frc.robot.shuffleboard;

import java.util.Map;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

public class Cat5Grid {
    private final ShuffleboardLayout layout;
    private final int width;
    private final int height;

    private int rowIndex = 0;

    public Cat5Grid(ShuffleboardTab tab, String title, int width, int height) {
        layout = tab.getLayout(title, BuiltInLayouts.kGrid);
        this.width = width;
        this.height = height;
    }

    public SimpleWidget add(String title, Object defaultValue) {
        return layout.add(title, defaultValue)
            .withPosition(0, rowIndex++);
    }

    public ComplexWidget add(String title, Sendable sendable) {
        return layout.add(title, sendable)
            .withPosition(0, rowIndex++);
    }

    public void build() {
        layout.withProperties(Map.of("Number of columns", 1, "Number of rows", rowIndex));
        layout.withSize(width, height);
    }
}
