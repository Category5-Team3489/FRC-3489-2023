package frc.robot.subsystems;

import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import frc.robot.Cat5;
import frc.robot.RobotContainer;
import frc.robot.data.shuffleboard.Cat5ShuffleboardTab;

public class Camera extends Cat5Subsystem {
    // Constants
    // private static final int PixelWidth = 160;
    // private static final int PixelHeight = 120;
    // private static final int FPS = 10;

    public Camera(RobotContainer robotContainer) {
        super(robotContainer);

        try {
            HttpCamera limelightFeed = new HttpCamera("limelight", "http://10.34.89.11:5800/stream.mjpg");

            Cat5ShuffleboardTab.Main.get()
                .add(limelightFeed)
                .withWidget(BuiltInWidgets.kCameraStream)
                .withSize(5, 4)
                .withPosition(2, 0);
            
            // UsbCamera camera = CameraServer.startAutomaticCapture(0);
            // VideoSink server = CameraServer.getServer();

            // // Configure camera
            // camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
            // camera.setResolution(PixelWidth, PixelHeight);
            // camera.setFPS(FPS);

            // Cat5ShuffleboardTab.Main.get().add(server.getSource())
            //     .withSize(5, 4);
        }
        catch (Exception e) {
            Cat5.warning("Limelight camera had trouble initializing", false);
        }
    }
}
