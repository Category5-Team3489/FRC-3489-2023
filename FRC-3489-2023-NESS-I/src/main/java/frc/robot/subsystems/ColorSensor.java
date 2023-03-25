package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.util.Color;
import frc.robot.Constants;
import frc.robot.enums.GamePiece;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class ColorSensor extends Cat5Subsystem<ColorSensor> {
    // #region Singleton
    private static ColorSensor instance = new ColorSensor();

    public static ColorSensor get() {
        return instance;
    }
    // #endregion

    // Configs
    // private final ColorAndProximityConfig colorAndProximityConfig = new ColorAndProximityConfig();

    // Devices
    // private final ColorSensorV3 colorSensor = new ColorSensorV3(I2C.Port.kMXP);

    // State
    private Color color = new Color(0, 0, 0);
    private int proximity = 0; // 0 to 2047
    private GamePiece detectedGamePiece = GamePiece.Unknown;
    private final Timer warningTimer = new Timer();

    private ColorSensor() {
        super(i -> instance = i);

        // colorSensor.configureColorSensor(ColorSensorResolution.kColorSensorRes20bit, ColorSensorMeasurementRate.kColorRate200ms, GainFactor.kGain1x);

        warningTimer.restart();

        // #region Shuffleboard
        if (Constants.IsDebugShuffleboardEnabled) {
            var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
                .withSize(2, 1);

            layout.addDouble("Red", () -> color.red);
            layout.addDouble("Green", () -> color.green);
            layout.addDouble("Blue", () -> color.blue);
            layout.addString("Proximity", () -> Integer.toString(proximity));
            layout.addString("Detected Game Piece", () -> detectedGamePiece.toString());

            layout.addBoolean("LimitSwitch", () -> oof.get());
        }
        // #endregion
    }

    // @Override
    // public void periodic() {
    //     if (Robot.isSimulation()) {
    //         return;
    //     }

    //     color = colorSensor.getColor();
    //     proximity = colorSensor.getProximity();

    //     if (proximity < colorAndProximityConfig.getDetectionProximity()) {
    //         detectedGamePiece = GamePiece.Unknown;
    //     }
    //     else {
    //         Translation3d current = new Translation3d(color.red, color.green, color.blue);
    //         Color coneColor = colorAndProximityConfig.getConeColor();
    //         Color cubeColor = colorAndProximityConfig.getCubeColor();
    //         double coneConfidence = current.getDistance(new Translation3d(coneColor.red, coneColor.green, coneColor.blue));
    //         double cubeConfidence = current.getDistance(new Translation3d(cubeColor.red, cubeColor.green, cubeColor.blue));
            
    //         if (coneConfidence <= cubeConfidence) {
    //             detectedGamePiece = GamePiece.Cone;
    //         }
    //         else {
    //             detectedGamePiece = GamePiece.Cube;
    //         }
    //     }

    //     if (warningTimer.hasElapsed(WarningIntervalSeconds)) {
    //         warningTimer.restart();

    //         if (!colorSensor.isConnected()) {
    //             Cat5Utils.time();
    //             DriverStation.reportWarning("Color sensor not connected", false);
    //             Leds.get().getCommand(LedPattern.Aqua, LedPattern.StrobeRed, 0.5, true)
    //                 .schedule();
    //         }
    //         else if (colorSensor.hasReset()) {
    //             Cat5Utils.time();
    //             DriverStation.reportWarning("Color sensor has reset", false);
    //             Leds.get().getCommand(LedPattern.DarkBlue, LedPattern.StrobeRed, 0.5, true)
    //                 .schedule();
    //         }
    //         else if (color.red == 0 && color.green == 0 && color.blue == 0 && proximity == 0) {
    //             Cat5Utils.time();
    //             DriverStation.reportWarning("Color sensor values are zero", false);
    //             Leds.get().getCommand(LedPattern.BlueGreen, LedPattern.StrobeRed, 0.5, true)
    //                 .schedule();
    //         }
    //     }
    // }

    // #region Public
    public Color getColor() {
        return color;
    }

    DigitalInput oof = new DigitalInput(2);

    public int getProximity() {
        if (!oof.get()) {
            return 2047;
        }
        return 0;
        // return proximity;
    }

    public GamePiece getDetectedGamePiece() {
        // return detectedGamePiece;
        if (oof.get()) { // color sensor inverted, not triggered here
            return GamePiece.Unknown;
        }

        GamePiece piece = Leds.get().getIndicatedGamePiece();

        if (piece == GamePiece.Unknown) {
            piece = GamePiece.Cone;
        }

        return piece;
    }
    // #endregion
}