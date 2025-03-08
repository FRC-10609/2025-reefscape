package frc.robot.commands;

import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.Driver;
import frc.robot.subsystems.PowerManagement.AdaptiveSpeedController;
import frc.robot.subsystems.PowerManagement.IBrownOutDetector;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;
import frc.robot.subsystems.SwerveDrive.DrivebaseCfg;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriverCommands extends Command {
  private final DriveSubsystem drive;
  private final AdaptiveSpeedController speedController;

  private final String kDriver1 = "Driver1";
  private final String kDriver2 = "Driver2";

  private SlewRateLimiter turnLimiter = new SlewRateLimiter(7);
  private final SendableChooser<String> driverChooser = new SendableChooser<>();

  boolean finesse_mode = false;
  
  public DriverCommands(DriveSubsystem drive, IBrownOutDetector brownOutDetector) {
    this.drive = drive;
    this.speedController = new AdaptiveSpeedController(brownOutDetector, 3.0, DrivebaseCfg.FINESSE_TRANSLATION_GAIN, DrivebaseCfg.TRANSLATION_GAIN_1);
    addRequirements(drive);

    driverChooser.setDefaultOption("Default Driver", kDriver1);
    driverChooser.addOption("Driver 1", kDriver1);
    driverChooser.addOption("Driver 2", kDriver2);
    SmartDashboard.putData("Driver Chooser", driverChooser);
  }

  @Override
  public void initialize() {}

  @Override
  public void execute() {
    double teleopSpeedGain;
    double teleopRotationGain;
    double driver_gain;

    double forwardSpeed;
    double strafeSpeed;
    double rotationSpeed;

    String driver = (String) driverChooser.getSelected();

    switch(driver){
      case kDriver1:
      default:
        driver_gain = DrivebaseCfg.TRANSLATION_GAIN_1;
      case kDriver2:
        driver_gain = DrivebaseCfg.TRANSLATION_GAIN_2;
    }

    if(Driver.Controller.getHID().getRightBumperButton()){
      teleopSpeedGain = DrivebaseCfg.FINESSE_TRANSLATION_GAIN;
      teleopRotationGain = DrivebaseCfg.FINESSE_ROTATION_GAIN;
    }else{
      teleopSpeedGain = driver_gain;
      teleopRotationGain = DrivebaseCfg.ROTATION_GAIN;
    }
    //Need to convert joystick input (-1 to 1) into m/s!!! 100% == MAX Attainable Speed
    forwardSpeed = ((MathUtil.applyDeadband(Driver.getLeftY(), 0.03)) * teleopSpeedGain) *
        DrivebaseCfg.MAX_SPEED_METERS_PER_SECOND;

    strafeSpeed = ((MathUtil.applyDeadband(Driver.getLeftX(), 0.03)) * teleopSpeedGain) *
        DrivebaseCfg.MAX_SPEED_METERS_PER_SECOND;

    //Need to convert joystick input (-1 to 1) into rad/s!!! 100% == MAX Attainable Rotation
    /*rotationSpeed = turnLimiter.calculate(((MathUtil.applyDeadband(Driver.getRightX(), 0.1)) * 
        calculateDriveMagnitude(teleopRotationGain)) *
        DrivebaseCfg.MAX_ROTATION_RADIANS_PER_SECOND);*/
        
      rotationSpeed = ((MathUtil.applyDeadband(Driver.getRightX(), 0.03)) * 
        calculateDriveMagnitude(teleopRotationGain)) * DrivebaseCfg.MAX_ROTATION_RADIANS_PER_SECOND;

    SmartDashboard.putNumber("Forward In", forwardSpeed);
    SmartDashboard.putNumber("Strafe In", strafeSpeed);
    SmartDashboard.putNumber("Rotation In", rotationSpeed);

    if(DrivebaseCfg.ADAPTIVE_LIMITING_ENABLED){
      var speedCommand = speedController.GetSpeedCommand(
        forwardSpeed, // Forward
        strafeSpeed, // Strafe
        rotationSpeed, // Rotate
        Driver.Controller.leftBumper().getAsBoolean()); // brake
    
      drive.drive(-speedCommand.forwardSpeed, -speedCommand.strafeSpeed, -speedCommand.rotationSpeed, false);
    }else{
      drive.drive(-forwardSpeed, -strafeSpeed, -rotationSpeed, false);
    }

  }

  @Override
  public void end(boolean interrupted) {}

  @Override
  public boolean isFinished() {
    return false;
  }

  private double calculateDriveMagnitude(double rotation_gain){

    //Magnitude of the driver input is magnitude = sqrt(x^2 + y^2)
    double magnitude = Math.sqrt(Math.pow(Driver.getLeftX(),2) + Math.pow(Driver.getLeftY(),2));

    if(magnitude > 1.0){
      magnitude = 1.0;
    }else if (magnitude < rotation_gain){
      magnitude = rotation_gain;
    }else{
      //value is within range
    }

    return(magnitude);
  }
}