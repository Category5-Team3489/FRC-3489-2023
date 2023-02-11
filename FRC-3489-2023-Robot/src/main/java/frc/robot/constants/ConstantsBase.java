package frc.robot.constants;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class ConstantsBase<T extends SubsystemBase> {
    protected final T subsystem;

    public ConstantsBase(T subsystem) {
        this.subsystem = subsystem;
    }
}
