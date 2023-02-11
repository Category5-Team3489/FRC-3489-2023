package frc.robot.constants;

import edu.wpi.first.wpilibj.Preferences;
import frc.robot.subsystems.Arm;

public class ArmConstants extends ConstantsBase<Arm> {
    public static final int MotorDeviceId = 1;
    public static final int LimitSwitchChannel = 1;

    public static final double V4BarGearRatio = 50.0 / 1.0;
    public static final double RadiansPerRevolution = 1.0 / (V4BarGearRatio * 2.0 * Math.PI);
    public static final double LimitSwitchAngleRadians = Math.toRadians(-80);
    public static final double MaxAngleRadians = Math.toRadians(90);

    public static final double HorizontalResistGravityVolts = 0.05 * 12.0;
    public static final double ResistStaticFrictionVolts = 0.05 * 12.0;
    public static final double HomingVolts = -0.15 * 12.0;

    private static final double ProportionalGainVoltsPerRadianOfError = (0.2 * 12.0) / Math.toRadians(90);
    private static final double IntegralGainVoltsPerRadianSecondOfError = (0.0 * 12.0) / (Math.toRadians(90) * 1.0);
    private static final double DerivativeGainVoltsPerRadianPerSecondOfError = (0.0 * 12.0) / (Math.toRadians(90) / 1.0);
    private static final double FeedforwardGainVoltsPerRadianOfError = (0 * 12.0) / Math.toRadians(90);
    private static final double IntegrationZoneRadiansOfError = Math.toRadians(45);
    private static final double MinOutputVolts = -0.4 * 12.0;
    private static final double MaxOutputVolts = 0.4 * 12.0;

    public static final double ProportionalGainPercentPerRevolutionOfError = ProportionalGainVoltsPerRadianOfError * ((1.0 / 12.0) / (2.0 * Math.PI));
    public static final double IntegralGainPercentPerRevolutionMillisecondOfError = IntegralGainVoltsPerRadianSecondOfError * ((1.0 / 12.0)) / ((1.0 / (2.0 * Math.PI)) * (1.0 / 1000.0));
    public static final double DerivativeGainPercentPerRevolutionPerMillisecondOfError = DerivativeGainVoltsPerRadianPerSecondOfError * ((1.0 / 12.0)) / ((1.0 / (2.0 * Math.PI)) / (1.0 / 1000.0));
    public static final double FeedforwardGainPercentPerRevolutionOfError = FeedforwardGainVoltsPerRadianOfError * ((1.0 / 12.0) / (2.0 * Math.PI));
    public static final double IntegrationZoneRevolutionsOfError = IntegrationZoneRadiansOfError * (1.0 / (2.0 * Math.PI));
    public static final double MinOutputPercent = MinOutputVolts * (1.0 / 12.0);
    public static final double MaxOutputPercent = MaxOutputVolts * (1.0 / 12.0);

    public ArmConstants(Arm subsystem) {
        super(subsystem);
    }
}
