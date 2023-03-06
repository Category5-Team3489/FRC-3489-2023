// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotState;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.Command.InterruptionBehavior;

import frc.robot.Constants;
import frc.robot.RobotContainer;
import frc.robot.Constants.LedConstants;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

public class Leds extends Cat5Subsystem<Leds> {
    //#region Singleton
    private static Leds instance = new Leds();
    
    public static Leds get() {
        return instance;
    }
    //#endregion

    private boolean haveTeleopLedsFlashedThisEnable = false;

    private final PWMSparkMax rightLeds = new PWMSparkMax(LedConstants.RightPort);
    PWMSparkMax leftLeds = new PWMSparkMax(LedConstants.LeftPort);

    public LedState ledState = LedState.Off;

    // TODO Limit led setting bandwidth, compare led buffer with applied and unapplied on update then only set if they are different

   private Leds() {
    super((i) -> instance = i);
        // led.setLength(buffer.getLength());
        // led.setData(buffer);
        // led.start();

        RobotContainer.get().man.axisLessThan(3, Constants.LedConstants.CubeLEDManipulator)
        .onTrue(Commands.runOnce(() -> {
            setLeds(LedState.NeedCube);
        }))
        .onFalse(Commands.runOnce(() -> {
            setLeds(LedState.Off);
        }));

        RobotContainer.get().man.axisGreaterThan(3, Constants.LedConstants.ConeLEDManipulator)
        .onTrue(Commands.runOnce(() -> {
            setLeds(LedState.NeedCone);
        }))
        .onFalse(Commands.runOnce(() -> {
            setLeds(LedState.Off);
        }));

        if (RobotState.isDisabled() && getCurrentAlliance() == "Blue")
            setLeds(LedState.DisabledPatternBlue);
        else if (RobotState.isDisabled() && getCurrentAlliance() == "Red")
            setLeds(LedState.DisabledPatternRed);
        else setLeds(LedState.Off);
        

        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 3);

        layout.addString("LED", () -> ledState.toString());

        var mainLayout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
        .withSize(2, 4);
        mainLayout.addString("LED State", () -> ledState.toString());
    }

    public enum LedState {
        Off,
        TeleopBlink,
        NeedCone,
        NeedCube,
        Intake,
        PlaceCone,
        PlaceCube,
        DarkRed,
        Red,
        DisabledPatternBlue,
        DisabledPatternRed
    }

    public void setLeds(LedState ledState) {
        this.ledState = ledState;

        switch (ledState) {
        case Off: //Off
            stopLeds();
        break;
        case TeleopBlink: //Lawn Green
            setSolidColor(0.71);
        break;
        case NeedCone: //Yellow
            setSolidColor(0.69);
        break;
        case NeedCube: //Blue Violet 
            setSolidColor(0.89);
        break;
        case PlaceCone: //Yellow
            setSolidColor(0.69);
        break;
        case PlaceCube: //Violet 
            setSolidColor(0.91);
        break;
        case DarkRed:
            setSolidColor(0.59);
        break;
        case Red:
            setSolidColor(0.61);
        break;
        case DisabledPatternBlue:
            setSolidColor(0.7);
        break;
        case DisabledPatternRed:
            setSolidColor(0.27);
            break;
            default:
                break;
        }
    }

    public void setSolidColor(double colorSpeed) {
        rightLeds.set(colorSpeed);
        leftLeds.set(colorSpeed);
    }

    public void stopLeds() {
        rightLeds.set(0.99);
        leftLeds.set(0.99);
    }

    // public void setSolidColor(LedColor color) {
    //     for (var i = 0; i < buffer.getLength(); i++) {
    //         color.apply(i, buffer);
    //     }
    //     led.setData(buffer);
    //     colorEntry.setString(color.toString());
    // }

    // public void stopLeds() {
    //     setSolidColor(LedColor.Off);
    // }
    
    @Override
    public void periodic() {     
        tryFlashTeleopLeds();
    }

    public void tryFlashTeleopLeds() {
        if (DriverStation.isDisabled()) {
            haveTeleopLedsFlashedThisEnable = false;
            return;
        }

        if (!DriverStation.isTeleop() || haveTeleopLedsFlashedThisEnable) {
            return;
        }
        Commands.runOnce(() -> setLeds(LedState.TeleopBlink), this)
            .withTimeout(1)
            .withInterruptBehavior(InterruptionBehavior.kCancelIncoming)
            .schedule();
            
        // Command command = new FlashLeds(this, LedColor.White, 10, 0.1, 0.1)
        //     .withInterruptBehavior(InterruptionBehavior.kCancelIncoming);
        
        haveTeleopLedsFlashedThisEnable = true;
    }

    public Command getSolidColorForSecondsCommand(double color, double seconds, boolean isInterruptible) {
        Runnable start = () -> {
            setSolidColor(color);
        };
        Runnable end = () -> {
            stopLeds();
        };

        InterruptionBehavior interruptBehavior = isInterruptible ?
            InterruptionBehavior.kCancelSelf : InterruptionBehavior.kCancelIncoming;

        return Commands.startEnd(start, end, this)
            .withTimeout(seconds)
            .withInterruptBehavior(interruptBehavior);
    }

    private String getCurrentAlliance(){
        if (DriverStation.getAlliance() == Alliance.Blue)
        return "Blue";
        else if (DriverStation.getAlliance() == Alliance.Red)
        return "Red";
        else return "Invalid";
    }
 }
