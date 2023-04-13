package frc.robot.subsystems;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import frc.robot.Cat5;
import frc.robot.Robot;
import frc.robot.RobotContainer;
import frc.robot.data.shuffleboard.Cat5ShuffleboardTab;

public class Camera extends Cat5Subsystem {
    // Constants
    private static final int PixelWidth = 160;
    private static final int PixelHeight = 120;
    private static final int FPS = 10;

    public Camera(RobotContainer robotContainer) {
        super(robotContainer);

        if (Robot.isSimulation()) {
            return;
        }

        try {
            UsbCamera camera = CameraServer.startAutomaticCapture(0);
            VideoSink server = CameraServer.getServer();

            // Configure camera
            camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
            camera.setResolution(PixelWidth, PixelHeight);
            camera.setFPS(FPS);

            Cat5ShuffleboardTab.Main.get().add(server.getSource())
                .withSize(5, 4);
        }
        catch (Exception e) {
            Cat5.warning("Camera had trouble initializing", false);
        }
    }
}
