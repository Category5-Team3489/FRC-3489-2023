package frc.robot.commands.leds;

import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Leds;

public abstract class LedsCommandBase extends CommandBase {
    protected final Leds leds;

    public LedsCommandBase(Leds leds) {
        this.leds = leds;
        addRequirements(leds);
    }

    public abstract boolean isOverridable();
}
