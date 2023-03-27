package frc.robot;

import static edu.wpi.first.wpilibj2.command.Commands.*;

public class RobotActions {
    //#region Singleton
    private static RobotActions instance = new RobotActions();

    public static RobotActions get() {
        return instance;
    }
    //#endregion

    private RobotActions() {
        instance = this;
    }
}
