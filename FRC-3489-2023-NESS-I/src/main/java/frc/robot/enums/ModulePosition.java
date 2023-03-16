package frc.robot.enums;

public enum ModulePosition {
    FrontLeft(0),
    FrontRight(1),
    BackLeft(2),
    BackRight(3);

    public final int index;

    private ModulePosition(int index) {
        this.index = index;
    }
}