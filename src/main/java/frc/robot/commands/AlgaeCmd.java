package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Algae.AlgaeSubsystem;

public class AlgaeCmd extends Command {
    AlgaeSubsystem algaeSubsystem;
    int pos;

    public AlgaeCmd(AlgaeSubsystem algaeSubsystem, int pos) {
        this.algaeSubsystem = algaeSubsystem;
        this.pos = pos;
    }

    @Override 
    public void initialize(){
        System.out.println("Algae Command Initialized");
    }

    @Override
    public void execute(){
        algaeSubsystem.setAlgaePosition(pos);
    }

    @Override
    public void end(boolean interrupted){
        System.out.print("Algae Command Ended");
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
