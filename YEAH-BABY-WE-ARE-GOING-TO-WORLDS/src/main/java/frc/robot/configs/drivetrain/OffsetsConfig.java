package frc.robot.configs.drivetrain;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import frc.robot.Cat5Utils;
import frc.robot.Constants;
import frc.robot.configs.Cat5Config;
import frc.robot.data.Cat5ShuffleboardTab;
import frc.robot.subsystems.Drivetrain;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class OffsetsConfig {
    // Constants
    private static final String FrontLeftOffsetRadiansPreferencesKey = "Drivetrain/FrontLeftOffsetRadians";
    private static final String FrontRightOffsetRadiansPreferencesKey = "Drivetrain/FrontRightOffsetRadians";
    private static final String BackLeftOffsetRadiansPreferencesKey = "Drivetrain/BackLeftOffsetRadians";
    private static final String BackRightOffsetRadiansPreferencesKey = "Drivetrain/BackRightOffsetRadians";

    // State
    private Drivetrain drivetrain;
    private double frontLeftOffsetRadians = 0;
    private double frontRightOffsetRadians = 0;
    private double backLeftOffsetRadians = 0;
    private double backRightOffsetRadians = 0;

    public OffsetsConfig(Drivetrain drivetrain) {
        this.drivetrain = drivetrain;

        frontLeftOffsetRadians = Preferences.getDouble(FrontLeftOffsetRadiansPreferencesKey, 0);
        frontRightOffsetRadians = Preferences.getDouble(FrontRightOffsetRadiansPreferencesKey, 0);
        backLeftOffsetRadians = Preferences.getDouble(BackLeftOffsetRadiansPreferencesKey, 0);
        backRightOffsetRadians = Preferences.getDouble(BackRightOffsetRadiansPreferencesKey, 0);

        if (!Constants.IsDebugShuffleboardEnabled) {
            return;
        }
        
        //#region Shuffleboard
        var layout = Cat5ShuffleboardTab.Drivetrain.get().getLayout(getClass().getName())
            .withSize(2, 1);

        layout.addDouble("Front Left", () -> Math.toDegrees(getFrontLeftOffsetRadians()));
        layout.addDouble("Front Right", () -> Math.toDegrees(getFrontRightOffsetRadians()));
        layout.addDouble("Back Left", () -> Math.toDegrees(getBackLeftOffsetRadians()));
        layout.addDouble("Back Right", () -> Math.toDegrees(getBackRightOffsetRadians()));

        layout.add(drivetrain.run(() -> {
            frontLeftOffsetRadians = 0;

            Drivetrain.get().setFrontLeftPercentAngle(0, 0);

            Cat5Utils.time();
            System.out.println("Zero Front Left");
        })
            .ignoringDisable(true)
            .withName("Zero Front Left")
        );
        layout.add(Drivetrain.get().run(() -> {
            frontRightOffsetRadians = 0;

            Drivetrain.get().setFrontRightPercentAngle(0, 0);

            Cat5Utils.time();
            System.out.println("Zero Front Right");
        })
            .ignoringDisable(true)
            .withName("Zero Front Right")
        );
        layout.add(Drivetrain.get().run(() -> {
            backLeftOffsetRadians = 0;

            Drivetrain.get().setBackLeftPercentAngle(0, 0);

            Cat5Utils.time();
            System.out.println("Zero Back Left");
        })
            .ignoringDisable(true)
            .withName("Zero Back Left")
        );
        layout.add(Drivetrain.get().run(() -> {
            backRightOffsetRadians = 0;

            Drivetrain.get().setBackRightPercentAngle(0, 0);

            Cat5Utils.time();
            System.out.println("Zero Back Right");
        })
            .ignoringDisable(true)
            .withName("Zero Back Right")
        );

        layout.add(runOnce(() -> {
            frontLeftOffsetRadians = Drivetrain.get().frontLeftModule.getSteerAngle();

            Preferences.setDouble(FrontLeftOffsetRadiansPreferencesKey, frontLeftOffsetRadians);

            Cat5Utils.time();
            System.out.println("Save Front Left (rad): " + frontLeftOffsetRadians);
        })
            .ignoringDisable(true)
            .withName("Save Front Left")
        );
        layout.add(runOnce(() -> {
            frontRightOffsetRadians = Drivetrain.get().frontRightModule.getSteerAngle();

            Preferences.setDouble(FrontRightOffsetRadiansPreferencesKey, frontRightOffsetRadians);

            Cat5Utils.time();
            System.out.println("Save Front Right (rad): " + frontRightOffsetRadians);
        })
            .ignoringDisable(true)
            .withName("Save Front Right")
        );
        layout.add(runOnce(() -> {
            backLeftOffsetRadians = Drivetrain.get().backLeftModule.getSteerAngle();

            Preferences.setDouble(BackLeftOffsetRadiansPreferencesKey, backLeftOffsetRadians);

            Cat5Utils.time();
            System.out.println("Save Back Left (rad): " + backLeftOffsetRadians);
        })
            .ignoringDisable(true)
            .withName("Save Back Left")
        );
        layout.add(runOnce(() -> {
            backRightOffsetRadians = Drivetrain.get().backRightModule.getSteerAngle();

            Preferences.setDouble(BackRightOffsetRadiansPreferencesKey, backRightOffsetRadians);

            Cat5Utils.time();
            System.out.println("Save Back Right (rad): " + backRightOffsetRadians);
        })
            .ignoringDisable(true)
            .withName("Save Back Right")
        );
        //#endregion
    }

    //#region Public
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
    //#endregion
}
