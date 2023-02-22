package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.constants.DrivetrainConstants;
import frc.robot.shuffleboard.Cat5Shuffleboard;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.PowerDistributionHub;

public class MotorCurrentDiagnostics extends Diagnostics<PowerDistributionHub> {
    private double[] chanleCurrents = subsystem.getChannelCurrents();
    private final PowerDistribution pdh = subsystem.pdh;

    private int numChannels = pdh.getNumChannels();
    private double currentVoltage;
    private int currentChannel;

    ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("Motor Voltage")
        .withSize(2, 4);

    public MotorCurrentDiagnostics(PowerDistributionHub subsystem) {
        super(subsystem);
        diagnosticLayout.add("Get Motor Current", GetMotorCommand());
    }

    private void setShuffleBoardChart() {
        for (int i = 0; i > numChannels; i++) {
            currentChannel = i;
            diagnosticLayout.addDouble(shuffleBoardChart(), () -> currentVoltage);
        }
    }

    private String shuffleBoardChart() {
        String motor = null;
        switch (currentChannel){
            case 1:
                motor = "Back Right Drive";
                currentVoltage = chanleCurrents[1];
                break;
            case 10:
                motor = "Right Gripper Motor";
                currentVoltage = chanleCurrents[10];
                break;
        }
        return motor;
    }

    private CommandBase GetMotorCommand() {
        return Commands.runOnce(() -> setShuffleBoardChart(), subsystem)
            .withName("Get Motor Current");
    }
}
