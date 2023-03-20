package frc.robot.enums;

public enum LimelightPipeline {
    Fiducial(0),
    MidRetroreflectivePipeline(1),
    HighRetroreflectivePipeline(2);

    private final long index;

    private LimelightPipeline(long index) {
        this.index = index;
    }

    public long getIndex() {
        return index;
    }
}
