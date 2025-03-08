package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.Climber.ClimberSubsystem;

public class ClimberCmd extends Command {
    ClimberSubsystem climber;
    double pwr;

    public ClimberCmd(ClimberSubsystem climber, double pwr) {
        this.climber = climber;
        this.pwr = pwr;
    }

    @Override
    public void initialize() {
        System.out.println("Climber initialised");
    }

    @Override
    public void execute() {
        climber.runClimber(pwr);
    }

    @Override
    public void end(boolean interrupted) {
        climber.runClimber(0);
        System.out.println("Climber ended");
    }

    @Override
    public boolean isFinished() {
        return false;
    }
}
