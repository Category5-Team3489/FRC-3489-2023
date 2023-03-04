package frc.robot.subsystems;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import frc.robot.Constants.CameraConstants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class DriverCamera extends Cat5Subsystem<DriverCamera> {
    private static DriverCamera instance = new DriverCamera();

    public static DriverCamera get() {
        return instance;
    }

    public DriverCamera() {
        super((i) -> instance = i);

        // var layout = getLayout(Cat5ShuffleboardTab.DriveCamera, BuiltInLayouts.kList)
        // .withSize(2, 3);
        
        try {
            UsbCamera camera = CameraServer.startAutomaticCapture(0);
            // VideoSink server = CameraServer.getServer();
    
            camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
            camera.setResolution(CameraConstants.PixelWidth, CameraConstants.PixelHeight);
            camera.setFPS(CameraConstants.FPS);
    
            // layout
            // .add(server.getSource())
            // .withWidget(BuiltInWidgets.kCameraStream)
            // .withSize(4, 4);

        } catch (Exception e) {
            System.out.println("[CameraHandler] Couldn't init cameras");
        }

         
    }
    
}
