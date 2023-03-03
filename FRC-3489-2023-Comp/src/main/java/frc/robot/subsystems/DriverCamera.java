package frc.robot.subsystems;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.cscore.HttpCamera;
import edu.wpi.first.cscore.UsbCamera;
import edu.wpi.first.cscore.VideoSink;
import edu.wpi.first.cscore.VideoSource.ConnectionStrategy;
import frc.robot.Constants;

public class DriverCamera {

    // public DriverCamera() {
    //     try {
    //         UsbCamera camera = CameraServer.startAutomaticCapture(0);
    //         VideoSink server = CameraServer.getServer();
    
    //         camera.setConnectionStrategy(ConnectionStrategy.kKeepOpen);
    //         camera.setResolution(Constants.CameraConstants.PixelWidth, Constants.CameraConstants.PixelHeight);
    //         camera.setFPS(Constants.CameraConstants.FPS);
    
    //         shuffleboardHandler.createCameraWidget(server.getSource());
    //         shuffleboardHandler.createLimelightCameraWidget(limelightFeed);
    //     } catch (Exception e) {
    //         System.out.println("[CameraHandler] Couldn't init cameras");
    //     }
    // }

    // @Override
    // public void teleopInit() {
    //     components.cameraServo.setAngle(Constants.CameraConstants.ServoPositions[Constants.CameraConstants.ServoStartingPositionIndex]);
    //     servoPositionIndex = Constants.CameraConstants.ServoStartingPositionIndex;
    // }

    // @Override
    // public void teleopPeriodic() {
    //     /*
    //     double throttle = components.manipulatorJoystick.getThrottle();
    //     components.cameraServo.setAngle(GeneralUtils.lerp(70, 140, (throttle + 1d) / 2d));
    //     */
    //     if(shouldSwitchCamera()){
    //         nextServoPosition();
    //     }
    // }

    // public void nextServoPosition() {
    //     if (!servoPositionIndexDirectionReversed)
    //         servoPositionIndex++;
    //     else
    //         servoPositionIndex--;
    //     if (servoPositionIndex == Constants.Camera.ServoPositions.length) {
    //         servoPositionIndexDirectionReversed = true;
    //         servoPositionIndex = Constants.Camera.ServoPositions.length - 2;
    //     }
    //     else if (servoPositionIndex == -1) {
    //         servoPositionIndexDirectionReversed = false;
    //         servoPositionIndex = 1;
    //     }
    //     components.cameraServo.setAngle(Constants.Camera.ServoPositions[servoPositionIndex]);
    // }

    // private boolean shouldSwitchCamera() {
    //     return components.rightDriveJoystick.getRawButtonPressed(Constants.Buttons.SwitchCamera) ||
    //         components.rightDriveJoystick.getRawButtonPressed(Constants.Buttons.SwitchCameraB);
    // }
    
}
