package frc.robot.subsystems;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.ColorSensorConstants.*;

public class ColorSensor extends Cat5Subsystem<ColorSensor> {
    //#region Singleton
    private static ColorSensor instance = new ColorSensor();

    public static ColorSensor get() {
        return instance;
    }
    //#endregion

    // Devices
    private final I2C.Port i2cPort = I2C.Port.kMXP;
    private final ColorSensorV3 colorSensor = new ColorSensorV3(i2cPort);

    // State
    private State state = State.Nothing;
    private Color color = Color.kBlack;
    private int proximity = 0; // 0 to 2047

    private ColorSensor() {
        super((i) -> instance = i);

        // TODO DSAHHHHHHHH
        // https://docs.wpilib.org/en/stable/docs/yearly-overview/known-issues.html#onboard-i2c-causing-system-lockups
    }

    @Override
    public void initShuffleboard() {
        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.addDouble("Red", () -> color.red);
        layout.addDouble("Green", () -> color.green);
        layout.addDouble("Blue", () -> color.blue);
        layout.addInteger("Proximity", () -> proximity);
        layout.addString("State", () -> state.toString());
    }

    @Override
    public void periodic() {
        color = colorSensor.getColor();
        proximity = colorSensor.getProximity();

        if (proximity < DetectionProximity) {
            state = State.Nothing;
        }
        else {
            Translation3d current = new Translation3d(color.red, color.green, color.blue);
            double cone = current.getDistance(ConeColor);
            double cube = current.getDistance(CubeColor);
            if (cone <= cube) {
                state = State.Cone;
            }
            else {
                state = State.Cube; 
            }
        }

        state = State.Cube;

        // System.out.println(state.toString());
    }

    public State getState() {
        return state;
    }

    public enum State {
        Cone,
        Cube,
        Nothing
    }
}
