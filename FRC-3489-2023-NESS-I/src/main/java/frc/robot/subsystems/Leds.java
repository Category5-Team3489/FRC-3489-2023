package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import frc.robot.Constants;
import frc.robot.Inputs;
import frc.robot.enums.GamePiece;
import frc.robot.enums.LedPattern;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.LedsConstants.*;

import java.util.function.Supplier;

public class Leds extends Cat5Subsystem<Leds> {
    // #region Singleton
    private static Leds instance = new Leds();

    public static Leds get() {
        return instance;
    }
    // #endregion

    // Devices
    private final PWMSparkMax leftBlinkin = new PWMSparkMax(LeftChannel);
    private final PWMSparkMax rightBlinkin = new PWMSparkMax(RightChannel);

    // Commands
    private final Command coneIndicatorCommand = getCommand(LedPattern.Yellow, Double.MAX_VALUE, true);
    private final Command cubeIndicatorCommand = getCommand(LedPattern.BlueViolet, Double.MAX_VALUE, true);

    // State
    private LedPattern leftActivePattern = LedPattern.Black;
    private LedPattern rightActivePattern = LedPattern.Black;
    private GamePiece indicatedGamePiece = GamePiece.Unknown;

    private Leds() {
        super(i -> instance = i);

        // https://www.revrobotics.com/content/docs/REV-11-1105-UM.pdf

        setDefaultCommand(getCommand(() -> {
            switch (DriverStation.getAlliance()) {
                case Red:
                    return LedPattern.ColorWavesLavaPalette;
                case Blue:
                    return LedPattern.ColorWavesOceanPalette;
                default:
                    return LedPattern.Black;
            }
        }, Double.MAX_VALUE, true));

        //#region Shuffleboard
        if (Constants.IsDebugShuffleboardEnabled) {
            var layout = getLayout(Cat5ShuffleboardTab.Leds, BuiltInLayouts.kList)
                .withSize(2, 1);

            layout.addString("Left Active Pattern", () -> leftActivePattern.toString());
            layout.addString("Right Active Pattern", () -> rightActivePattern.toString());
            layout.addString("Indicated Game Piece", () -> indicatedGamePiece.toString());
        }
        //#endregion
    }

    @Override
    public void periodic() {
        if (!DriverStation.isTeleopEnabled()) {
            indicatedGamePiece = GamePiece.Unknown;

            coneIndicatorCommand.cancel();
            cubeIndicatorCommand.cancel();
        }
        else {
            indicatedGamePiece = Inputs.getIndicatedGamePiece();

            switch (indicatedGamePiece) {
                case Cone:
                    coneIndicatorCommand.schedule();
                    break;
                case Cube:
                    cubeIndicatorCommand.schedule();
                    break;
                case Unknown:
                    coneIndicatorCommand.cancel();
                    cubeIndicatorCommand.cancel();
                    break;
            }
        }
    }

    //#region Public
    public Command getCommand(LedPattern pattern, double seconds, boolean isInterruptible) {
        return getCommand(pattern, pattern, seconds, isInterruptible);
    }

    public Command getCommand(LedPattern leftPattern, LedPattern rightPattern, double seconds, boolean isInterruptible) {
        Runnable run = () -> {
            leftActivePattern = leftPattern;
            rightActivePattern = rightPattern;

            leftBlinkin.set(leftActivePattern.getValue());
            rightBlinkin.set(rightActivePattern.getValue());
        };

        InterruptionBehavior interruptBehavior = isInterruptible ?
            InterruptionBehavior.kCancelSelf : InterruptionBehavior.kCancelIncoming;

        return run(run)
            .withTimeout(seconds)
            .withInterruptBehavior(interruptBehavior);
    }

    public Command getCommand(Supplier<LedPattern> patternSupplier, double seconds, boolean isInterruptible) {
        return getCommand(patternSupplier, patternSupplier, seconds, isInterruptible);
    }

    public Command getCommand(Supplier<LedPattern> leftPatternSupplier, Supplier<LedPattern> rightPatternSupplier, double seconds, boolean isInterruptible) {
        Runnable run = () -> {
            leftActivePattern = leftPatternSupplier.get();
            rightActivePattern = rightPatternSupplier.get();

            leftBlinkin.set(leftActivePattern.getValue());
            rightBlinkin.set(rightActivePattern.getValue());
        };

        InterruptionBehavior interruptBehavior = isInterruptible ?
            InterruptionBehavior.kCancelSelf : InterruptionBehavior.kCancelIncoming;

        return run(run)
            .withTimeout(seconds)
            .withInterruptBehavior(interruptBehavior);
    }

    public GamePiece getIndicatedGamePiece() {
        return indicatedGamePiece;
    }
    //#endregion
}