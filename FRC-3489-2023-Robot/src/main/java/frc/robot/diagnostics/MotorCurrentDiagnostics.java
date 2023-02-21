package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import frc.robot.shuffleboard.Cat5Shuffleboard;
import frc.robot.subsystems.PowerDistributionHub;

public class MotorCurrentDiagnostics extends Diagnostics<PowerDistributionHub> {
    private double[] chanleCurrents;
    private final PowerDistribution pdh = subsystem.pdh;

    ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("Motor Voltage")
        .withSize(2, 4);

    public MotorCurrentDiagnostics(PowerDistributionHub subsystem) {
        super(subsystem);
    }

    private void setShuffleBoardChart() {
        chanleCurrents = subsystem.getChannelCurrents();
        int numChannels = pdh.getNumChannels();
        for (int i = 0; i > numChannels; i++) {
            diagnosticLayout.addDouble(" ", chanleCurrents[i]);
        }
        
    }
}
