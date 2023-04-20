package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import frc.robot.RobotContainer;
import frc.robot.enums.GamePiece;
import frc.robot.enums.LedPattern;

public class Leds extends Cat5Subsystem {
    // Constants
    public static final int LeftChannel = 1;
    public static final int RightChannel = 0;

    // Devices
    private final Indicator indicator;
    private final PWMSparkMax leftBlinkin = new PWMSparkMax(LeftChannel);
    private final PWMSparkMax rightBlinkin = new PWMSparkMax(RightChannel);

    public Leds(RobotContainer robotContainer, Indicator indicator) {
        super(robotContainer);
        this.indicator = indicator;
    }

    @Override
    public void periodic() {
        if (DriverStation.isDisabled()) {
            return;
        }

        LedPattern target;

        GamePiece indicated = indicator.getIndicatedGamePiece();
        if (indicated == GamePiece.Unknown) {
            switch (DriverStation.getAlliance()) {
                case Red:
                    target = LedPattern.ColorWavesLavaPalette;
                case Blue:
                    target = LedPattern.ColorWavesOceanPalette;
                default:
                    target = LedPattern.StrobeWhite;
            }
        }
        else {
            if (indicated == GamePiece.Cone) {
                target = LedPattern.Yellow;
            }
            else {
                target = LedPattern.BlueViolet;
            }
        }

        leftBlinkin.set(target.getValue());
        rightBlinkin.set(target.getValue());
    }
}
