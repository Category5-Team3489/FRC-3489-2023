package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;
import frc.robot.Constants;
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

    // State
    private LedPattern leftActivePattern = LedPattern.Black;
    private LedPattern rightActivePattern = LedPattern.Black;

    private Leds() {
        super(i -> instance = i);

        // https://www.revrobotics.com/content/docs/REV-11-1105-UM.pdf

        setDefaultCommand(getCommand(() -> {
            return getAlliancePattern();
        }, Double.MAX_VALUE, true));

        //#region Shuffleboard
        if (Constants.IsDebugShuffleboardEnabled) {
            var layout = getLayout(Cat5ShuffleboardTab.Leds, BuiltInLayouts.kList)
                .withSize(2, 1);

            layout.addString("Left Active Pattern", () -> leftActivePattern.toString());
            layout.addString("Right Active Pattern", () -> rightActivePattern.toString());
        }
        //#endregion
    }

    private LedPattern getAlliancePattern() {
        switch (DriverStation.getAlliance()) {
            case Red:
				return LedPattern.ColorWavesLavaPalette;
			case Blue:
				return LedPattern.ColorWavesOceanPalette;
			default:
				return LedPattern.Black;
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
    //#endregion
}