package frc.robot.diagnostics;

import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardLayout;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.Commands;
import frc.robot.shuffleboard.Cat5Shuffleboard;
import frc.robot.subsystems.Gripper;
import frc.robot.subsystems.Gripper.IntakeState;

public class GripperDiagnostics extends Diagnostics<Gripper>{
    
    ShuffleboardLayout diagnosticLayout = Cat5Shuffleboard.createDiagnosticLayout("Gripper")
            .withSize(2, 4);
    
    public GripperDiagnostics(Gripper subsystem) {
        super(subsystem);

        diagnosticLayout.add("Test Intake", testGripper());

        
    }
 
    public CommandBase testGripper() {
        return Commands.runOnce(() -> subsystem.setState(IntakeState.Grab), subsystem);
    }
}
