package frc.robot.configs.drivetrain;

import edu.wpi.first.wpilibj.Preferences;
import frc.robot.Cat5;
import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;
import frc.robot.subsystems.Drivetrain;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class OffsetsConfig {
    // Constants
    private static final String FrontLeftOffsetRadiansPreferencesKey = "Drivetrain/FrontLeftOffsetRadians";
    private static final String FrontRightOffsetRadiansPreferencesKey = "Drivetrain/FrontRightOffsetRadians";
    private static final String BackLeftOffsetRadiansPreferencesKey = "Drivetrain/BackLeftOffsetRadians";
    private static final String BackRightOffsetRadiansPreferencesKey = "Drivetrain/BackRightOffsetRadians";

    // State
    private double frontLeftOffsetRadians = 0;
    private double frontRightOffsetRadians = 0;
    private double backLeftOffsetRadians = 0;
    private double backRightOffsetRadians = 0;

    public OffsetsConfig(RobotContainer robotContainer, Drivetrain drivetrain) {
        frontLeftOffsetRadians = Preferences.getDouble(FrontLeftOffsetRadiansPreferencesKey, 0);
        frontRightOffsetRadians = Preferences.getDouble(FrontRightOffsetRadiansPreferencesKey, 0);
        backLeftOffsetRadians = Preferences.getDouble(BackLeftOffsetRadiansPreferencesKey, 0);
        backRightOffsetRadians = Preferences.getDouble(BackRightOffsetRadiansPreferencesKey, 0);

        if (!Constants.IsDebugShuffleboardEnabled) {
            return;
        }
        
        var layout = robotContainer.layouts.get(Cat5ShuffleboardLayout.Offsets_Config)
            .withSize(2, 4);

        // layout.addDouble("Front Left", () -> Math.toDegrees(getFrontLeftOffsetRadians()));
        // layout.addDouble("Front Right", () -> Math.toDegrees(getFrontRightOffsetRadians()));
        // layout.addDouble("Back Left", () -> Math.toDegrees(getBackLeftOffsetRadians()));
        // layout.addDouble("Back Right", () -> Math.toDegrees(getBackRightOffsetRadians()));

        layout.add(drivetrain.run(() -> {
            frontLeftOffsetRadians = 0;

            drivetrain.setFrontLeftPercentAngle(0, 0);

            Cat5.print("Zeroed Front Left");
        })
            .ignoringDisable(true)
            .withName("Zero Front Left")
        );
        layout.add(drivetrain.run(() -> {
            frontRightOffsetRadians = 0;

            drivetrain.setFrontRightPercentAngle(0, 0);

            Cat5.print("Zeroed Front Right");
        })
            .ignoringDisable(true)
            .withName("Zero Front Right")
        );
        layout.add(drivetrain.run(() -> {
            backLeftOffsetRadians = 0;

            drivetrain.setBackLeftPercentAngle(0, 0);

            Cat5.print("Zeroed Back Left");
        })
            .ignoringDisable(true)
            .withName("Zero Back Left")
        );
        layout.add(drivetrain.run(() -> {
            backRightOffsetRadians = 0;

            drivetrain.setBackRightPercentAngle(0, 0);

            Cat5.print("Zeroed Back Right");
        })
            .ignoringDisable(true)
            .withName("Zero Back Right")
        );

        layout.add(runOnce(() -> {
            frontLeftOffsetRadians = drivetrain.frontLeftModule.getSteerAngle();

            Preferences.setDouble(FrontLeftOffsetRadiansPreferencesKey, frontLeftOffsetRadians);

            Cat5.print("Saved Front Left (deg): " + Math.toDegrees(frontLeftOffsetRadians));
        })
            .ignoringDisable(true)
            .withName("Save Front Left")
        );
        layout.add(runOnce(() -> {
            frontRightOffsetRadians = drivetrain.frontRightModule.getSteerAngle();

            Preferences.setDouble(FrontRightOffsetRadiansPreferencesKey, frontRightOffsetRadians);

            Cat5.print("Saved Front Right (deg): " + Math.toDegrees(frontRightOffsetRadians));
        })
            .ignoringDisable(true)
            .withName("Save Front Right")
        );
        layout.add(runOnce(() -> {
            backLeftOffsetRadians = drivetrain.backLeftModule.getSteerAngle();

            Preferences.setDouble(BackLeftOffsetRadiansPreferencesKey, backLeftOffsetRadians);

            Cat5.print("Saved Back Left (deg): " + Math.toDegrees(backLeftOffsetRadians));
        })
            .ignoringDisable(true)
            .withName("Save Back Left")
        );
        layout.add(runOnce(() -> {
            backRightOffsetRadians = drivetrain.backRightModule.getSteerAngle();

            Preferences.setDouble(BackRightOffsetRadiansPreferencesKey, backRightOffsetRadians);

            Cat5.print("Saved Back Right (deg): " + Math.toDegrees(backRightOffsetRadians));
        })
            .ignoringDisable(true)
            .withName("Save Back Right")
        );
    }

    public double getFrontLeftOffsetRadians() {
        return frontLeftOffsetRadians;
    }
    public double getFrontRightOffsetRadians() {
        return frontRightOffsetRadians;
    }
    public double getBackLeftOffsetRadians() {
        return backLeftOffsetRadians;
    }
    public double getBackRightOffsetRadians() {
        return backRightOffsetRadians;
    }
}
