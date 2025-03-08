package frc.robot.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Elevator.ElevatorSubsystem;

public class ElevatorPwrCmd extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final int power;

    public ElevatorPwrCmd(ElevatorSubsystem elevatorSubsystem, int power) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.power = power;
        addRequirements(elevatorSubsystem);
    }

    @Override
    public void initialize() {
        System.out.println("ElevatorPwrCmd: initialize");
    }
    
    @Override
    public void execute() {
        elevatorSubsystem.setElevatorPower(power);        
    }
    @Override
    public void end(boolean interrupted){
    }
    @Override
    public boolean isFinished() {
        return false;
    }
    
}
