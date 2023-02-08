package frc.robot.subsystems;

import edu.wpi.first.hal.PowerDistributionFaults;
import edu.wpi.first.hal.PowerDistributionStickyFaults;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.PowerDistribution.ModuleType;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Constants;

public class PowerDistributionData extends SubsystemBase {
    // Devices
    PowerDistribution PDH = new PowerDistribution(Constants.PowerDistributionConstants.PDHCanID, ModuleType.kRev);

    //Get Data
    public double getPDHTemperature(){
        return PDH.getTemperature();
    }

    public double getInputVoltage(){
        return PDH.getVoltage();
    }

    public double[] getChannelVoltageOutputs(){
       int numChannels = PDH.getNumChannels();
        double[] voltages = new double[numChannels];
        for (int i = 0; i < numChannels; i++){
            voltages[i] = PDH.getCurrent(i);
        }
        return voltages;
    }

    public double getChannelVoltageOutputs(int channel){
        return PDH.getCurrent(channel);
    }

    public PowerDistributionFaults getFaults(){
        return PDH.getFaults();
    }

    public PowerDistributionStickyFaults getStickyFaults(){
        return PDH.getStickyFaults();
    }


}