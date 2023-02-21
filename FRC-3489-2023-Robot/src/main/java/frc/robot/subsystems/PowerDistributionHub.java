package frc.robot.subsystems;

import edu.wpi.first.hal.PowerDistributionFaults;
import edu.wpi.first.hal.PowerDistributionStickyFaults;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants.PowerDistributionHubConstants;

public class PowerDistributionHub extends SubsystemBase {
    public final PowerDistribution pdh = new PowerDistribution(PowerDistributionHubConstants.Module, ModuleType.kRev);

    public double getTemperature() {
        return pdh.getTemperature();
    }

    public double getInputVoltage() {
        return pdh.getVoltage();
    }

    public double[] getChannelCurrents() {
        int numChannels = pdh.getNumChannels();
        double[] currents = new double[numChannels];
        for (int i = 0; i < numChannels; i++) {
            currents[i] = pdh.getCurrent(i);
        }
        return currents;
    }

    public double getChannelCurrent(int channel) {
        return pdh.getCurrent(channel);
    }

    public PowerDistributionFaults getFaults() {
        return pdh.getFaults();
    }

    public PowerDistributionStickyFaults getStickyFaults() {
        return pdh.getStickyFaults();
    }
}