// package frc.robot.subsystems;

// import java.util.function.BooleanSupplier;

// import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

// import edu.wpi.first.wpilibj.DigitalInput;
// import edu.wpi.first.wpilibj.Timer;
// import edu.wpi.first.wpilibj2.command.CommandBase;
// import frc.robot.RobotContainer;
// import frc.robot.enums.GamePiece;

// public class Gripper extends Cat5Subsystem {
//     // Constants
//     private static final boolean IsConeReintakingEnabled = true;
//     private static final boolean IsCubeReintakingEnabled = true;
//     private static final double ReintakeAntiConeEatTimeout = 2.0;
//     private static final double ReintakeAntiCubeEatTimeout = 2.0;

//     private static final double IntakePercent = -0.5;

//     private static final int LeftMotorDeviceId = 9;
//     private static final int RightMotorDeviceId = 10;
//     private static final int LimitSwitchChannel = 2;

//     // Devices
//     private final WPI_TalonSRX leftMotor = new WPI_TalonSRX(LeftMotorDeviceId);
//     private final WPI_TalonSRX rightMotor = new WPI_TalonSRX(RightMotorDeviceId);
//     private final DigitalInput limitSwitch = new DigitalInput(LimitSwitchChannel);

//     // Suppliers
//     private final BooleanSupplier isLimitSwitchDisabled;

//     // Commands
//     private final CommandBase stopCommand = getStopCommand();
//     private final CommandBase intakeCommand = getIntakeCommand();

//     // State
//     private GamePiece heldGamePiece = GamePiece.Unknown;
//     private double motorPercent = 0;
//     private boolean canReintakeAgain = true;
//     private Timer reintakeAntiEatTimer = new Timer();

//     public Gripper(RobotContainer robotContainer) {
//         super(robotContainer);
//     }

//     private void setMotors(double percent) {
//         motorPercent = percent;
        
//         if (percent != 0) {
//             leftMotor.set(percent);
//             rightMotor.set(-percent);
//         }
//         else {
//             leftMotor.stopMotor();
//             rightMotor.stopMotor();
//         }
//     }

//     @Override
//     public void periodic() {
//         // if (heldGamePiece != GamePiece.Unknown) {
//         //     var indicated = Leds.get().getIndicatedGamePiece();
//         //     if (indicated != GamePiece.Unknown) {
//         //         heldGamePiece = indicated;
//         //     }
//         // }
//     }
    
//     private CommandBase getStopCommand() {
//         return run(() -> {
//             if (heldGamePiece == GamePiece.Unknown) {
//                 canReintakeAgain = true;
//             }

//             setMotors(0);

//             if (canReintakeAgain) {
//                 if (IsConeReintakingEnabled) {
//                     if (heldGamePiece == GamePiece.Cone && limitSwitch.get()) {
//                         intakeCommand.schedule();
//                         reintakeAntiEatTimer.restart();
//                     }
//                 }
//                 if (IsCubeReintakingEnabled) {
//                     if (heldGamePiece == GamePiece.Cube && limitSwitch.get()) {
//                         intakeCommand.schedule();
//                         reintakeAntiEatTimer.restart();
//                     }
//                 }
//             }
//         })
//             .withName("Stop");
//     }

//     private CommandBase getIntakeCommand() {
//         return run(() -> {
//             if (heldGamePiece == GamePiece.Unknown) {
//                 canReintakeAgain = true;
//             }

//             GamePiece detectedGamePiece = GamePiece.Unknown;

//             // TODO
//             // if (!isLimitSwitchDisabled.getAsBoolean()) {
//             //     detectedGamePiece = getDetectedGamePiece();
//             // }

//             if (detectedGamePiece == GamePiece.Unknown) {
//                 if (IsConeReintakingEnabled) {
//                     if (heldGamePiece == GamePiece.Cone &&
//                         canReintakeAgain && reintakeAntiEatTimer.hasElapsed(ReintakeAntiConeEatTimeout)) {
//                         canReintakeAgain = false;
//                         stopCommand.schedule();
//                     }
//                 }
//                 if (IsCubeReintakingEnabled) {
//                     if (heldGamePiece == GamePiece.Cube &&
//                         canReintakeAgain && reintakeAntiEatTimer.hasElapsed(ReintakeAntiCubeEatTimeout)) {
//                         canReintakeAgain = false;
//                         stopCommand.schedule();
//                     }
//                 }

//                 setMotors(IntakePercent);
//             }
//             else {
//                 // TODO
//                 // Cat5Utils.time();
//                 // System.out.println("Now holding game piece: " + detectedGamePiece);
//                 // System.out.println("Arm angle degrees: " + Arm.get().getTargetAngleDegrees());

//                 heldGamePiece = detectedGamePiece;
//                 stopCommand.schedule();
//             }
//         })
//             .withName("Intake");
//     }

//     private CommandBase getNamePercentSecondsCommand(String name, double percent, double seconds) {
//         return run(() -> {
//             setMotors(percent);
//         })
//             .withTimeout(seconds)
//             .withName(name);
//     }
// }
