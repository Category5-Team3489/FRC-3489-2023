// package frc.robot.archive;
// package frc.robot.subsystems;

// import com.ctre.phoenix.motorcontrol.ControlMode;
// import com.ctre.phoenix.motorcontrol.FeedbackDevice;
// import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

// import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
// import edu.wpi.first.wpilibj2.command.CommandBase;
// import frc.robot.shuffleboard.Cat5ShuffleboardTab;

// import static frc.robot.Constants.WristConstants.*;

// public class Wrist extends Cat5Subsystem<Wrist> {
//     //#region Singleton
//     private static Wrist instance = new Wrist();

//     public static Wrist get() {
//         return instance;
//     }
//     //#endregion

//     // Devices
//     private final WPI_TalonSRX motor = new WPI_TalonSRX(MotorDeviceId);
//     private final DigitalInput topLimitSwitch = new DigitalInput(TopLimitSwitchChannel);
//     private final DigitalInput bottomLimitSwitch = new DigitalInput(BottomLimitSwitchChannel);

//     // Commands
//     private final CommandBase setWristAngleCommand;
//     private final CommandBase homeTopWristCommand;
//     // private final CommandBase homeBottomWristCommand;

//     // State
//     private double targetClicks;
//     private double targetDegrees;
//     private boolean isHomed = false;
//     // private boolean lastLimitSwitchValue = false;

//     private Wrist() {
//         super((i) -> instance = i);

//         setWristAngleCommand = setWristAngleCommand();
//         // homeBottomWristCommand = homeBottomWristCommand();
//         homeTopWristCommand = homeTopWristCommand();

//         motor.configFactoryDefault();
//         motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

//         motor.configPeakOutputForward(MinOutputPercent);
// 		motor.configPeakOutputReverse(MaxOutputPercent);

//         motor.config_kP(0, ProportionalGainPercentPerClickOfError);

//         var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
//             .withSize(2, 1);

//         layout.addDouble("Wrist Target Clicks", () -> targetClicks);
//         layout.addDouble("Wrist Target Degrees", () -> targetDegrees);
//         layout.addBoolean("Is Homed", () -> isHomed);
//         layout.addBoolean("Top Limit Switch", () -> topLimitSwitch.get());
//         layout.addBoolean("Bottom Limit Switch", () -> bottomLimitSwitch.get());
//     }

//     public void setWristAngle(double targetPositionDegree) {
//         targetDegrees = targetPositionDegree;
//         targetClicks = targetPositionDegree * (ClicksPerRotation / 360);
//     }

//     private CommandBase setWristAngleCommand() {
//         return run(() -> {
//             motor.set(ControlMode.Position, targetClicks);
//         });
//     }

//     // public boolean pollLimitSwitchRisingEdge() {
//     //     if (topLimitSwitch.get() && !lastLimitSwitchValue) {
//     //         lastLimitSwitchValue = true;

//     //         return true;
//     //     }

//     //     lastLimitSwitchValue = topLimitSwitch.get();

//     //     return false;
//     // }

//     private CommandBase homeTopWristCommand() {
//         return run(() -> {
//             if (isHomed) {
//                 return;
//             }
//             if (!isHomed && topLimitSwitch.get()) { //  && !lastLimitSwitchValue
//                 setWristAngle(MaxAngleDegrees);
//                 isHomed = true;
//             }
//             else {
//                 // motor.setVoltage(HomingPercent * 12.0);
//             }
//         });
//     }

//     // private CommandBase homeBottomWristCommand() {
//     //     return Commands.run(() -> {
//     //         if (isHomed) {
//     //             return;
//     //         }
//     //         if (!isHomed && bottomLimitSwitch.get() && !lastLimitSwitchValue) {
//     //             setWristAngle(MinAngleDegrees);
//     //             isHomed = true;
//     //         }
//     //         else {
//     //             motor.setVoltage(-HomingPercent * 12.0);
//     //         }
//     //     }, this);
//     // }

//     @Override
//     public void periodic() {
//         if (isHomed) {
//             setWristAngleCommand.schedule();
//         }
//         else {
//             homeTopWristCommand.schedule();
//         }
//     }

// private CommandBase fleeBottomLimitSwitchCommand() {
//     return run(() -> {
//         motor.setVoltage(FleeLimitSwitchPercent * 12);
//     })
//     .withTimeout(FleeLimitSwitchSeconds);
// }

// private CommandBase fleeTopLimitSwitchCommand() {
//     return run(() -> {
//         motor.setVoltage(-FleeLimitSwitchPercent * 12);
//     })
//     .withTimeout(FleeLimitSwitchSeconds);
// }
// }
