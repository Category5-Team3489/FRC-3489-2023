package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import frc.robot.Cat5;
import frc.robot.RobotContainer;
import frc.robot.data.Cat5DeltaTracker;
import frc.robot.enums.GamePiece;

public class Indicator extends Cat5Subsystem {
    private GamePiece indicatedGamePiece = GamePiece.Unknown;

    public Indicator(RobotContainer robotContainer) {
        super(robotContainer);

        new Cat5DeltaTracker<GamePiece>(robotContainer, indicatedGamePiece,
        (last) -> {
            return last != indicatedGamePiece;
        }, (last) -> {
            Cat5.print("Indicated game piece: " + last.toString() + " -> " + indicatedGamePiece.toString());
            return indicatedGamePiece;
        });
    }

    @Override
    public void periodic() {
        if (DriverStation.isTeleopEnabled()) {
            indicatedGamePiece = robotContainer.input.getIndicatedGamePiece();
        }
    }

    public GamePiece getIndicatedGamePiece() {
        return indicatedGamePiece;
    }

    public void setIndicatedGamePiece(GamePiece indicatedGamePiece) {
        if (DriverStation.isTeleopEnabled()) {
            Cat5.print("Attempted to set indicated game piece to " + indicatedGamePiece.toString() + " while in teleop");
            return;
        }

        this.indicatedGamePiece = indicatedGamePiece;
        Cat5.print("Set indicated game piece to " + indicatedGamePiece.toString());
    }
}
