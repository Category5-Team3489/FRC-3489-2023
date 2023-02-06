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
import frc.robot.Constants;
import frc.robot.Constants.LinearSlideConstants;
import frc.robot.commands.archive.linearslide.HomeLinearSlide;
import frc.robot.commands.archive.linearslide.SetLinearSlide;

public class LinearSlideOld extends SubsystemBase {
    private final ShuffleboardTab tab = Constants.getMainTab();
    private GenericEntry hasBeenHomedEntry = tab.add("Has Linear Slide Been Homed", false).getEntry();

    private final CANSparkMax motor = new CANSparkMax(LinearSlideConstants.Motor, MotorType.kBrushless);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(LinearSlideConstants.BottomLimitSwitch);
    private final DigitalInput topLimitSwitch = new DigitalInput(LinearSlideConstants.TopLimitSwitch);

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
        motor.set(LinearSlideConstants.RetractSpeed);
    }
    public void extend() {
        motor.set(LinearSlideConstants.ExtendSpeed);
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
        setLinearSlide = new SetLinearSlide(this, getPosition(), LinearSlideConstants.FullExtendEncoder);
        setLinearSlide.schedule();
    }

    public boolean setHalfExtend() {
        setLinearSlide = new SetLinearSlide(this, getPosition(), LinearSlideConstants.HalfExtendEncoder);
        setLinearSlide.schedule();
        return true;
    }

    public void setRetract() {
        setLinearSlide = new SetLinearSlide(this, getPosition(), LinearSlideConstants.FullretractEncoder);
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