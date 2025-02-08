package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Climber.ClimberSubsystem;
import java.util.function.Supplier;

public class ElevatorCommand extends Command {
    ClimberSubsystem climber;
    Supplier<Double> pwr;
    public ElevatorCommand(ClimberSubsystem climber, Supplier<Double> pwr){
    this.climber = climber;
    this.pwr = pwr;
    addRequirements(climber);
   }
   @Override
   public void initialize(){System.out.println("Climber Command Initialized");}

   @Override
   public void execute(){
       climber.setClimberPower(pwr.get());
   }
   @Override
   public void end(boolean interrupted){
       System.out.println("Climber Command Ended");
   }

   @Override 
   public boolean isFinished(){return false;}
}

