package frc.robot.commands;

import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.enums.LimelightPipeline;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;
import frc.robot.subsystems.Odometry;

public class AllignMidCone extends CommandBase {
    // Constants
    private static final double ProportionalGain100PercentPerMeter = 1.0;
    private static final double TargetXMeters = 0; // positive forward, negative backwards
    private static final double TargetYMeters = 0; // positive left, negative right
    private static final Rotation2d TargetHeading = Rotation2d.fromDegrees(180);
    private static final double MetersPerSecond = 0.5;
    private static final double MinMetersPerSecond = 0.1;
    private static final double MinPercent = MinMetersPerSecond / MetersPerSecond;
    private static final double ToleranceMeters = 0.05;
    private static final double DegreesPerSecond = 45;
    
    // State
    private final Limelight limelight;
    private final Drivetrain drivetrain;
    private double errorMeters;

    public AllignMidCone(Limelight limelight, Drivetrain drivetrain) {
        this.limelight = limelight;
        this.drivetrain = drivetrain;

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        limelight.setDesiredPipeline(LimelightPipeline.MidRetroreflective);
    }

    @Override
    public void execute() {
        if (!limelight.isActivePipeline(LimelightPipeline.MidRetroreflective) || !drivetrain.isAroundTargetHeading()) {
            drivetrain.driveFieldRelative(0.0, 0.0, 1.0, TargetHeading, DegreesPerSecond);
            return;
        }

        
    }
}
