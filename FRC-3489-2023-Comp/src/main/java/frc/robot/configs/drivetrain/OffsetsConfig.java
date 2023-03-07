package frc.robot.configs.drivetrain;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.configs.Cat5Config;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.subsystems.Drivetrain;

public class OffsetsConfig extends Cat5Config {
    private static final String FrontLeftOffsetRadiansPreferencesKey = "Drivetrain/FrontLeftOffsetRadians";
    private static final String FrontRightOffsetRadiansPreferencesKey = "Drivetrain/FrontRightOffsetRadians";
    private static final String BackLeftOffsetRadiansPreferencesKey = "Drivetrain/BackLeftOffsetRadians";
    private static final String BackRightOffsetRadiansPreferencesKey = "Drivetrain/BackRightOffsetRadians";

    private double frontLeftOffsetRadians = 0;
    private double frontRightOffsetRadians = 0;
    private double backLeftOffsetRadians = 0;
    private double backRightOffsetRadians = 0;

    public final DoubleSupplier getFrontLeftOffsetRadians = () -> frontLeftOffsetRadians;
    public final DoubleSupplier getFrontRightOffsetRadians = () -> frontRightOffsetRadians;
    public final DoubleSupplier getBackLeftOffsetRadians = () -> backLeftOffsetRadians;
    public final DoubleSupplier getBackRightOffsetRadians = () -> backRightOffsetRadians;

    public OffsetsConfig() {
        frontLeftOffsetRadians = Preferences.getDouble(FrontLeftOffsetRadiansPreferencesKey, 0);
        frontRightOffsetRadians = Preferences.getDouble(FrontRightOffsetRadiansPreferencesKey, 0);
        backLeftOffsetRadians = Preferences.getDouble(BackLeftOffsetRadiansPreferencesKey, 0);
        backRightOffsetRadians = Preferences.getDouble(BackRightOffsetRadiansPreferencesKey, 0);
        
        var layout = getLayout(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList);

        layout.add("Front Left", Math.toDegrees(getFrontLeftOffsetRadians.getAsDouble()));
        layout.add("Front Right", Math.toDegrees(getFrontRightOffsetRadians.getAsDouble()));
        layout.add("Back Left", Math.toDegrees(getBackLeftOffsetRadians.getAsDouble()));
        layout.add("Back Right", Math.toDegrees(getBackRightOffsetRadians.getAsDouble()));

        layout.add(Commands.run(() -> {
            frontLeftOffsetRadians = 0;

            Drivetrain.get().setFrontLeftPercentAngle(0, 0);
        })
            .ignoringDisable(true)
            .withName("Zero Front Left")
        );
        layout.add(Commands.run(() -> {
            frontRightOffsetRadians = 0;

            Drivetrain.get().setFrontRightPercentAngle(0, 0);
        })
            .ignoringDisable(true)
            .withName("Zero Front Right")
        );
        layout.add(Commands.run(() -> {
            backLeftOffsetRadians = 0;

            Drivetrain.get().setBackLeftPercentAngle(0, 0);
        })
            .ignoringDisable(true)
            .withName("Zero Back Left")
        );
        layout.add(Commands.run(() -> {
            backRightOffsetRadians = 0;

            Drivetrain.get().setBackRightPercentAngle(0, 0);
        })
            .ignoringDisable(true)
            .withName("Zero Back Right")
        );

        layout.add(Commands.runOnce(() -> {
            frontLeftOffsetRadians = Drivetrain.get().frontLeftModule.getSteerAngle();

            Preferences.setDouble(FrontLeftOffsetRadiansPreferencesKey, frontLeftOffsetRadians);

            System.out.println(frontLeftOffsetRadians);
        })
            .ignoringDisable(true)
            .withName("Save Front Left")
        );
        layout.add(Commands.runOnce(() -> {
            frontRightOffsetRadians = Drivetrain.get().frontRightModule.getSteerAngle();

            Preferences.setDouble(FrontRightOffsetRadiansPreferencesKey, frontRightOffsetRadians);

            System.out.println(frontRightOffsetRadians);
        })
            .ignoringDisable(true)
            .withName("Save Front Right")
        );
        layout.add(Commands.runOnce(() -> {
            backLeftOffsetRadians = Drivetrain.get().backLeftModule.getSteerAngle();

            Preferences.setDouble(BackLeftOffsetRadiansPreferencesKey, backLeftOffsetRadians);

            System.out.println(backLeftOffsetRadians);
        })
            .ignoringDisable(true)
            .withName("Save Back Left")
        );
        layout.add(Commands.runOnce(() -> {
            backRightOffsetRadians = Drivetrain.get().backRightModule.getSteerAngle();

            Preferences.setDouble(BackRightOffsetRadiansPreferencesKey, backRightOffsetRadians);

            System.out.println(backRightOffsetRadians);
        })
            .ignoringDisable(true)
            .withName("Save Back Right")
        );
    }
}
