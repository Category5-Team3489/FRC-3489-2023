// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.HashSet;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.autos.Cat5Autos;
import frc.robot.data.Cat5Data;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayout;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayouts;
import frc.robot.enums.GamePiece;
import frc.robot.enums.GridPosition;
import frc.robot.enums.WristState;
import frc.robot.interfaces.Cat5Updatable;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.Camera;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Indicator;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;
import frc.robot.subsystems.Odometry;
import frc.robot.subsystems.Wrist;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class RobotContainer implements Cat5Updatable {
    // State
    public final Robot robot;
    public final DataLog dataLog;
    public final Cat5ShuffleboardLayouts layouts = new Cat5ShuffleboardLayouts(this);
    public final Cat5Data data = new Cat5Data();
    public final Cat5Input input = new Cat5Input(this);
    private final Cat5Autos autos = new Cat5Autos();
    private final Cat5Actions actions;

    //#region Subsystem Init
    private HashSet<String> subsystems = new HashSet<String>();
    private String lastInitializedSubsystem = "";

    public void initSubsystem(String name) {
        boolean isUnique = subsystems.add(name);
        if (!isUnique) {
            Cat5.error(name + " subsystem initialized more than once!", false);
        }
        else {
            Cat5.print("Initializing " + name + " subsystem...");
            lastInitializedSubsystem = name;
        }
    }

    private void initComplete() {
        Cat5.print("Initialized " + lastInitializedSubsystem + " subsystem!");
    }
    //#endregion

    //#region Updatables
    private ArrayList<Cat5Updatable> updatables = new ArrayList<Cat5Updatable>();

    public void registerUpdatable(Cat5Updatable updatable) {
        updatables.add(updatable);
    }
    //#endregion

    // Subsystems
    private final Camera camera;

    private final NavX2 navx;
    private final Limelight limelight;
    private final Drivetrain drivetrain;

    private final Indicator indicator;
    private final Gripper gripper;
    private final Wrist wrist;
    private final Arm arm;

    private final Leds leds;

    private final Odometry odometry;

    public RobotContainer(Robot robot, DataLog dataLog) {
        this.robot = robot;
        this.dataLog = dataLog;

        registerUpdatable(data);

        Cat5.print("Initializing...");

        camera = new Camera(this);
        initComplete();

        indicator = new Indicator(this);
        initComplete();
        gripper = new Gripper(this, indicator);
        initComplete();
        wrist = new Wrist(this);
        initComplete();
        arm = new Arm(this, gripper);
        initComplete();

        navx = new NavX2(this);
        initComplete();
        limelight = new Limelight(this);
        initComplete();
        drivetrain = new Drivetrain(this, navx, arm, limelight);
        initComplete();

        leds = new Leds(this, indicator);
        initComplete();

        odometry = new Odometry(this, drivetrain, navx);
        initComplete();

        Cat5.print("Initialization complete!");

        actions = new Cat5Actions(this, camera, navx, limelight, drivetrain, indicator, gripper, wrist, arm, leds, odometry);

        configureBindings();

        addAutos();

        // Log match time
        DoubleLogEntry matchTimeLogEntry = new DoubleLogEntry(dataLog, "/match-time");
        data.createDatapoint(() -> Timer.getMatchTime())
            .withLog(data -> {
                matchTimeLogEntry.append(data);
            }, 5);

        // TODO Read through all new code to find test items!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            // TODO Indicate in shuffleboard with blinking widget when a game piece is picked up, heldGamePiece, unknown -> !unknown

            // TODO Move to carry position after picking a game piece up automatically // ISSUE WITH THIS IF AT HUMAN PLAYER STATION OR NOT FLOOR PICKUP POS

                // TODO When arm is starting to move, or stopiing, do the unstowPiece or another gripper command!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // USE Arm.isAroundTarget and inital state transition, in set state look for changes with held game piece being not unknown!?????

            // TODO Test 165deg or > turns being disallowed

            // TODO Reset odometry on navx heading jump

        // TODO Leds and prints for actions

        // TODO Human player station pickup, 30.315in, 0.77m to left and right of april tag is good position to center robot on
    
        // TODO x = 0.254m / tan(10deg - ty), mid cone node distance from limelight
        // TODO x = 0.403098m / tan(10deg - ty), mid cube node distance from limelight
        // TODO x = 0.695452 / tan(10deg - ty), human player station
        // TODO high pole is 45.81in to top of tape, 41.81in to bottom

        // TODO 18.13 in to center of cube node tag

        // TODO 27.38in to center of HP station tag from floor

        // limelight 10deg down from horizontal
        // limelight 34in off of floor

        // TODO Shuffleboard blink indicator, picked up piece at human player station

        // TODO Go through district champs code

        // TODO Use Limelight.printTargetData

        // TODO Drive command, set default command

        // TODO Delta trackers on subsystem active commands enums

        // TODO Implement LEDS and use leds... EVERYWHERE, + priority stuff for them, game piece indicator default

        // TODO Increase logging and shuffleboard frequencies?

        // TODO Arm, DeltaTracker, shuffleboard vitals and shuffleboard debug

        // TODO make dataLog private and make functions to handle shuffleboard and logging stuff, put inside of Cat5Data

        // TODO Log limelight: tx, ty, tid, ta

        // TODO Log even more stuff??????!??!?!??!
    }

    private void configureBindings() {
        input.automate.onTrue(actions.automation());

        input.gripperStop.onTrue(actions.gripperStop());
        input.gripperIntake.onTrue(actions.gripperIntake());
        input.gripperOuttake.onTrue(actions.gripperOuttake());

        input.wristPickup.onTrue(actions.wristPickup());
        input.wristCarry.onTrue(actions.wristCarry());

        input.armDoubleSubstation.onTrue(actions.armDoubleSubstation());
        input.armHome.onTrue(actions.armHome());
        input.armPickup.onTrue(actions.armPickup());
        input.armLow.onTrue(actions.armLow());
        input.armMid.onTrue(actions.armMid());
        input.armHigh.onTrue(actions.armHigh());

        input.navxZeroYaw.onTrue(actions.navxZeroYaw());

        input.drivetrainNorth.onTrue(actions.drivetrainCardinalDirection(0));
        input.drivetrainEast.onTrue(actions.drivetrainCardinalDirection(-90));
        input.drivetrainSouth.onTrue(actions.drivetrainCardinalDirection(-180));
        input.drivetrainWest.onTrue(actions.drivetrainCardinalDirection(-270));
    }

    private void addAutos() {
        autos.addAuto(() -> print("Doing nothing... Because this is the nothing auto!")
            .withName("NothingAuto")
        );

        autos.addSelectorWidget();
    }

    //#region Events
    public void disabledExit() {
        drivetrain.resetTargetHeading();
        Cat5.print("Drivetrain reset target heading on enable");

        GamePiece detected = gripper.getDetectedGamePiece();
        gripper.setHeldGamePiece(detected);
        Cat5.print("Detected game piece: \"" + detected.toString() + "\" on enable, set as held game piece in gripper");

        wrist.setState(WristState.Carry);

        arm.forceHome();
    }

    public void autonomousInit() {
        // TODO
        // Leds.get().getCommand(LedPattern.Blue, 1.0, false)

        indicator.setIndicatedGamePiece(GamePiece.Cone);
    }

    public void teleopInit() {        
        // TODO
        // Leds.get().getCommand(LedPattern.Green, 1.0, false)
    }
    //#endregion

    //#region Because of no singletons, use events instead maybe
    public void notifyHeadingJump() {
        drivetrain.resetTargetHeading();
    }
    
    public double getAverageDriveVelocityMetersPerSecond() {
        return drivetrain.getAverageDriveVelocityMetersPerSecond();
    }

    public GridPosition getArmGridPosition() {
        return arm.getGridPosition();
    }

    public void pickedUpGamePiece() {
        Cat5.print("Picked up " + gripper.getHeldGamePiece() + ", with arm at " + Cat5.prettyDouble(arm.getTargetDegrees()) + " degrees");
    }
    //#endregion

    public Command getAutonomousCommand() {
        return autos.getAutonomousCommand();
    }

    public void configureShuffleboardLayout(Cat5ShuffleboardLayout layout, ShuffleboardLayout shuffleboardLayout) {
        shuffleboardLayout.withSize(2, 4);

        if (layout == Cat5ShuffleboardLayout.Driver) {
            shuffleboardLayout.withPosition(0, 0);
        }
        else if (layout == Cat5ShuffleboardLayout.Manipulator) {
            shuffleboardLayout.withPosition(7, 0);
        }
    }

    @Override
    public void update(double time) {
        for (Cat5Updatable updatable : updatables) {
            updatable.update(time);
        }
    }
}
