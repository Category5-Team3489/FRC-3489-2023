package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.commands.placement.HighConeNode;
import frc.robot.commands.placement.HighCubeNode;
import frc.robot.commands.placement.MidConeNode;
import frc.robot.commands.placement.MidCubeNode;
import frc.robot.enums.ArmState;
import frc.robot.enums.GamePiece;
import frc.robot.enums.WristState;
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

import edu.wpi.first.math.geometry.Rotation2d;

public class Cat5Actions {
    public final RobotContainer robotContainer;

    public final Camera camera;

    public final NavX2 navx;
    public final Limelight limelight;
    public final Drivetrain drivetrain;

    public final Indicator indicator;
    public final Gripper gripper;
    public final Wrist wrist;
    public final Arm arm;

    public final Leds leds;

    public final Odometry odometry;

    public Cat5Actions(RobotContainer robotContainer, Camera camera, NavX2 navx, Limelight limelight, Drivetrain drivetrain, Indicator indicator, Gripper gripper, Wrist wrist, Arm arm, Leds leds, Odometry odometry) {
        this.robotContainer = robotContainer;
        this.camera = camera;
        this.navx = navx;
        this.limelight = limelight;
        this.drivetrain = drivetrain;
        this.indicator = indicator;
        this.gripper = gripper;
        this.wrist = wrist;
        this.arm = arm;
        this.leds = leds;
        this.odometry = odometry;
    }

    //#region Automation
    public Command automation() {
        return runOnce(() -> {
            switch (arm.getGridPosition()) {
                case Low:
                    Cat5.print("Low automation not implemented!");
                    break;
                case Mid:
                    if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                        automationMidCube().schedule();
                    }
                    else {
                        automationMidCone().schedule();
                    }
                    break;
                case High:
                    if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                        automationHighCube().schedule();
                    }
                    else {
                        automationHighCone().schedule();
                    }
                    break;
            }
        });
    }

    private Command automationMidCone() {
        return sequence(
            new MidConeNode(limelight, drivetrain),
            runOnce(() -> {
                arm.setState(ArmState.ScoreMidCone);
            }),
            waitSeconds(0.3),
            gripperOuttake()
        );
    }
    private Command automationHighCone() {
        return sequence(
            new HighConeNode(limelight, drivetrain),
            waitSeconds(0.5),
            gripperOuttake()
        );
    }
    private Command automationMidCube() {
        return sequence(
            new MidCubeNode(limelight, drivetrain),
            waitSeconds(0.5),
            gripperOuttake()
        );
    }
    private Command automationHighCube() {
        return sequence(
            new HighCubeNode(limelight, drivetrain),
            waitSeconds(0.5),
            gripperOuttake()
        );
    }
    //#endregion

    //#region Auto
    public Command waitForDriveCommand() {
        return waitUntil(() -> {
            return drivetrain.isDriveCommandActive();
        });
    }

    public Command waitUntilDrivetrainIsAroundTargetHeading() {
        return waitUntil(() -> drivetrain.isAroundTargetHeading());
    }

    public Command waitUntilArmIsAroundTarget() {
        return waitUntil(() -> arm.isAroundTarget());
    }
    //#endregion

    //#region General
    public Command gripperStop() {
        return runOnce(() -> {
            gripper.setHeldGamePiece(GamePiece.Unknown);
            gripper.resetCanReintakeAgain();
            gripper.stopCommand.schedule();
        });
    }
    public Command gripperIntake() {
        return runOnce(() -> {
            gripper.setHeldGamePiece(GamePiece.Unknown);
            gripper.resetCanReintakeAgain();
            gripper.intakeCommand.schedule();
        });
    }
    public Command gripperOuttake() {
        return runOnce(() -> {
            switch (arm.getGridPosition()) {
                case Low:
                    if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                        gripper.lowOuttakeCubeCommand.schedule();
                    }
                    else {
                        gripper.lowOuttakeConeCommand.schedule();
                    }
                    break;
                case Mid:
                    if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                        gripper.midOuttakeCubeCommand.schedule();
                    }
                    else {
                        gripper.midOuttakeConeCommand.schedule();
                    }
                    break;
                case High:
                    if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                        gripper.highOuttakeCubeCommand.schedule();
                    }
                    else {
                        gripper.highOuttakeConeCommand.schedule();
                    }
                    break;
            }

            gripper.setHeldGamePiece(GamePiece.Unknown);
        });
    }

    public Command wristPickup() {
        return runOnce(() -> {
            wrist.setState(WristState.PickupOrLow);
        });
    }
    public Command wristCarry() {
        return runOnce(() -> {
            wrist.setState(WristState.CarryOrHighest);
        });
    }
    public Command wristWaitForArm() {
        return waitSeconds(0.4);
    }

    public Command armDoubleSubstation() {
        return sequence(
            runOnce(() -> {
                arm.setState(ArmState.DoubleSubstation);
            }),
            wristWaitForArm(),
            runOnce(() -> {
                wrist.setState(WristState.DoubleSubstation);
            }),
            gripperIntake()
        );
    }
    // TODO get rid of allowForceHoming
    public Command armHome(boolean allowForceHoming) {
        return runOnce(() -> {
            // TODO this will also get removed
            if (allowForceHoming && arm.getState() == ArmState.Home && gripper.getHeldGamePiece() == GamePiece.Unknown) {
                arm.forceHome();
            }

            arm.setState(ArmState.Home);
            wrist.setState(WristState.CarryOrHighest);
            gripper.resetCanReintakeAgain();
            gripper.stopCommand.schedule();
        });
    }
    public Command armPickup() {
        return sequence(
            runOnce(() -> {
                arm.setState(ArmState.Pickup);
            }),
            wristWaitForArm(),
            runOnce(() -> {
                wrist.setState(WristState.PickupOrLow);
            }),
            gripperIntake()
        );
    }
    public Command armLow() {
        return sequence(
            runOnce(() -> {
                if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                    arm.setState(ArmState.LowCube);
                }
                else {
                    arm.setState(ArmState.LowCone);
                }
            }),
            wristWaitForArm(),
            runOnce(() -> {
                wrist.setState(WristState.PickupOrLow);
            })
        );
    }
    public Command armMid() {
        return sequence(
            runOnce(() -> {
                if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                    arm.setState(ArmState.MidCube);
                }
                else {
                    ArmState state = arm.getState();
                    if (state == ArmState.MidCone) {
                        arm.setState(ArmState.ScoreMidCone);
                    }
                    else if (state == ArmState.ScoreMidCone) {
                        arm.setState(ArmState.MidCone);
                    }
                    else {
                        arm.setState(ArmState.MidCone);
                    }
                }
            }),
            wristWaitForArm(),
            runOnce(() -> {
                wrist.setState(WristState.Mid);
            })
        );
    }
    public Command armHigh() {
        return sequence(
            runOnce(() -> {
                if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                    arm.setState(ArmState.HighCube);
                }
                else {
                    arm.setState(ArmState.HighCone);
                }
            }),
            wristWaitForArm(),
            runOnce(() -> {
                if (gripper.getHeldGamePiece() == GamePiece.Cube) {
                    wrist.setState(WristState.HighCube);
                }
                else {
                    wrist.setState(WristState.HighCone);
                }
            })
        );
    }

    public Command navxZeroYaw() {
        return navx.getZeroYawCommand();
    }

    public Command drivetrainCardinalDirection(double degrees) {
        return runOnce(() -> {
            Rotation2d target = Rotation2d.fromDegrees(degrees);
            drivetrain.setTargetHeading(target);

            // double delta = Math.abs(navx.getRotation().minus(target).getDegrees());
            // if (delta < 165) {
            //     drivetrain.setTargetHeading(target);
            // }
        });
    }
    //#region General
}
