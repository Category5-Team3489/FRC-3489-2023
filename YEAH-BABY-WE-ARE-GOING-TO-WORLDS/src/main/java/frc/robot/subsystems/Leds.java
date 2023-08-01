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
        //if robot == disabled: dont set leds
        if (DriverStation.isDisabled()) {
            return;
        }

        //target = color from ledPattern enum
        LedPattern target;

        //change led colors based on the game piece needed (controlled by manipulator)
        GamePiece indicated = indicator.getIndicatedGamePiece();
        // if the game piece is not selected by manipulator:
        if (indicated == GamePiece.Unknown) {
            //set leds to green by default; if there is an alliance, leds = alliance color
            switch (DriverStation.getAlliance()) {
                case Red:
                    target = LedPattern.ColorWavesLavaPalette;
                case Blue:
                    target = LedPattern.ColorWavesOceanPalette;
                default:
                    target = LedPattern.Green;
            }
        }
        //if there is a game piece selected by manipulator, leds = piece color
        else {
            if (indicated == GamePiece.Cone) {
                target = LedPattern.Yellow;
            }
            else {
                target = LedPattern.BlueViolet;
            }
        }

        //set leds to selected color enum value; if ColorWavesLavaPalette(-0.39): Blinkin.set(-0.39)
        leftBlinkin.set(target.getValue()); 
        rightBlinkin.set(target.getValue());
    }
}
