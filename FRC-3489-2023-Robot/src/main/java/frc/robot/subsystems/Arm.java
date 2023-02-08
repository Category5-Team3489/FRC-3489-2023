package frc.robot.subsystems;

import java.util.Map;
import java.util.function.DoubleSupplier;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxPIDController;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.BooleanEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.FunctionalCommand;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RepeatCommand;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Cat5Shuffleboard;
import frc.robot.Constants;
import frc.robot.Constants.ArmConstants;

public class Arm extends SubsystemBase {
    // Devices
    private final CANSparkMax motor = new CANSparkMax(ArmConstants.MotorDeviceId, MotorType.kBrushless);
    private final DigitalInput limitSwitch = new DigitalInput(ArmConstants.LimitSwitchChannel);
    private final SparkMaxPIDController pidController;
    private final RelativeEncoder encoder;

    private final DoubleSupplier pGain, iGain, dGain, iZone, ffGain, minOutput, maxOutput;

    private boolean isHomed = false;

    public Arm() {
        register();

        motor.restoreFactoryDefaults();
        motor.burnFlash();

        pidController = motor.getPIDController();
        encoder = motor.getEncoder();

        // Shuffleboard
        ShuffleboardLayout mainLayout = Cat5Shuffleboard.createMainLayout("Arm")
            .withSize(2, 1);
        mainLayout.addBoolean("Is Homed", () -> isHomed);
        mainLayout.addDouble("Set Motor Speed", () -> motor.get());

        var testLayout = Cat5Shuffleboard.getMainTab()
            .getLayout("test", BuiltInLayouts.kGrid)
            .withProperties(Map.of("Number of columns", 1, "Number of rows", 3));

            // TODO THIS GRID STUFF WORKS
        testLayout.add("a", "aaa")
            .withPosition(0, 3);
            testLayout.add("c", "ccc")
            .withPosition(0, 1);
        testLayout.add("b", "bbb")
            .withPosition(0, 2);
        

        var pGainEntry = mainLayout.add("P Gain", 0).getEntry();
        var iGainEntry = mainLayout.add("I Gain", 0).getEntry();
        var dGainEntry = mainLayout.add("D Gain", 0).getEntry();
        var iZoneEntry = mainLayout.add("I Zone", 0).getEntry();
        var ffGainEntry = mainLayout.add("FF Gain", 0).getEntry();
        var minOutputEntry = mainLayout.add("Min Output", 0).getEntry();
        var maxOutputEntry = mainLayout.add("Max Output", 0).getEntry();
        pGain = () -> pGainEntry.getDouble(0.0);
        iGain = () -> iGainEntry.getDouble(0.0);
        dGain = () -> dGainEntry.getDouble(0.0);
        iZone = () -> iZoneEntry.getDouble(0.0);
        ffGain = () -> ffGainEntry.getDouble(0.0);
        minOutput = () -> minOutputEntry.getDouble(0.0);
        maxOutput = () -> maxOutputEntry.getDouble(0.0);

        // FunctionalCommand e = new FunctionalCommand(null, null, null, null, null)

        mainLayout.add("Update Constants", new RunCommand(() -> {
            pidController.getD();
        }));
        mainLayout.addDouble("Test", pGain);

//         int rows = 5; // or however many you want
// var list =
//   Shuffleboard
//       .getTab("Example Tab")
//       .getLayout("Fake List", BuiltInLayouts.kGrid)
//       .withProperties(Map.of("Number of columns", 1, "Number of rows", rows));

// list.addBoolean("2 Always False", () -> false)
//     .withWidget(BuiltInWidgets.kBooleanBox)
//     .withPosition(0, 0); // place the first widget in row 0
// list.addBoolean("1 Always True", () -> true)
//     .withWidget(BuiltInWidgets.kBooleanBox)
//     .withPosition(0, 1); // place the second widget in row 1
    }

    @Override
    public void periodic() {

    }

    public void gotoHome() {

    }
    public void gotoPosition(double position) {

    }

    private void setMotorPosition(double position) {

    }
}
