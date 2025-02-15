package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CoralDelivery.ElevatorCfg;
import frc.robot.subsystems.CoralDelivery.ElevatorSubsystem;


public class ElevatorSetPositionCmd extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final int level;

    public ElevatorSetPositionCmd(ElevatorSubsystem elevatorSubsystem, int level) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.level = level;
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void initialize() {
        System.out.println("ElevatorSetPositionCmd: initialize");
    }
    
    @Override
    public void execute() {
        elevatorSubsystem.setElevatorLevel(level);        
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
