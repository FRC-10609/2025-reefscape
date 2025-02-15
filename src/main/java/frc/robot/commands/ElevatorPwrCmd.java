package frc.robot.commands;

import java.util.function.Supplier;

import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.CoralDelivery.ElevatorSubsystem;

public class ElevatorPwrCmd extends Command {
    private final ElevatorSubsystem elevatorSubsystem;
    private final double power;

    public ElevatorPwrCmd(ElevatorSubsystem elevatorSubsystem, Supplier<Double> power) {
        this.elevatorSubsystem = elevatorSubsystem;
        this.power = power.get();
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
    public boolean isFinished() {
        return true;
    }
    
}
