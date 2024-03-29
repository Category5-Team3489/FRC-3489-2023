package frc.robot.data.shuffleboard;

import edu.wpi.first.wpilibj.shuffleboard.BuiltInLayouts;
import edu.wpi.first.wpilibj.shuffleboard.LayoutType;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;

public enum Cat5ShuffleboardLayout {
    Driver(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList),
    Manipulator(Cat5ShuffleboardTab.Main, BuiltInLayouts.kList),
    Offsets_Config(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList),
    Debug_Buttons(Cat5ShuffleboardTab.Debug, BuiltInLayouts.kList),
    Debug_Drive_Velocities(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList),
    Debug_Drive_Stator_Current(Cat5ShuffleboardTab.Drivetrain, BuiltInLayouts.kList),
    Debug_Target_Data(Cat5ShuffleboardTab.Limelight, BuiltInLayouts.kList),
    Debug_Campose(Cat5ShuffleboardTab.Limelight, BuiltInLayouts.kList),
    Debug_Pipeline(Cat5ShuffleboardTab.Limelight, BuiltInLayouts.kList),
    Debug_Distance_Estimation(Cat5ShuffleboardTab.Limelight, BuiltInLayouts.kList),
    Debug_Gripper(Cat5ShuffleboardTab.Gripper, BuiltInLayouts.kList),
    Debug_Odometry(Cat5ShuffleboardTab.Odometry, BuiltInLayouts.kList),
    Debug_Arm(Cat5ShuffleboardTab.Arm, BuiltInLayouts.kList),
    Debug_Wrist(Cat5ShuffleboardTab.Wrist, BuiltInLayouts.kList);

    private final Cat5ShuffleboardTab tab;
    private final LayoutType type;
    
    private Cat5ShuffleboardLayout(Cat5ShuffleboardTab tab, LayoutType type) {
        this.tab = tab;
        this.type = type;
    }

    public ShuffleboardLayout create() {
        return tab.get().getLayout(toString(), type);
    }
}
