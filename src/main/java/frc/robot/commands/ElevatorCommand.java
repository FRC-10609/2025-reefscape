package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;

import java.util.function.Supplier;

import frc.robot.Driver;
import frc.robot.subsystems.CoralDelivery.ElevatorCfg;
import frc.robot.subsystems.CoralDelivery.ElevatorSubsystem;

public class ElevatorCommand extends Command {
    ElevatorSubsystem climber;
    Supplier<Double> pwr;
    int pos;
    public ElevatorCommand(ElevatorSubsystem climber, Supplier<Double> pwr, int pos){
    this.climber = climber;
    this.pwr = pwr;
    this.pos = pos;
    addRequirements(climber);
   }
   @Override
   public void initialize(){System.out.println("Climber Command Initialized");}

   @Override
   public void execute(){
        if (ElevatorCfg.CLIMBER_POSITIONS[pos] != null){
            climber.setClimberPosition(ElevatorCfg.CLIMBER_POSITIONS[pos]);
        }else{
            climber.setClimberPower(pwr.get());
        }
   }
   @Override
   public void end(boolean interrupted){
       System.out.println("Climber Command Ended");
       climber.setClimberPower(0);
   }

   @Override 
   public boolean isFinished(){
    if (pos != 0) {
        return climber.isAtPosition(ElevatorCfg.CLIMBER_POSITIONS[pos]);
    }
    return false;
 }
    
}


