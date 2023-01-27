// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.commands.leds;

import frc.robot.LedColor;
import frc.robot.subsystems.Leds;
import edu.wpi.first.wpilibj.Timer;

public class FlashLeds extends LedsCommandBase {
    private final Timer timer = new Timer();

    private final LedColor color;
    private final int cycles;
    private final double timeOn;
    private final double timeOff;
    
    private int currentCycle = 0;
    private boolean isOn = true;
    
    public FlashLeds(Leds leds, LedColor color, int cycles, double timeOn, double timeOff) {
        super(leds);

        this.color = color;
        this.cycles = cycles;
        this.timeOn = timeOn;
        this.timeOff = timeOff;
    }

    @Override
    public void initialize() {
        timer.start();
        leds.setSolidColor(color);
        System.out.println("initialize: " + currentCycle);
    }

    @Override
    public void execute() {
        if (isOn) {
            if (timer.advanceIfElapsed(timeOn)) {
                isOn = false;
                leds.stopLeds();
                System.out.println("off: " + currentCycle);
            }
        }
        else {
            if (timer.advanceIfElapsed(timeOff)) {
                isOn = true;
                leds.setSolidColor(color);
                System.out.println("on: " + currentCycle);
                currentCycle++;
            }
        }
    }


    @Override
    public void end(boolean interrupted) {
        leds.stopLeds();
        System.out.println("end: " + currentCycle);
    }

    @Override
    public boolean isFinished() {
        if (currentCycle == cycles) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isOverridable() {
        return false;
    }
}
