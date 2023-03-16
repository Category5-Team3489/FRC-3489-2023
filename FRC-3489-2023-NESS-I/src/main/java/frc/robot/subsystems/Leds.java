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
    private LedPattern activePattern = LedPattern.Black;

    private Leds() {
        super(i -> instance = i);

        // https://www.revrobotics.com/content/docs/REV-11-1105-UM.pdf

        setDefaultCommand(getCommand(LedPattern.Alliance, Double.MAX_VALUE, true));

        //#region Shuffleboard
        if (Constants.IsDebugShuffleboardEnabled) {
            var layout = getLayout(Cat5ShuffleboardTab.Leds, BuiltInLayouts.kList)
                .withSize(2, 1);

            layout.addString("Active Pattern", () -> activePattern.toString());
        }
        //#endregion
    }

    //#region Public
    public Command getCommand(LedPattern pattern, double seconds, boolean isInterruptible) {
        Runnable run = () -> {
            LedPattern activePattern = pattern;

            if (activePattern == LedPattern.Alliance) {
                switch (DriverStation.getAlliance()) {
                    case Red:
                        activePattern = LedPattern.ColorWavesLavaPalette;
                        break;
                    case Blue:
                        activePattern = LedPattern.ColorWavesOceanPalette;
                        break;
                    case Invalid:
                        activePattern = LedPattern.Black;
                        break;
                }
            }

            leftBlinkin.set(activePattern.getValue());
            rightBlinkin.set(activePattern.getValue());

            this.activePattern = activePattern;
        };

        InterruptionBehavior interruptBehavior = isInterruptible ?
            InterruptionBehavior.kCancelSelf : InterruptionBehavior.kCancelIncoming;

        return run(run)
            .withTimeout(seconds)
            .withInterruptBehavior(interruptBehavior);
    }
    //#endregion
}