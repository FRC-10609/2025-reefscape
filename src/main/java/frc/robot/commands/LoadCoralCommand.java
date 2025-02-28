// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.
package frc.robot.commands;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.CoralDelivery.CoralDeliverySubsystem;

public class LoadCoralCommand extends Command{
    // @SuppressWarnings({"PMD.UnusedPrivateField", "PMD.SingularField"})
    private CoralDeliverySubsystem coraldelivery;
    
    public LoadCoralCommand(CoralDeliverySubsystem coraldelivery) {
        this.coraldelivery = coraldelivery;
        addRequirements(coraldelivery);
    }


  /**
   * Creates a new ExampleCommand.
   *
   * @param subsystem The subsystem used by this command.
   */



  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    coraldelivery.setDeliveryPower(0.5);
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    coraldelivery.setDeliveryPower(0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return false;
  }

}
