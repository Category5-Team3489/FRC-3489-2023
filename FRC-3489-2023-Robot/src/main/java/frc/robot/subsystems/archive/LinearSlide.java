package frc.robot.subsystems.archive;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.networktables.GenericEntry;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.Cat5Shuffleboard;
import frc.robot.Triggers;

public class LinearSlide extends SubsystemBase {
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

    private final GenericEntry hasBeenHomedEntry;
    private final GenericEntry motorSpeedEntry;
    private final GenericEntry linearSlidePosition;
    private final GenericEntry linearSlideEncoderPosition;

    private final CANSparkMax motor = new CANSparkMax(Motor, MotorType.kBrushless);
    private final DigitalInput bottomLimitSwitch = new DigitalInput(BottomLimitSwitch);
    private final DigitalInput topLimitSwitch = new DigitalInput(TopLimitSwitch);

    private boolean hasBeenHomed = false;
    private boolean hasSetPosition = false;
    private double position = 0;

    // TODO Could add command at end of autonomous or some time during auto to start homing

    public LinearSlide() {
        register();

        ShuffleboardLayout mainLayout = Cat5Shuffleboard.createMainLayout("Linear Slide")
            .withSize(2, 1);
        linearSlidePosition = mainLayout.add("Linear Slide Position", " ").getEntry();
        linearSlideEncoderPosition = mainLayout.add("Linear Slide Encoder Position", 0.0).getEntry();

        ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("Linear Slide")
            .withSize(2, 1);
        diagnosticLayout.add("Linear Slide Diagnostic", linearSlideDiagnostic());

        new Trigger(() -> isRetracted())
            .onTrue(new InstantCommand(() -> {
                setPosition(0);


                if (motor.get() < 0) {
                    stop();
                }
            }));
        new Trigger(() -> isExtended())
            .onTrue(new InstantCommand(() -> {
                setPosition(EncoderCountLength);

                if (motor.get() > 0) {
                    stop();
                }
            }));

        new Trigger(() -> !hasBeenHomed)
            .and(Triggers.IsTeleopEnabled)
            .onTrue(new InstantCommand(() -> {
                retract();
                linearSlidePosition.setString("Homing");
             }))
             .onFalse(new InstantCommand(() -> {
                 linearSlidePosition.setString("Homed");
            }));

        new Trigger(() -> hasSetPosition)
            .and(Triggers.IsEnabled)
            .whileTrue(Commands.run(() -> {
                
                if (Math.abs(getPercentExtended() - getPercentExtendedAtPosition(position)) <= SetPositionTolerancePercentage) { // Is at set position and within tolerance
                    stop();
                    hasSetPosition = true;
                    setShuffleBoardPosition(position);
                    linearSlideEncoderPosition.setDouble(getPositionAtPercentExtended(position));
                    
                }
                else { // Has not arrived at set position, keep moving in the direction that is towards the set position
                    if(getPosition() - position <= 0) {
                        extend();
                        linearSlidePosition.setString("Extending");
                    }
                    else{
                        retract();
                        linearSlidePosition.setString("Retracting");
                    }
                }
            }, this));

        
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
        motor.set(RetractSpeed);
        motorSpeedEntry.setDouble(motor.get());
    }
    public void extend() {
        motor.set(ExtendSpeed);
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
        return EncoderCountLength * percentExtended;
    }
    public double getPercentExtended() {
        return getPosition() / EncoderCountLength;
    }

    public double getPercentExtendedAtPosition(double position) {
        return position / EncoderCountLength;
    }

    public void gotoPercentExtended(double percentExtended) {
        hasSetPosition = true;
        position = getPositionAtPercentExtended(percentExtended);
    }

    public void setShuffleBoardPosition(double position) {
        if(position == FullExtendEncoder) {
            linearSlidePosition.setString("Extended");
        } else if(position == HalfExtendEncoder) {
            linearSlidePosition.setString("Half Extended");
        } else if(position == FullretractEncoder) {
            linearSlidePosition.setString("Retracted");
        }
    }

    public CommandBase linearSlideDiagnostic() {
        return Commands.sequence(
            Commands.race(
                Commands.run(() -> {
                    gotoPercentExtended(FullretractEncoder);
                    setLinearSlide();
                }, this),
                Commands.run(() -> {
                    new WaitCommand(5);
                })
            ),
            Commands.race(
                Commands.run(() -> {
                    gotoPercentExtended(HalfExtendEncoder);
                    setLinearSlide();
                }, this),
                Commands.run(() -> {
                    new WaitCommand(5);
                })
            ),
            Commands.race(
                Commands.run(() -> {
                    gotoPercentExtended(FullExtendEncoder);
                    setLinearSlide();
                    new WaitCommand(5);
                }, this),
                Commands.run(() -> {
                    new WaitCommand(5);
                })
            ),
            Commands.race(
                Commands.run(() -> {
                    gotoPercentExtended(HalfExtendEncoder);
                    setLinearSlide();
                }, this),
                Commands.run(() -> {
                    new WaitCommand(5);
                })
            ),
            Commands.race(
                Commands.run(() -> {
                    gotoPercentExtended(FullretractEncoder);
                    setLinearSlide();
                }, this),
                Commands.run(() -> {
                    new WaitCommand(5);
                })
            )
        )
        .withName("Run Linear Slide Diagnostic");
    }

    public void setLinearSlide() {
        if (Math.abs(getPercentExtended() - getPercentExtendedAtPosition(position)) <= SetPositionTolerancePercentage) { // Is at set position and within tolerance
            stop();
            hasSetPosition = true;
            setShuffleBoardPosition(position);
            linearSlideEncoderPosition.setDouble(getPositionAtPercentExtended(position));
            
        }
        else { // Has not arrived at set position, keep moving in the direction that is towards the set position
            if(getPosition() - position <= 0) {
                extend();
                linearSlidePosition.setString("Extending");
            }
            else{
                retract();
                linearSlidePosition.setString("Retracting");
            }
        }
    }
}
