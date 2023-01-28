// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.math.filter.Debouncer.DebounceType;
import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Utils;

public class LinearSlide extends SubsystemBase {
    private final NetworkTable limelight = NetworkTableInstance.getDefault().getTable("limelight");
    private final NetworkTableEntry tx = limelight.getEntry("tx"); // -29.8 to 29.8 degrees
    private final NetworkTableEntry tid = limelight.getEntry("tid");
    private final NetworkTableEntry camtran = limelight.getEntry("camtran");
    private final NetworkTableEntry tvert = limelight.getEntry("tvert");

    private final ShuffleboardTab tab = Shuffleboard.getTab("RIB");
    private GenericEntry txEntry = tab.add("TX", 0.0).getEntry();
    private GenericEntry tidEntry = tab.add("TID", 0).getEntry();
    private GenericEntry camtranEntry = tab.add("CamTran", "").getEntry();
    private GenericEntry tvertEntry = tab.add("TVERT", 0.0).getEntry();
    private GenericEntry distEntry = tab.add("Dist", 0.0).getEntry();
    private GenericEntry hasBeenHomedEntry = tab.add("Has Linear Slide Been Homed", false).getEntry();
    private GenericEntry bottomLimitSwitchEntry = tab.add("Is At Bottom", false).getEntry();
    private GenericEntry topLimitSwitchEntry = tab.add("Is At Top", false).getEntry();

    private final CANSparkMax testMotor = new CANSparkMax(1, MotorType.kBrushless);
    
    private final DigitalInput bottomLimitSwitch = new DigitalInput(0);
    private final DigitalInput topLimitSwitch = new DigitalInput(1);
    private final Trigger bottomTrigger = new Trigger(() -> bottomLimitSwitch.get()).debounce(0.1, DebounceType.kBoth);
    private final Trigger topTrigger = new Trigger(() -> topLimitSwitch.get()).debounce(0.1, DebounceType.kBoth);

    private boolean hasBeenHomed = false;

    // Target area and distance have inverse square relationship???

    public LinearSlide() {
        register();

        limelight.getEntry("pipeline").setNumber(1);
        // limelight.getEntry("pipeline").setNumber(2);
    }

    public boolean isAtBottom() {
        return bottomTrigger.getAsBoolean();
    }
    public boolean isAtTop() {
        return topTrigger.getAsBoolean();
    }

    public double getPosition() {
        return testMotor.getEncoder().getPosition();
    }
    public void setPosition(double position) {
        testMotor.getEncoder().setPosition(position);
        hasBeenHomed = true;
        hasBeenHomedEntry.setBoolean(hasBeenHomed);
    }

    @Override
    public void periodic() {
        updateShuffleboard();

        txEntry.setDouble(tx.getDouble(0.0));
        tidEntry.setInteger(tid.getInteger(-1));
        var a = camtran.getNumberArray(new Double[] {});
        String s = "";
        for (Number n : a) {
            s += n + ", ";
        }
        camtranEntry.setString(s);
        double tvertValue = tvert.getDouble(0.0);
        tvertEntry.setDouble(tvertValue);

        double percent = Utils.step(tvertValue, 48, 21);
        double distance = Utils.lerp(25, 57, percent * percent);
        distEntry.setDouble(distance);
        

        //double percent = 

        if (!DriverStation.isTeleopEnabled()) {
            return;
        }

        // < -10, move left
        // > -10 && < 10 stop
        // > 10, move right

        double txValue = tx.getDouble(0);

        long tidValue = tid.getInteger(-1);

        if (tidValue == -1) {
            testMotor.stopMotor();
        }
        else {
            double speed = tidValue / 25d;
            testMotor.set(speed);
        }
        // if (txValue < -10) {
        //     testMotor.set(0.15);
        // }
        // if (txValue > -10 && txValue < 10) {
        //     testMotor.stopMotor();
        // }
        // if (txValue > 10) {
        //     testMotor.set(-0.15);
        // }

        // if (isAtTop()) {
        //     testMotor.set(-0.15);
        //     System.out.println("top");
        //     // setPosition(10000);
        // }
        // else if (isAtBottom()) {
        //     testMotor.set(0.15);
        //     System.out.println("bottom");
        //     // setPosition(0);
        // }
        // else {
        //     testMotor.stopMotor();
        //     System.out.println("stop");
        // }
    }

    private void updateShuffleboard() {
        topLimitSwitchEntry.setBoolean(isAtTop());
        bottomLimitSwitchEntry.setBoolean(isAtBottom());
    }
}
