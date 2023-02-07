// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.archive;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Cat5Shuffleboard;
import frc.robot.commands.archive.linearslide.HomeLinearSlide;
import frc.robot.commands.archive.linearslide.SetLinearSlide;

public class LinearSlideOld extends SubsystemBase {
    public static final int Motor = 9;
    public static final int BottomLimitSwitch = 24;
    public static final int TopLimitSwitch = 25;
    
    public static final int GotoBottomButton = 11;
    public static final int GotoMiddleButton = 8;
    public static final int GotoTopButton = 7;
    public static final int StopButton = 12;
    
    public static final double ExtendSpeed = 0.3;
    public static final double RetractSpeed = -0.3;

    public static final double SetPositionTolerancePercentage = 0.025;

    public static final double EncoderCountLength = 1000;
    public static final double FullExtendEncoder = 1000;
    public static final double HalfExtendEncoder = 500;
    public static final double FullretractEncoder = 0;

    private final ShuffleboardTab tab = Cat5Shuffleboard.getMainTab();
    private GenericEntry hasBeenHomedEntry = tab.add("Has Linear Slide Been Homed", false).getEntry();

    private final CANSparkMax motor = new CANSparkMax(Motor, MotorType.kBrushless);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(BottomLimitSwitch);
    private final DigitalInput topLimitSwitch = new DigitalInput(TopLimitSwitch);

    private HomeLinearSlide homeLinearSlide;
    private SetLinearSlide setLinearSlide;

    private boolean hasBeenHomed = false;

    public LinearSlideOld() {
        register();
    }

    public boolean isRetracted() {
        return bottomLimitSwitch.get();
    }
    public boolean isExtended() {
        return topLimitSwitch.get();
    }

    public void stop() {
        if (homeLinearSlide != null) {
            homeLinearSlide.cancel();
        }
        // TODO STOP NORMAL MOVING

        motor.stopMotor();
    }
    public void retract() {
        motor.set(RetractSpeed);
    }
    public void extend() {
        motor.set(ExtendSpeed);
    }

    public double getPosition() {
        return motor.getEncoder().getPosition();
    }
    public void setPosition(double position) {
        motor.getEncoder().setPosition(position);
        hasBeenHomed = true;
        hasBeenHomedEntry.setBoolean(hasBeenHomed);
    }

    public void setFullExtend() {
        setLinearSlide = new SetLinearSlide(this, getPosition(), FullExtendEncoder);
        setLinearSlide.schedule();
    }

    public boolean setHalfExtend() {
        setLinearSlide = new SetLinearSlide(this, getPosition(), HalfExtendEncoder);
        setLinearSlide.schedule();
        return true;
    }

    public void setRetract() {
        setLinearSlide = new SetLinearSlide(this, getPosition(), FullretractEncoder);
        setLinearSlide.schedule();
    }

    // Worry about invalid extension, rehoming if hits any limit switches????
   // public  getPercentExtended() {
  //      return 
    //}

    @Override
    public void periodic() {
        tryStartHoming();
    }

    private void tryStartHoming() {
        boolean isEnabledAndHasNotBeenHomed = DriverStation.isEnabled() && !hasBeenHomed;
        boolean isNotHoming = homeLinearSlide == null || homeLinearSlide.isFinished();
        if (!isEnabledAndHasNotBeenHomed || !isNotHoming) {
            return;
        }

        homeLinearSlide = new HomeLinearSlide(this);
        homeLinearSlide.schedule();
    }
}