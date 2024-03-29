package frc.robot.subsystems;

import com.revrobotics.ColorSensorV3;

import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.util.Color;

import frc.robot.enums.GamePiece;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;
import frc.robot.Constants.OperatorConstants;
import frc.robot.configs.colorsensor.ColorAndProximityConfig;
import frc.robot.subsystems.Leds.LedState;

import static frc.robot.Constants.ColorSensorConstants.*;

public class ColorSensor extends Cat5Subsystem<ColorSensor> {
    // #region Singleton
    private static ColorSensor instance = new ColorSensor();

    public static ColorSensor get() {
        return instance;
    }
    // #endregion

    // Configs
    public final ColorAndProximityConfig colorAndProximityConfig = new ColorAndProximityConfig();

    // Devices
    private ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kMXP);

    // State
    private Color color = new Color(0, 0, 0);
    private int proximity = 0;
    private GamePiece detectedGamePiece = GamePiece.Unknown;
    private Timer errorPrintTimer = new Timer();

    private ColorSensor() {
        super((i) -> instance = i);

        // proximity: 0 to 2047
        // https://docs.wpilib.org/en/stable/docs/yearly-overview/known-issues.html#onboard-i2c-causing-system-lockups

        errorPrintTimer.restart();

        // #region Shuffleboard
        if (OperatorConstants.DebugShuffleboard) {
            var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
                    .withSize(2, 3);

            layout.addDouble("Red", () -> color.red);
            layout.addDouble("Green", () -> color.green);
            layout.addDouble("Blue", () -> color.blue);
            layout.addString("Proximity", () -> Integer.toString(proximity));
            layout.addString("Detected Game Piece", () -> detectedGamePiece.toString());
        }
        // #endregion
    }

    @Override
    public void periodic() {
        color = colorSensor.getColor();
        proximity = colorSensor.getProximity();

        if (proximity < colorAndProximityConfig.getDetectionProximity.get()) {
            detectedGamePiece = GamePiece.Unknown;
        } else {
            Translation3d current = new Translation3d(color.red, color.green, color.blue);
            Color coneColor = colorAndProximityConfig.getConeColor.get();
            Color cubeColor = colorAndProximityConfig.getCubeColor.get();
            double cone = current.getDistance(new Translation3d(coneColor.red, coneColor.green, coneColor.blue));
            double cube = current.getDistance(new Translation3d(cubeColor.red, cubeColor.green, cubeColor.blue));
            if (cone <= cube) {
                detectedGamePiece = GamePiece.Cone;
            } else {
                detectedGamePiece = GamePiece.Cube;
            }
        }

        if (errorPrintTimer.hasElapsed(ErrorPrintIntervalSeconds)) {
            errorPrintTimer.restart();

            if (colorSensor.hasReset()) {
                System.out.println("Color sensor has reset");
                Leds.get().setLeds(LedState.ErrorPattern);
            }

            if (!colorSensor.isConnected()) {
                System.out.println("Color sensor is not connected, reboot to fix");
                Leds.get().setLeds(LedState.ErrorPattern);
            }

            if (proximity == 0 && color.red == 0 && color.green == 0 && color.blue == 0)
            {
                System.out.println("Color sensor is connected, but values are zero");
                Leds.get().setLeds(LedState.ErrorPattern);
            }
        }
    }

    // #region Public
    public Color getColor() {
        return color;
    }

    public int getProximity() {
        return proximity;
    }

    public GamePiece getDetectedGamePiece() {
        return detectedGamePiece;
    }
    // #endregion
}
