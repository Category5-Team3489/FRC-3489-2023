package frc.robot.subsystems;

import java.util.function.Consumer;

public class PoseEstimator extends Cat5Subsystem<PoseEstimator> {
    //#region Singleton
    private static Limelight instance = new PoseEstimator();

    public static Limelight get() {
        return instance;
    }
    //#endregion

    protected PoseEstimator() {
        super(initSingleton);
    }
    // Seconds since last vision update widget

    // April tag area, and tag count, current encoder-based robot velocity, can be used to determine trustworthiness of vision measurement
}
