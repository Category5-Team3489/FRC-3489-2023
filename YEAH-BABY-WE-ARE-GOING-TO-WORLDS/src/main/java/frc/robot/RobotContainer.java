// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.ArrayList;
import java.util.HashSet;

import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.autos.Cat5Autos;
import frc.robot.data.Cat5Data;
import frc.robot.data.shuffleboard.Cat5ShuffleboardLayouts;
import frc.robot.enums.GamePiece;
import frc.robot.interfaces.Cat5Updatable;
import frc.robot.subsystems.Camera;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Indicator;
import frc.robot.subsystems.Leds;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.NavX2;
import frc.robot.subsystems.Wrist;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class RobotContainer implements Cat5Updatable {
    // State
    public final Robot robot;
    public final DataLog dataLog;
    public final Cat5ShuffleboardLayouts layouts;
    public final Cat5Data data;
    public final Cat5Input input;
    private final Cat5Autos autos;

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
    @SuppressWarnings("unused")
    private final Camera camera;

    private final NavX2 navx;
    @SuppressWarnings("unused")
    private final Limelight limelight;
    private final Drivetrain drivetrain;

    private final Indicator indicator;
    private final Gripper gripper;
    @SuppressWarnings("unused")
    private final Wrist wrist;

    @SuppressWarnings("unused")
    private final Leds leds;


    public RobotContainer(Robot robot, DataLog dataLog) {
        this.robot = robot;
        this.dataLog = dataLog;
        layouts = new Cat5ShuffleboardLayouts();
        data = new Cat5Data();
        input = new Cat5Input(this);
        autos = new Cat5Autos();

        registerUpdatable(data);

        Cat5.print("Initializing...");

        camera = new Camera(this);
        initComplete();

        navx = new NavX2(this);
        initComplete();
        limelight = new Limelight(this);
        initComplete();
        drivetrain = new Drivetrain(this, navx);
        initComplete();

        indicator = new Indicator(this);
        initComplete();
        gripper = new Gripper(this, indicator);
        initComplete();
        wrist = new Wrist(this);
        initComplete();

        leds = new Leds(this);
        initComplete();

        Cat5.print("Initialization complete!");

        configureBindings();

        addAutos();

        // TODO When outtaking with gripper, always set held game piece to Unknown

        // TODO Leds and prints for actions

        // TODO Human player station pickup, 30.315in, 0.77m to left and right of april tag is good position to center robot on
    
        // TODO x = 0.254m / tan(10deg - ty), mid cone node distance from limelight
        // TODO x = 0.403098m / tan(10deg - ty), mid cube node distance from limelight
        // TODO x = 0.695452 / tan(10deg - ty), human player station

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
    }

    private void configureBindings() {
        input.automate.onTrue(runOnce(() -> {

        }));

        input.gripperStop.onTrue(gripper.stopCommand);
        input.gripperIntake.onTrue(runOnce(() -> {
            gripper.setHeldGamePiece(GamePiece.Unknown);
            gripper.intakeCommand.schedule();
        }));
        input.gripperOuttake.onTrue(runOnce(() -> {
            gripper.setHeldGamePiece(GamePiece.Unknown);
            // TODO schedule coresponding command
        }));

        input.wristPickup.onTrue(runOnce(() -> {

        }));
        input.wristCarry.onTrue(runOnce(() -> {

        }));

        input.armDoubleSubstation.onTrue(runOnce(() -> {

        }));
        input.armHome.onTrue(runOnce(() -> {

        }));
        input.armPickup.onTrue(runOnce(() -> {

        }));
        input.armLow.onTrue(runOnce(() -> {

        }));
        input.armMid.onTrue(runOnce(() -> {

        }));
        input.armHigh.onTrue(runOnce(() -> {

        }));

        input.navxZeroYaw.onTrue(navx.getZeroYawCommand());

        // TODO Disallow > 165 deg turns
        input.drivetrainNorth.onTrue(runOnce(() -> {
            // 0
        }));
        input.drivetrainEast.onTrue(runOnce(() -> {
            // -90
        }));
        input.drivetrainSouth.onTrue(runOnce(() -> {
            // -180
        }));
        input.drivetrainWest.onTrue(runOnce(() -> {
            // -270
        }));
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

        // TODO
        // GamePiece detected = Gripper.get().getDetectedGamePiece();
        // Gripper.get().setHeldGamePiece(detected);
        // Cat5Utils.time();
        // System.out.println("Detected game piece: \"" + detected.toString() + "\" on enable, set as held game piece in gripper");

        // TODO
        // Wrist.get().setState(WristState.Carry);

        // TODO
        // Arm.get().command(ArmCommand.ForceHome);
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

    //#region Wrappers
    public void resetTargetHeading() {
        drivetrain.resetTargetHeading();
    }
    
    public double getAverageDriveVelocityMetersPerSecond() {
        return drivetrain.getAverageDriveVelocityMetersPerSecond();
    }
    //#endregion

    public Command getAutonomousCommand() {
        return autos.getAutonomousCommand();
    }

    @Override
    public void update(double time) {
        for (Cat5Updatable updatable : updatables) {
            updatable.update(time);
        }
    }
}
