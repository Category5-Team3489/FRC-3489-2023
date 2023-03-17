package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import frc.robot.shuffleboard.Cat5ShuffleboardTab;

import static frc.robot.Constants.WristConstants.*;

public class Wrist extends Cat5Subsystem<Wrist> {
    private static Wrist instance = new Wrist();

    //#region Singleton
    public static Wrist get() {
        return instance;
    }
    //#endregion

    //Devices
    private final WPI_TalonSRX motor = new WPI_TalonSRX(WristMotorId);

    private double targetClicks;
    private double targetPositionDegree;

    private Wrist() {
        super((i) -> instance = i);

        motor.configFactoryDefault();
        motor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute);

        motor.configPeakOutputForward(PeakOutputForward);
		motor.configPeakOutputReverse(PeakOutputReverse);

        motor.config_kP(SlotIdx, KP);

        var layout = getLayout(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList)
            .withSize(2, 1);

        layout.addDouble("Wrist Target Clicks", () -> targetClicks);
    }

    public void setWristAngle(double targetPositionDegree) { 
        this.targetPositionDegree = targetPositionDegree;
        targetClicks = targetPositionDegree * (ClicksPerRotation / 360);
        motor.set(ControlMode.Position, targetClicks);
    }

    @Override
    public void periodic() {
        setWristAngle(targetPositionDegree);
    }
}
