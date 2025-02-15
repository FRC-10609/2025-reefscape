package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Algae.AlgaeSubsystem;

public class AlgaeIntakeCmd extends Command {
    AlgaeSubsystem algaeSubsystem;
    int in;

    public AlgaeIntakeCmd(AlgaeSubsystem algaeSubsystem, int in) {
        this.algaeSubsystem = algaeSubsystem;
        this.in = in;
    }

    @Override 
    public void initialize(){
        System.out.println("Algae Intake Command Initialized");
    }

    @Override
    public void execute(){
        algaeSubsystem.inTake(in);
    }

    @Override
    public void end(boolean interrupted){
        algaeSubsystem.inTake(0);
    }

    @Override
    public boolean isFinished(){
        return false;
    }
}
