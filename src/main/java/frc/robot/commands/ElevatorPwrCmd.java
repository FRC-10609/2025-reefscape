package frc.robot.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Elevator.ElevatorSubsystem;

public class ElevatorPwrCmd extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final Supplier<Double> power;

    public ElevatorPwrCmd(ElevatorSubsystem elevatorSubsystem, Supplier<Double> power) {
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
        elevatorSubsystem.setElevatorPower(power.get());        
    }
    @Override
    public void end(boolean interrupted){
        elevatorSubsystem.setElevatorPower(0);
    }
    @Override
    public boolean isFinished() {
        return false;
    }
    
}
