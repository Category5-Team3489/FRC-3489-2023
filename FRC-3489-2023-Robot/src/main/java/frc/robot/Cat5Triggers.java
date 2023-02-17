package frc.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj2.command.button.Trigger;

public class Cat5Triggers {
    public static final Trigger IsTeleopEnabled = new Trigger(() -> DriverStation.isTeleopEnabled());
    public static final Trigger IsEnabled = new Trigger(() -> DriverStation.isEnabled());
}
