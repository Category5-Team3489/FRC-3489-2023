package frc.robot;

import edu.wpi.first.wpilibj2.command.Command;
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
    @SuppressWarnings("unused")
    private final RobotContainer robotContainer;

    @SuppressWarnings("unused")
    private final Camera camera;

    private final NavX2 navx;
    @SuppressWarnings("unused")
    private final Limelight limelight;
    private final Drivetrain drivetrain;

    @SuppressWarnings("unused")
    private final Indicator indicator;
    private final Gripper gripper;
    private final Wrist wrist;
    private final Arm arm;

    @SuppressWarnings("unused")
    private final Leds leds;

    @SuppressWarnings("unused")
    private final Odometry odometry;

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

    public Command gripperStop() {
        return gripper.stopCommand;
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
            gripper.setHeldGamePiece(GamePiece.Unknown);
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
        });
    }

    public Command wristPickup() {
        return runOnce(() -> {
            wrist.setState(WristState.Pickup);
        });
    }
    public Command wristCarry() {
        return runOnce(() -> {
            wrist.setState(WristState.Carry);
        });
    }

    public Command armDoubleSubstation() {
        return sequence(
            runOnce(() -> {
                arm.setState(ArmState.DoubleSubstation);
            }),
            waitSeconds(0.4),
            runOnce(() -> {
                wrist.setState(WristState.DoubleSubstation);
                gripper.intakeCommand.schedule();
            })
        );
    }
    public Command armHome() {
        return runOnce(() -> {
            if (arm.getState() == ArmState.Home) {
                arm.forceHome();
            }
            arm.setState(ArmState.Home);
            wrist.setState(WristState.Carry);
            gripper.stopCommand.schedule();
        });
    }
    public Command armPickup() {
        return sequence(
            runOnce(() -> {
                arm.setState(ArmState.Pickup);
            }),
            waitSeconds(0.4),
            runOnce(() -> {
                wrist.setState(WristState.Pickup);
                gripper.intakeCommand.schedule();
            })
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
            waitSeconds(0.4),
            runOnce(() -> {
                wrist.setState(WristState.Pickup);
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
                    if (arm.getState() == ArmState.MidCone) {
                        arm.setState(ArmState.ScoreMidCone);
                    }
                    else if (arm.getState() == ArmState.ScoreMidCone) {
                        arm.setState(ArmState.MidCone);
                    }
                    else {
                        arm.setState(ArmState.MidCone);
                    }
                }
            }),
            waitSeconds(0.4),
            runOnce(() -> {
                wrist.setState(WristState.Carry);
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
            waitSeconds(0.4),
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
            double delta = Math.abs(navx.getRotation().getDegrees() - target.getDegrees());
            if (delta < 165) {
                drivetrain.setTargetHeading(target);
            }
        });
    }
}
