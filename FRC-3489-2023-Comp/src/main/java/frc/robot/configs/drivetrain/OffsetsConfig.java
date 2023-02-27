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
        load();
        
        var layout = getLayout(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList);

        var frontLeftEntry = layout.add("Front Left", Math.toDegrees(getFrontLeftOffsetRadians.getAsDouble())).getEntry();
        var frontRightEntry = layout.add("Front Right", Math.toDegrees(getFrontRightOffsetRadians.getAsDouble())).getEntry();
        var backLeftEntry = layout.add("Back Left", Math.toDegrees(getBackLeftOffsetRadians.getAsDouble())).getEntry();
        var backRightEntry = layout.add("Back Right", Math.toDegrees(getBackRightOffsetRadians.getAsDouble())).getEntry();

        layout.add(Commands.run(() -> {
            frontLeftOffsetRadians = 0;
            frontRightOffsetRadians = 0;
            backLeftOffsetRadians = 0;
            backRightOffsetRadians = 0;

            Drivetrain.get().setFrontLeftPercentAngle(0, 0);
            Drivetrain.get().setFrontRightPercentAngle(0, 0);
            Drivetrain.get().setBackLeftPercentAngle(0, 0);
            Drivetrain.get().setBackRightPercentAngle(0, 0);
        })
            .withName("Configure")
        );

        layout.add(Commands.runOnce(() -> {
            load();

            frontLeftEntry.setDouble(Math.toDegrees(frontLeftOffsetRadians));
            frontRightEntry.setDouble(Math.toDegrees(frontRightOffsetRadians));
            backLeftEntry.setDouble(Math.toDegrees(backLeftOffsetRadians));
            backRightEntry.setDouble(Math.toDegrees(backRightOffsetRadians));
        })
            .ignoringDisable(true)
            .withName("Load")
        );

        layout.add(Commands.runOnce(() -> {
            frontLeftOffsetRadians = Math.toRadians(frontLeftEntry.getDouble(0));
            frontRightOffsetRadians = Math.toRadians(frontRightEntry.getDouble(0));
            backLeftOffsetRadians = Math.toRadians(backLeftEntry.getDouble(0));
            backRightOffsetRadians = Math.toRadians(backRightEntry.getDouble(0));

            save();
        })
            .ignoringDisable(true)
            .withName("Save Shuffleboard")
        );

        layout.add(Commands.runOnce(() -> {
            // TODO SAVE from robot, make sure to update shuffleboard values too
            frontLeftOffsetRadians = Math.toRadians(frontLeftEntry.getDouble(0));
            frontRightOffsetRadians = Math.toRadians(frontRightEntry.getDouble(0));
            backLeftOffsetRadians = Math.toRadians(backLeftEntry.getDouble(0));
            backRightOffsetRadians = Math.toRadians(backRightEntry.getDouble(0));

            save();
        })
            .ignoringDisable(true)
            .withName("Save Robot")
        );
    }

    private void load() {
        frontLeftOffsetRadians = Preferences.getDouble(FrontLeftOffsetRadiansPreferencesKey, 0);
        frontRightOffsetRadians = Preferences.getDouble(FrontRightOffsetRadiansPreferencesKey, 0);
        backLeftOffsetRadians = Preferences.getDouble(BackLeftOffsetRadiansPreferencesKey, 0);
        backRightOffsetRadians = Preferences.getDouble(BackRightOffsetRadiansPreferencesKey, 0);
    }

    private void save() {
        Preferences.setDouble(FrontLeftOffsetRadiansPreferencesKey, frontLeftOffsetRadians);
        Preferences.setDouble(FrontRightOffsetRadiansPreferencesKey, frontRightOffsetRadians);
        Preferences.setDouble(BackLeftOffsetRadiansPreferencesKey, backLeftOffsetRadians);
        Preferences.setDouble(BackRightOffsetRadiansPreferencesKey, backRightOffsetRadians);
    }
}
