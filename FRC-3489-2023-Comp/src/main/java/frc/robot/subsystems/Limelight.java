package frc.robot.subsystems;

public class Limelight extends Cat5Subsystem<Limelight> {
    //#region Singleton
    private static Limelight instance = new Limelight();

    public static Limelight get() {
        return instance;
    }
    //#endregion

    private Limelight() {
        super((i) -> instance = i);

        // https://www.andymark.com/products/limelight-2-plus
        // Field of View: 59.6 x 49.7 degrees

        // botpose	Robot transform in field-space. Translation (X,Y,Z) Rotation(Roll,Pitch,Yaw), total latency (cl+tl)

        // use getpipe before reading data from pipeline, ex: was on april tag pipeline, need to center cone nodes, set cone node pipeline, then wait until getpipe == expected
        // then start centering, you can do pursue point until pipeline gives good data
    }

    @Override
    public void initShuffleboard() {

    }
}
