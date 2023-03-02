package frc.robot.commands.drivetrain;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj.Timer;

import frc.robot.configs.drivetrain.DriveMotorConfig;
import frc.robot.enums.ModulePosition;
import frc.robot.subsystems.Drivetrain;

public class MapDriveMotors extends CommandBase {
    private static int Step = 4;
    private static double FirstStepSeconds = 2.0;
    private static double StepSeconds = 0.25;
    
    // State
    private int percent = -100;
    private Timer timer = new Timer();

    public MapDriveMotors() {
        addRequirements(Drivetrain.get());
    }

    @Override
    public void execute() {
        if (percent >= 101) {
            cancel();
            return;
        }

        double value = percent / 100.0;

        var frontLeft = DriveMotorConfig.getDriveMotor(ModulePosition.FrontLeft);
        var frontRight = DriveMotorConfig.getDriveMotor(ModulePosition.FrontRight);
        var backLeft = DriveMotorConfig.getDriveMotor(ModulePosition.BackLeft);
        var backRight = DriveMotorConfig.getDriveMotor(ModulePosition.BackRight);

        frontLeft.set(ControlMode.PercentOutput, value);
        frontRight.set(ControlMode.PercentOutput, value);
        backLeft.set(ControlMode.PercentOutput, value);
        backRight.set(ControlMode.PercentOutput, value);
    
        boolean shouldStep = false;
        if (percent == -100) {
            if (timer.hasElapsed(FirstStepSeconds)) {
                shouldStep = true;
            }
        }
        else {
            if (timer.hasElapsed(StepSeconds)) {
                shouldStep = true;
            }
        }

        if (shouldStep) {
            step();
        }
    }

    private void step() {
        var frontLeftMap = DriveMotorConfig.getPercentVelocityMap(ModulePosition.FrontLeft);
        var frontRightMap = DriveMotorConfig.getPercentVelocityMap(ModulePosition.FrontRight);
        var backLeftMap = DriveMotorConfig.getPercentVelocityMap(ModulePosition.BackLeft);
        var backRightMap = DriveMotorConfig.getPercentVelocityMap(ModulePosition.BackRight);

        double value = percent / 100.0;

        var frontLeftVelocity = Drivetrain.get().frontLeftModule.getDriveVelocity();
        var frontRightVelocity = Drivetrain.get().frontRightModule.getDriveVelocity();
        var backLeftVelocity = Drivetrain.get().backLeftModule.getDriveVelocity();
        var backRightVelocity = Drivetrain.get().backRightModule.getDriveVelocity();

        frontLeftMap.addSample(value, frontLeftVelocity);
        frontRightMap.addSample(value, frontRightVelocity);
        backLeftMap.addSample(value, backLeftVelocity);
        backRightMap.addSample(value, backRightVelocity);

        percent += Step;
        timer.restart();
    }

    //#region Public
    public void reset() {
        var frontLeftMap = DriveMotorConfig.getPercentVelocityMap(ModulePosition.FrontLeft);
        var frontRightMap = DriveMotorConfig.getPercentVelocityMap(ModulePosition.FrontRight);
        var backLeftMap = DriveMotorConfig.getPercentVelocityMap(ModulePosition.BackLeft);
        var backRightMap = DriveMotorConfig.getPercentVelocityMap(ModulePosition.BackRight);

        frontLeftMap.clear();
        frontRightMap.clear();
        backLeftMap.clear();
        backRightMap.clear();

        percent = -100;
        timer.restart();
    }
    //#endregion Public
}
