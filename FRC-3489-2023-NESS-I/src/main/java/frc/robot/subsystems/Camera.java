package frc.robot.subsystems;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Cat5Utils;
import frc.robot.Robot;
import frc.robot.Constants.CameraConstants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class Camera extends Cat5Subsystem<Camera> {
    //#region Singleton
    private static Camera instance = new Camera();

    public static Camera get() {
        return instance;
    }
    //#endregion

    private Camera() {
        super(i -> instance = i);

        if (Robot.isSimulation()) {
            return;
        }

        try {
            UsbCamera camera = CameraServer.startAutomaticCapture(0);
            VideoSink server = CameraServer.getServer();

            camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
            camera.setResolution(CameraConstants.PixelWidth, CameraConstants.PixelHeight);
            camera.setFPS(CameraConstants.FPS);

            //#region Shuffleboard
            Cat5ShuffleboardTab.Main.get().add(server.getSource());
            //#endregion
        }
        catch (Exception e) {
            Cat5Utils.time();
            DriverStation.reportWarning("Camera not initialized", false);
        }
    }
}