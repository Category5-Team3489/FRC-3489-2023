package frc.robot.enums;

import com.revrobotics.CANSparkMax.IdleMode;

import static frc.robot.subsystems.Arm.*;

public enum ArmState {
    Home(GridPosition.Low, MinDegrees, IdleMode.kBrake),
    Homing(GridPosition.Low, MinDegrees, IdleMode.kCoast),
    DoubleSubstation(GridPosition.High, 22, IdleMode.kBrake), // 12.76
    Pickup(GridPosition.Low, -70, IdleMode.kBrake),
    LowCone(GridPosition.Low, -51.5, IdleMode.kBrake),
    LowCube(GridPosition.Low, -51.5, IdleMode.kBrake),
    MidCone(GridPosition.Mid, 20.83, IdleMode.kBrake),
    ScoreMidCone(GridPosition.Mid, -13.33, IdleMode.kBrake),
    MidCube(GridPosition.Mid, 1.17, IdleMode.kBrake),
    HighCone(GridPosition.High, 22.86, IdleMode.kBrake),
    HighCube(GridPosition.High, 21.12, IdleMode.kBrake);

    private final GridPosition gridPosition;
    private final double degrees;
    private final IdleMode idleMode;

    private ArmState(GridPosition gridPosition, double degrees, IdleMode idleMode) {
        this.gridPosition = gridPosition;
        this.degrees = degrees;
        this.idleMode = idleMode;
    }

    public GridPosition getGridPosition() {
        return gridPosition;
    }

    public double getDegrees() {
        return degrees;
    }

    public IdleMode getIdleMode() {
        return idleMode;
    }
}
