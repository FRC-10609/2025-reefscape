package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Climber.ClimberSubsystem;
import java.util.function.Supplier;
import frc.robot.subsystems.Climber.ClimberCfg;

public class ElevatorCommand extends Command {
    ClimberSubsystem climber;
    Supplier<Double> pwr;
    int pos;
    public ElevatorCommand(ClimberSubsystem climber, Supplier<Double> pwr, int pos){
    this.climber = climber;
    this.pwr = pwr;
    this.pos = pos;
    addRequirements(climber);
   }
   @Override
   public void initialize(){System.out.println("Climber Command Initialized");}

   @Override
   public void execute(){
        if (ClimberCfg.CLIMBER_POSITIONS[pos] != null){
            climber.setClimberPosition(ClimberCfg.CLIMBER_POSITIONS[pos]);
        }else{
            climber.setClimberPower(pwr.get());
        }
   }
   @Override
   public void end(boolean interrupted){
       System.out.println("Climber Command Ended");
   }

   @Override 
   public boolean isFinished(){return false;}
}

