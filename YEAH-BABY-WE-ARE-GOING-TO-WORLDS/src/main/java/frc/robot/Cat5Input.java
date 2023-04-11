package frc.robot;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.enums.GamePiece;

public class Cat5Input {
    // Devices
    private final CommandXboxController xbox = new CommandXboxController(0);
    private final CommandJoystick man = new CommandJoystick(1);

    // Triggers
    public final Trigger automate = man.button(5)
        .debounce(0.1, DebounceType.kBoth);
    public final Trigger gripperStop = man.button(1);
    public final Trigger gripperIntake = man.button(2);
    public final Trigger gripperOuttake = man.button(3);
    public final Trigger wristLow = man.button(4);
    public final Trigger wristHigh = man.button(6);
    public final Trigger armDoubleSubstation = man.button(8);
    public final Trigger armHome = man.button(10);
    public final Trigger armPickup = man.button(12);
    public final Trigger armLow = man.button(11);
    public final Trigger armMid = man.button(9)
        .debounce(0.1, DebounceType.kBoth);
    public final Trigger armHigh = man.button(7);
    public final Trigger navxZeroYaw = xbox.start();
    public final Trigger drivetrainNorth = xbox.y();
    public final Trigger drivetrainEast = xbox.b();
    public final Trigger drivetrainSouth = xbox.a();
    public final Trigger drivetrainWest = xbox.x();
    
    // Methods
    public double getDriveXPercent() {
        return Cat5.quadraticAxis(-xbox.getLeftY(), 0.05);
    }

    public double getDriveYPercent() {
        return Cat5.quadraticAxis(-xbox.getLeftX(), 0.05);
    }

    public double getDriveOmegaPercent() {
        return Cat5.quadraticAxis(-xbox.getRightX(), 0.05);
    }

    public boolean isBeingDriven() {
        return Math.abs(xbox.getLeftY()) > 0.1 ||
            Math.abs(xbox.getLeftX()) > 0.1 ||
            Math.abs(xbox.getRightX()) > 0.1;
    }

    public double getDriveSpeedLimiterPercent() {
        double speedLimiter = 1.0 / 2.0;

        if (xbox.leftBumper().getAsBoolean()) {
            speedLimiter = 1.0 / 3.0;
        }
        else if (xbox.rightBumper().getAsBoolean()) {
            speedLimiter = 1.0;
        }

        return speedLimiter;
    }

    public int getDrivePovAngleDegrees() {
        return xbox.getHID().getPOV();
    }

    public boolean shouldAutomateLeftDoubleSubstation() {
        return xbox.getLeftTriggerAxis() > 0.25;
    }

    public boolean shouldAutomateRightDoubleSubstation() {
        return xbox.getRightTriggerAxis() > 0.25;
    }

    public double getArmCorrectionPercent() {
        return Cat5.linearAxis(-man.getY(), 0.5);
    }

    public double getWristCorrectionPercent() {
        int pov = man.getHID().getPOV();

        if (pov == 315 || pov == 0 || pov == 45) {
            return 1.0;
        }

        if (pov == 135 || pov == 180 || pov == 225) {
            return -1.0;
        }

        return 0;
    }

    public GamePiece getIndicatedGamePiece() {
        double value = man.getThrottle();
        if (value > 0.8) {
            return GamePiece.Cone;
        }
        else if (value < -0.8) {
            return GamePiece.Cube;
        }
        return GamePiece.Unknown;
    }
}
