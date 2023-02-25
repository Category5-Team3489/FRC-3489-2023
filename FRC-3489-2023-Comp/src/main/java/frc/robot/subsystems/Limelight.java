package frc.robot.subsystems;

import frc.robot.Cat5Subsystem;

public class Limelight extends Cat5Subsystem<Limelight> {
    //#region Singleton
    private static Limelight instance;

    public static Limelight get() {
        if (instance == null) {
            instance = new Limelight();
        }

        return instance;
    }
    //#endregion

    public Limelight() {
        super(null);

        // https://www.andymark.com/products/limelight-2-plus
        // Field of View: 59.6 x 49.7 degrees

        // botpose	Robot transform in field-space. Translation (X,Y,Z) Rotation(Roll,Pitch,Yaw), total latency (cl+tl)

        // use getpipe before reading data from pipeline, ex: was on april tag pipeline, need to center cone nodes, set cone node pipeline, then wait until getpipe == expected
        // then start centering, you can do pursue point until pipeline gives good data
    }

    @Override
    protected void initShuffleboard() {

    }
}
