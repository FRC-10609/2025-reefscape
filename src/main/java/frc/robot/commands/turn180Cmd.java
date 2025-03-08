package frc.robot.commands;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;

public class turn180Cmd extends Command {
    DriveSubsystem m_driveSubsystem;

    public turn180Cmd(DriveSubsystem driveSubsystem) {
        m_driveSubsystem = driveSubsystem;
        addRequirements(m_driveSubsystem);
    }

    @Override
    public void initialize() {
        System.out.println("Turn 180 initialised");
    }

    @Override
    public void execute() {
        double[] speeds = {0.5, 0.5, 0.5, 0.5};
        double[] angles = {45, 45, 45, 45};
        SwerveModuleState[] states = m_driveSubsystem.makeSwerveModuleState(speeds, angles);
        m_driveSubsystem.setDesiredState(states);
    }
    
    @Override 
    public void end(boolean interrupted) {
        double[] speeds = {0, 0, 0, 0};
        double[] angles = {45, 45, 45, 45};
        SwerveModuleState[] states = m_driveSubsystem.makeSwerveModuleState(speeds, angles);
        m_driveSubsystem.setDesiredState(states);
        System.out.println("Turn 180 ended");
    }

    @Override
    public boolean isFinished() {
        return m_driveSubsystem.getIMU_Yaw() >= 0.5;
    }
}
