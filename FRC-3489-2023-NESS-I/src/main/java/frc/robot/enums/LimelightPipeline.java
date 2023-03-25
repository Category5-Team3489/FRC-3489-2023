package frc.robot.enums;

public enum LimelightPipeline {
    Fiducial(0),
    MidRetroreflective(1),
    HighRetroreflective(2),
    ZoomFiducial(3);

    private final long index;

    private LimelightPipeline(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }
}
