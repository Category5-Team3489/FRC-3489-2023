package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Constants;
import frc.robot.Triggers;
import frc.robot.Constants.LinearSlideConstants;

public class LinearSlide extends SubsystemBase {
    private final GenericEntry hasBeenHomedEntry;
    private final GenericEntry motorSpeedEntry;

    private final CANSparkMax motor = new CANSparkMax(LinearSlideConstants.Motor, MotorType.kBrushless);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(LinearSlideConstants.BottomLimitSwitch);
    private final DigitalInput topLimitSwitch = new DigitalInput(LinearSlideConstants.TopLimitSwitch);

    private boolean hasBeenHomed = false;
    private boolean hasSetPosition = false;
    private double position = 0;

    // TODO Could add command at end of autonomous or some time during auto to start homing

    public LinearSlide() {
        register();

        new Trigger(() -> isRetracted())
            .onTrue(new InstantCommand(() -> {
                setPosition(0);

                if (motor.get() < 0) {
                    stop();
                }
            }));
        new Trigger(() -> isExtended())
            .onTrue(new InstantCommand(() -> {
                setPosition(LinearSlideConstants.EncoderCountLength);

                if (motor.get() > 0) {
                    stop();
                }
            }));

        new Trigger(() -> !hasBeenHomed)
            .and(Triggers.IsTeleopEnabled)
            .onTrue(new InstantCommand(() -> retract()));

        new Trigger(() -> hasSetPosition)
            .and(Triggers.IsEnabled)
            .whileTrue(Commands.run(() -> {
                
                if (Math.abs(getPercentExtended() - getPositionAtPercentExtended(position)) <= LinearSlideConstants.SetPositionTolerancePercentage) { // Is at set position and within tolerance
                    stop();
                    hasSetPosition = true;
                }
                else { // Has not arrived at set position, keep moving in the direction that is towards the set position
                    if(getPosition() - position <= 0) {
                        extend();
                    }
                    else{
                        retract();
                    }
                }
            }, this));

        ShuffleboardLayout mainLayout = Constants.createMainLayout("Linear Slide")
            .withSize(2, 1);
        hasBeenHomedEntry = mainLayout.add("Has Been Homed", false).getEntry();
        motorSpeedEntry = mainLayout.add("Motor Speed", 0.0).getEntry();
    }

    public boolean hasBeenHomed() {
        return hasBeenHomed;
    }
    
    public boolean isRetracted() {
        return bottomLimitSwitch.get();
    }
    public boolean isExtended() {
        return topLimitSwitch.get();
    }
    
    public void stop() {
        motor.stopMotor();
        motorSpeedEntry.setDouble(0);
    }
    public void retract() {
        motor.set(LinearSlideConstants.RetractSpeed);
        motorSpeedEntry.setDouble(motor.get());
    }
    public void extend() {
        motor.set(LinearSlideConstants.ExtendSpeed);
        motorSpeedEntry.setDouble(motor.get());
    }

    public double getPosition() {
        return motor.getEncoder().getPosition();
    }
    public void setPosition(double position) {
        motor.getEncoder().setPosition(position);
        hasBeenHomed = true;
        hasBeenHomedEntry.setBoolean(hasBeenHomed);
    }

    public double getPositionAtPercentExtended(double percentExtended) {
        return LinearSlideConstants.EncoderCountLength * percentExtended;
    }
    public double getPercentExtended() {
        return getPosition() / LinearSlideConstants.EncoderCountLength;
    }

    public void gotoPercentExtended(double percentExtended) {
        hasSetPosition = true;
        position = getPositionAtPercentExtended(percentExtended);
    }
}
