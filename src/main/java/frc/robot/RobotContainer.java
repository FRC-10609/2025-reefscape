// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.Elevator.ElevatorSubsystem;
import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.Constants; 
import frc.robot.Constants.OperatorConstants;
import frc.robot.commands.DriverCommands;
import frc.robot.commands.ElevatorPwrCmd;
import frc.robot.commands.ResetGyro;
import frc.robot.commands.StopDriveMotors;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;
import frc.robot.commands.ElevatorSetPositionCmd;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a
 * "declarative" paradigm, very little robot logic should actually be handled in the {@link Robot}
 * periodic methods (other than the scheduler calls). Instead, the structure of the robot (including
 * subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer {
  // The robot's subsystems and commands are defined here...
  public final DriveSubsystem driveSubsystem = new DriveSubsystem();
  public final ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
  //private final PdpSubsystem pdpSubsystem = new PdpSubsystem();
  
  //Needed to invoke scheduler
  //public final Vision visionSubsystem = new Vision();

  private final SendableChooser<Command> autoChooser; 


  // The driver's controller


  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    // Configure the trigger bindings

    configureBindings();
    
    //Register named commands. Must register all commands we want Pathplanner to execute.
    NamedCommands.registerCommand("Stop Drive Motors", new StopDriveMotors(driveSubsystem));
  
    //Build an Autochooser from SmartDashboard selection.  Default will be Commands.none()
    
    //e.g new PathPlannerAuto("MiddleAutoAMPFinal");
    new PathPlannerAuto("Example Auto");

    autoChooser = AutoBuilder.buildAutoChooser();
    SmartDashboard.putData("Auto Chooser", autoChooser);
  }
  
  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary
   * predicate, or via the named factories in {@link
   * edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for {@link
   * CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller
   * PS4} controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight
   * joysticks}.
   */
  private void configureBindings() {
    //Drivetrain
    driveSubsystem.setDefaultCommand(new DriverCommands(driveSubsystem, new MockDetector())); //USES THE RIGHT BUMPER TO SLOW DOWN 

    Driver.Controller.leftTrigger(0.1).whileTrue(new ElevatorPwrCmd(elevatorSubsystem, () -> Driver.getLeftTrigger()));

    Driver.Controller.rightTrigger(0.1).whileTrue(new ElevatorPwrCmd(elevatorSubsystem, () -> -Driver.getRightTrigger()));

    // new Trigger(Driver.Controller.a()).onTrue(new ElevatorCommand(elevatorSubsystem, () -> 0.5, 1));

    // new Trigger(Driver.Controller.b()).onTrue(new ElevatorCommand(elevatorSubsystem, () -> 0.5, 2));
    
    // new Trigger(Driver.Controller.y()).onTrue(new ElevatorCommand(elevatorSubsystem, () -> 0.5, 3));
    
    // new Trigger(Driver.Controller.x()).onTrue(new ElevatorCommand(elevatorSubsystem, () -> 0.5, 4));
    
    Driver.Controller.start().onTrue(new ResetGyro(driveSubsystem));

    Driver.Controller.a().onTrue(new ElevatorSetPositionCmd(elevatorSubsystem, 0));
    Driver.Controller.b().onTrue(new ElevatorSetPositionCmd(elevatorSubsystem, 1));
    Driver.Controller.x().onTrue(new ElevatorSetPositionCmd(elevatorSubsystem, 2));
    Driver.Controller.y().onTrue(new ElevatorSetPositionCmd(elevatorSubsystem, 3));
    Driver.Controller.leftBumper().onTrue(new ElevatorSetPositionCmd(elevatorSubsystem, 4));


    //SysID stuff - comment out on competition build!
    // Driver.Controller.y().whileTrue(driveSubsystem.sysIdQuasistatic(SysIdRoutine.Direction.kForward));
    // Driver.Controller.a().whileTrue(driveSubsystem.sysIdQuasistatic(SysIdRoutine.Direction.kReverse));
    // Driver.Controller.b().whileTrue(driveSubsystem.sysIdDynamic(SysIdRoutine.Direction.kForward));
    // Driver.Controller.x().whileTrue(driveSubsystem.sysIdDynamic(SysIdRoutine.Direction.kReverse));

    /* sample code
    // Schedule `ExampleCommand` when `exampleCondition` changes to `true`
    new Trigger(m_exampleSubsystem::exampleCondition)
        .onTrue(new ExampleCommand(m_exampleSubsystem));

    // Schedule `exampleMethodCommand` when the Xbox controller's B button is pressed,
    // cancelling on release.
    m_driverController.b().whileTrue(m_exampleSubsystem.exampleMethodCommand());
    */
  }

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */
  public Command getAutonomousCommand() {
    // An example command will be run in autonomous
    return autoChooser.getSelected();
  }
}
