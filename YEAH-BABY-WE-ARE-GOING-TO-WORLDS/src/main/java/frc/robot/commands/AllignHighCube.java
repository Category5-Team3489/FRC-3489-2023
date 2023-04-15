package frc.robot.commands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.enums.LimelightPipeline;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Limelight;

public class AllignHighCube extends CommandBase {
    // Constants
    private static double MaxXMetersPerSecond = 0.5;
    private static double MaxYMetersPerSecond = 0.75;
    private static double StrafeToleranceMeters = 0.1;
    private static double DistanceToleranceMeters = 0.1;
    private static Rotation2d TargetHeading = Rotation2d.fromDegrees(180);
    private static double SpeedLimiterPercent = 0.5;
    private static double MaxOmegaDegreesPerSecond = 90;
    private static double XSetpointMeters = 0;
    private static double YSetpointMeters = 0;
    // State
    private final Limelight limelight;
    private final Drivetrain drivetrain;
    private final PIDController xController = new PIDController(1, 0, 0); // m/s per m of error
    private final PIDController yController = new PIDController(1, 0, 0); // m/s per m of error
    
    public AllignHighCube(Limelight limelight, Drivetrain drivetrain) {
        this.limelight = limelight;
        this.drivetrain = drivetrain;

        addRequirements(drivetrain);
    }

    @Override
    public void initialize() {
        limelight.setDesiredPipeline(LimelightPipeline.Fiducial);

        xController.setTolerance(StrafeToleranceMeters);
        yController.setTolerance(DistanceToleranceMeters);
    }

    @Override
    public void execute() {
        if (limelight.isActivePipeline(LimelightPipeline.Fiducial)) {
            drivetrain.driveFieldRelative(0, 0, SpeedLimiterPercent, TargetHeading, MaxOmegaDegreesPerSecond);
            return;
        }

        Pose3d campose = limelight.getCampose();
        double xMetersPerSecond = 0;
        double yMetersPerSecond = 0;

        if (campose != null) {
            xMetersPerSecond = xController.calculate(campose.getX(), XSetpointMeters);
            xMetersPerSecond = MathUtil.clamp(xMetersPerSecond, -MaxXMetersPerSecond, MaxXMetersPerSecond);

            yMetersPerSecond = yController.calculate(campose.getY(), YSetpointMeters);
            yMetersPerSecond = MathUtil.clamp(yMetersPerSecond, -MaxYMetersPerSecond, MaxYMetersPerSecond);
        }

        drivetrain.driveFieldRelative(xMetersPerSecond, yMetersPerSecond, SpeedLimiterPercent, TargetHeading, MaxOmegaDegreesPerSecond);
    }

    @Override
    public boolean isFinished() {
        return xController.atSetpoint() && yController.atSetpoint();
    }

    @Override
    public void end(boolean interrupted) {
        drivetrain.brakeTranslation();
        limelight.printTargetData();
    }
}
