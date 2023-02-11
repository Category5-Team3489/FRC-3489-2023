package frc.robot.diagnostics;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

public abstract class Diagnostics<T extends SubsystemBase> {
    protected final T subsystem;

    public Diagnostics(T subsystem) {
        this.subsystem = subsystem;
    }
}
