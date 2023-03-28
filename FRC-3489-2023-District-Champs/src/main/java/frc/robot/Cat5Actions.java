package frc.robot;

public class Cat5Actions {
    //#region Singleton
    private static Cat5Actions instance = new Cat5Actions();

    public static Cat5Actions get() {
        return instance;
    }
    //#endregion

    private Cat5Actions() {
        instance = this;
    }
}
