package frc.robot.shuffleboard;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.shuffleboard.ComplexWidget;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj.shuffleboard.SimpleWidget;

public class Cat5Grid {
    private final ShuffleboardLayout layout;

    private int rowIndex = 0;

    public Cat5Grid(ShuffleboardLayout layout) {
        this.layout = layout;
    }

    public SimpleWidget add(String title, Object defaultValue) {
        return layout.add(title, defaultValue)
            .withPosition(rowIndex++, 0);
    }

    public ComplexWidget add(String title, Sendable sendable) {
        return layout.add(title, sendable)
            .withPosition(rowIndex++, 0);
    }

    public void build() {

    }
}
