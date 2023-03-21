package frc.robot;

public class RobotCommands {
    //#region Singleton
    private static RobotCommands instance = new RobotCommands();

    public static RobotCommands get() {
        return instance;
    }
    //#endregion

    private RobotCommands() {
        instance = this;
    }
}