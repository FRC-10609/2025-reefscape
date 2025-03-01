package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CoralDelivery.CoralDeliverySubsystem;

public class PivotSetPositionCmd extends Command{
    private final CoralDeliverySubsystem pivotSubsystem;
    private final int presetNum;

    public PivotSetPositionCmd(CoralDeliverySubsystem pivotSubsystem, int level) {
        this.pivotSubsystem = pivotSubsystem;
        this.presetNum = level;
        addRequirements(pivotSubsystem);
    }

    @Override
    public void initialize() {
        System.out.println("ElevatorSetPositionCmd: initialize");
    }
    
    @Override
    public void execute() {
        pivotSubsystem.setPivotAnglePreset(presetNum);        
    }

    @Override
    public void end(boolean interrupted){
        System.out.println("ElevatorSetPositionCmd: end");
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
