package frc.robot;

import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

import edu.wpi.first.math.Pair;
import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInWidgets;
import edu.wpi.first.wpilibj2.command.button.CommandJoystick;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;
import frc.robot.enums.GamePiece;

public class Cat5Input {
    // Devices
    private final CommandXboxController xbox = new CommandXboxController(0);
    private final CommandJoystick man = new CommandJoystick(1);

    // Triggers
    public Trigger automate = man.button(5)
        .debounce(0.1, DebounceType.kBoth);
    public Trigger gripperStop = man.button(1);
    public Trigger gripperIntake = man.button(2);
    public Trigger gripperOuttake = man.button(3);
    public Trigger wristPickup = man.button(4);
    public Trigger wristCarry = man.button(6);
    public Trigger armDoubleSubstation = man.button(8);
    public Trigger armHome = man.button(10);
    public Trigger armPickup = man.button(12);
    public Trigger armLow = man.button(11);
    public Trigger armMid = man.button(9)
        .debounce(0.1, DebounceType.kBoth);
    public Trigger armHigh = man.button(7);
    public Trigger navxZeroYaw = xbox.start();
    public Trigger drivetrainNorth = xbox.y();
    public Trigger drivetrainEast = xbox.b();
    public Trigger drivetrainSouth = xbox.a();
    public Trigger drivetrainWest = xbox.x();

    // Trigger Combos
    public Trigger armForceHome = new Trigger(() -> armHome.getAsBoolean())
        .and(() -> gripperStop.getAsBoolean());

    public Cat5Input(RobotContainer robotContainer) {
        if (Robot.isReal()) {
            return;
        }

        ArrayList<Pair<String, Pair<Trigger, Consumer<Trigger>>>> triggers = new ArrayList<Pair<String, Pair<Trigger, Consumer<Trigger>>>>();

        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("automate", new Pair<Trigger, Consumer<Trigger>>(automate, automate -> this.automate = automate)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("gripperStop", new Pair<Trigger, Consumer<Trigger>>(gripperStop, gripperStop -> this.gripperStop = gripperStop)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("gripperIntake", new Pair<Trigger, Consumer<Trigger>>(gripperIntake, gripperIntake -> this.gripperIntake = gripperIntake)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("gripperOuttake", new Pair<Trigger, Consumer<Trigger>>(gripperOuttake, gripperOuttake -> this.gripperOuttake = gripperOuttake)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("wristPickup", new Pair<Trigger, Consumer<Trigger>>(wristPickup, wristPickup -> this.wristPickup = wristPickup)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("wristCarry", new Pair<Trigger, Consumer<Trigger>>(wristCarry, wristCarry -> this.wristCarry = wristCarry)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("armDoubleSubstation", new Pair<Trigger, Consumer<Trigger>>(armDoubleSubstation, armDoubleSubstation -> this.armDoubleSubstation = armDoubleSubstation)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("armHome", new Pair<Trigger, Consumer<Trigger>>(armHome, armHome -> this.armHome = armHome)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("armPickup", new Pair<Trigger, Consumer<Trigger>>(armPickup, armPickup -> this.armPickup = armPickup)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("armLow", new Pair<Trigger, Consumer<Trigger>>(armLow, armLow -> this.armLow = armLow)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("armMid", new Pair<Trigger, Consumer<Trigger>>(armMid, armMid -> this.armMid = armMid)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("armHigh", new Pair<Trigger, Consumer<Trigger>>(armHigh, armHigh -> this.armHigh = armHigh)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("navxZeroYaw", new Pair<Trigger, Consumer<Trigger>>(navxZeroYaw, navxZeroYaw -> this.navxZeroYaw = navxZeroYaw)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("drivetrainNorth", new Pair<Trigger, Consumer<Trigger>>(drivetrainNorth, drivetrainNorth -> this.drivetrainNorth = drivetrainNorth)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("drivetrainEast", new Pair<Trigger, Consumer<Trigger>>(drivetrainEast, drivetrainEast -> this.drivetrainEast = drivetrainEast)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("drivetrainSouth", new Pair<Trigger, Consumer<Trigger>>(drivetrainSouth, drivetrainSouth -> this.drivetrainSouth = drivetrainSouth)));
        triggers.add(new Pair<String, Pair<Trigger, Consumer<Trigger>>>("drivetrainWest", new Pair<Trigger, Consumer<Trigger>>(drivetrainWest, drivetrainWest -> this.drivetrainWest = drivetrainWest)));

        for (Pair<String, Pair<Trigger, Consumer<Trigger>>> e : triggers) {
            GenericEntry entry = robotContainer.layouts.get(Cat5ShuffleboardLayout.Debug_Buttons)
                .add(e.getFirst(), false)
                .withWidget(BuiltInWidgets.kToggleSwitch)
                .getEntry();
            BooleanSupplier supplier = () -> entry.getBoolean(false);
            e.getSecond().getSecond()
                .accept(
                    e.getSecond()
                        .getFirst()
                        .or(supplier)
                );
        }
    }
    
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
        double speedLimiterPercent = 1.0 / 2.0;

        boolean leftBumper = xbox.leftBumper().getAsBoolean();
        boolean rightBumper = xbox.rightBumper().getAsBoolean();

        if (leftBumper && rightBumper) {
            speedLimiterPercent = 1.0 / 8.0;
        }
        else if (leftBumper) {
            speedLimiterPercent = 1.0 / 3.0;
        }
        else if (rightBumper) {
            speedLimiterPercent = 1.0;
        }

        return speedLimiterPercent;
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
        if (!DriverStation.isTeleopEnabled()) {
            return GamePiece.Unknown;
        }

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
