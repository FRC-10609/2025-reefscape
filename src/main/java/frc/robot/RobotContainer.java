// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import frc.robot.subsystems.Algae.AlgaeSubsystem;
import frc.robot.subsystems.Climber.ClimberCfg;
import frc.robot.subsystems.Climber.ClimberSubsystem;
import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.commands.AlgaeCmd;
import frc.robot.commands.ClimberCmd;
import frc.robot.commands.DriverCommands;
import frc.robot.commands.ResetGyro;
import frc.robot.commands.StopDriveMotors;
import frc.robot.commands.turn180Cmd;
import frc.robot.commands.ClimberCmd;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;
import com.pathplanner.lib.commands.PathPlannerAuto;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.DriverCommands;
import frc.robot.commands.ElevatorPwrCmd;
import frc.robot.commands.ElevatorSetPositionCmd;
import frc.robot.commands.ResetGyro;
import frc.robot.commands.StopDriveMotors;
import frc.robot.commands.DeliverCoralCommand;
import frc.robot.commands.LoadCoralCommand;
import frc.robot.commands.PivotSetPositionCmd;
import frc.robot.subsystems.Elevator.ElevatorSubsystem;
import frc.robot.subsystems.PowerManagement.MockDetector;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;
import frc.robot.subsystems.CoralDelivery.CoralDeliverySubsystem;
import frc.robot.subsystems.Climber.ClimberCfg;;

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
  public final CoralDeliverySubsystem coralDelivery = new CoralDeliverySubsystem();
  public final AlgaeSubsystem algaeSubsystem = new AlgaeSubsystem();
  public final ClimberSubsystem climberSubsystem = new ClimberSubsystem();

  double heading;

  public ParallelCommandGroup l0CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 0), new PivotSetPositionCmd(coralDelivery, 0));
  public ParallelCommandGroup l1CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 1), new PivotSetPositionCmd(coralDelivery, 1));
  public ParallelCommandGroup l2CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 2), new PivotSetPositionCmd(coralDelivery, 2));
  public ParallelCommandGroup l3CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 3), new PivotSetPositionCmd(coralDelivery, 3));
  public ParallelCommandGroup l4CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 4), new PivotSetPositionCmd(coralDelivery, 4));
  public ParallelCommandGroup algaeCommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 5), new PivotSetPositionCmd(coralDelivery, 5));
  public ParallelCommandGroup cruisingCommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 0), new PivotSetPositionCmd(coralDelivery, 6));
  public ParallelCommandGroup algaeCommandGroup2 = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 6), new PivotSetPositionCmd(coralDelivery, 5));
  
  //private final PdpSubsystem pdpSubsystem = new PdpSubsystem();
  
  //Needed to invoke scheduler
  //public final Vision visionSubsystem = new Vision();
  
  private final SendableChooser<Command> autoChooser; 
  
  
  // The driver's controller
  
  
  
  /** The container for the robot. Contains subsystems, OI devices, and commands. */
  public RobotContainer() {
    
      NamedCommands.registerCommand("L1 Position", l1CommandGroup);
      NamedCommands.registerCommand("L2 Position", l2CommandGroup);
      NamedCommands.registerCommand("L3 Position", l3CommandGroup);
      NamedCommands.registerCommand("L4 Position", l4CommandGroup);
      NamedCommands.registerCommand("Loading Position", l0CommandGroup);
      NamedCommands.registerCommand("Cruising Position", cruisingCommandGroup);
      NamedCommands.registerCommand("Load Coral", new LoadCoralCommand(coralDelivery));
      NamedCommands.registerCommand("Deliver Coral", new DeliverCoralCommand(coralDelivery));
    // Configure the trigger bindings

    configureBindings();
    
    //Register named commands. Must register all commands we want Pathplanner to execute.
    NamedCommands.registerCommand("Stop Drive Motors", new StopDriveMotors(driveSubsystem));
  
    //Build an Autochooser from SmartDashboard selection.  Default will be Commands.none()
    
    //e.g new PathPlannerAuto("MiddleAutoAMPFinal");
    new PathPlannerAuto("Left");
    new PathPlannerAuto("Right");
    new PathPlannerAuto("Mid");


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
    ParallelCommandGroup l0CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 0), new PivotSetPositionCmd(coralDelivery, 0));
    ParallelCommandGroup l1CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 1), new PivotSetPositionCmd(coralDelivery, 1));
    ParallelCommandGroup l2CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 2), new PivotSetPositionCmd(coralDelivery, 2));
    ParallelCommandGroup l3CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 3), new PivotSetPositionCmd(coralDelivery, 3));
    ParallelCommandGroup l4CommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 4), new PivotSetPositionCmd(coralDelivery, 4));
    ParallelCommandGroup algaeCommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 5), new PivotSetPositionCmd(coralDelivery, 5));
    ParallelCommandGroup cruisingCommandGroup = new ParallelCommandGroup(new ElevatorSetPositionCmd(elevatorSubsystem, 0), new PivotSetPositionCmd(coralDelivery, 6));

    //Drivetrain
    driveSubsystem.setDefaultCommand(new DriverCommands(driveSubsystem, new MockDetector())); //USES THE RIGHT BUMPER TO SLOW DOWN 

    // Driver.Controller.leftTrigger(0.1).whileTrue(new ElevatorPwrCmd(elevatorSubsystem, () -> Driver.getLeftTrigger()));

    // Driver.Controller.rightTrigger(0.1).whileTrue(new ElevatorPwrCmd(elevatorSubsystem, () -> -Driver.getRightTrigger()));

    // new Trigger(Driver.Controller.a()).onTrue(new ElevatorCommand(elevatorSubsystem, () -> 0.5, 1));

    // new Trigger(Driver.Controller.b()).onTrue(new ElevatorCommand(elevatorSubsystem, () -> 0.5, 2));
    
    // new Trigger(Driver.Controller.y()).onTrue(new ElevatorCommand(elevatorSubsystem, () -> 0.5, 3));
    
    // new Trigger(Driver.Controller.x()).onTrue(new ElevatorCommand(elevatorSubsystem, () -> 0.5, 4));
    // driveSubsystem.setDefaultCommand(new DriverCommands(driveSubsystem, new MockDetector())); //USES THE LEFT BUMPER TO SLOW DOWN
    
    Driver.Controller.x().onTrue(new ResetGyro(driveSubsystem));



    Operator.Controller.a().onTrue(l0CommandGroup);
    Operator.Controller.b().onTrue(new AlgaeCmd(algaeSubsystem, 1));
    Operator.Controller.x().onTrue(l2CommandGroup);
    Operator.Controller.y().onTrue(l3CommandGroup);
    Operator.Controller.leftBumper().onTrue(l4CommandGroup);
    // Operator.Controller.rightTrigger().onTrue(algaeCommandGroup);
    Operator.Controller.rightBumper().onTrue(new AlgaeCmd(algaeSubsystem, 0));
    Operator.Controller.start().onTrue(cruisingCommandGroup);
    Operator.Controller.povLeft().whileTrue(new ClimberCmd(climberSubsystem, ClimberCfg.CLIMBER_SPEED_DOWN));
    Operator.Controller.povLeft().whileTrue(new ClimberCmd(climberSubsystem, ClimberCfg.CLIMBER_SPEED_UP));

    Operator.Controller.povUp().whileTrue(new ElevatorPwrCmd(elevatorSubsystem, 1));
    Operator.Controller.povDown().whileTrue(new ElevatorPwrCmd(elevatorSubsystem, -1));
    Operator.Controller.rightTrigger().onTrue(new PivotSetPositionCmd(coralDelivery, 5));


    Driver.Controller.povUp().whileTrue(new LoadCoralCommand(coralDelivery));
    Driver.Controller.povDown().whileTrue(new DeliverCoralCommand(coralDelivery));
    //SysID stuff - comment out on competition build!
    //Driver.Controller.y().whileTrue(driveSubsystem.sysIdQuasistatic(SysIdRoutine.Direction.kForward));
    //Driver.Controller.a().whileTrue(driveSubsystem.sysIdQuasistatic(SysIdRoutine.Direction.kReverse));
    //Driver.Controller.b().whileTrue(driveSubsystem.sysIdDynamic(SysIdRoutine.Direction.kForward));
    //Driver.Controller.x().whileTrue(driveSubsystem.sysIdDynamic(SysIdRoutine.Direction.kReverse));

    Driver.Controller.povLeft().whileTrue(new AlgaeCmd(algaeSubsystem, 1));
    Driver.Controller.povRight().whileTrue(new AlgaeCmd(algaeSubsystem, -1));
    Driver.Controller.b().onTrue(new SequentialCommandGroup(new InstantCommand(() -> driveSubsystem.resetGyro(0)), new turn180Cmd(driveSubsystem)));

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
