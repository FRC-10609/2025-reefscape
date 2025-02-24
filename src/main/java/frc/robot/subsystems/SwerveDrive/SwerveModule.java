package frc.robot.subsystems.SwerveDrive;

import static edu.wpi.first.units.Units.Meters;
import static edu.wpi.first.units.Units.MetersPerSecond;
import static edu.wpi.first.units.Units.Volts;

import com.ctre.phoenix6.configs.CANcoderConfiguration;
import com.ctre.phoenix6.hardware.CANcoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.config.*;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.units.measure.Distance;
import edu.wpi.first.units.measure.LinearVelocity;
import edu.wpi.first.units.measure.Voltage;

public class SwerveModule{
  private final SparkFlex driveMotor;
  private final SparkMax turningMotor;

  private final RelativeEncoder driveEncoder;

  private final CANcoder absEncoder;

  //REV Spark PIDF Controller
  private final SparkClosedLoopController drivePIDController;
  //WPI PID Controller
  private final PIDController turningPIDController = new PIDController(
                                                            SwerveModuleCfg.MODULE_TURN_PID_CONTROLLER_P, 
                                                            SwerveModuleCfg.MODULE_TURN_PID_CONTROLLER_I, 
                                                            SwerveModuleCfg.MODULE_TURN_PID_CONTROLLER_D);

  private final SimpleMotorFeedforward feedforward = new SimpleMotorFeedforward(
                                                            SwerveModuleCfg.DRIVE_MOTOR_KS,
                                                            SwerveModuleCfg.DRIVE_MOTOR_KV,
                                                            SwerveModuleCfg.DRIVE_MOTOR_KA);
  
  
  private double commandedSpeed;
  private double commandedAngle;

  public SwerveModule(int moduleId, SparkFlex driveMotor, SparkMax turnMotor, CANcoder absEncoder) {
    this.driveMotor = driveMotor;
    this.turningMotor = turnMotor;
    this.absEncoder = absEncoder;

    this.driveEncoder = driveMotor.getEncoder();

    //Setup Drive Encoder Config
    EncoderConfig driveEncoderConfig = new EncoderConfig();
    driveEncoderConfig.positionConversionFactor(SwerveModuleCfg.DRIVE_METERS_PER_ENCODER_REV);
    driveEncoderConfig.velocityConversionFactor(SwerveModuleCfg.DRIVE_ENCODER_MPS_PER_REV);

    //Setup Drive Motor Closed Loop Config settings
    ClosedLoopConfig drivePIDF_Config = new ClosedLoopConfig();
    drivePIDF_Config.p(SwerveModuleCfg.MODULE_DRIVE_PID_CONTROLLER_P);
    drivePIDF_Config.i(SwerveModuleCfg.MODULE_DRIVE_PID_CONTROLLER_I);
    drivePIDF_Config.d(SwerveModuleCfg.MODULE_DRIVE_PID_CONTROLLER_D);
    drivePIDF_Config.velocityFF(1/DrivebaseCfg.MAX_SPEED_METERS_PER_SECOND);


    //Setup Drive Motor Config
    SparkFlexConfig driveConfig = new SparkFlexConfig();
    driveConfig.idleMode(SwerveModuleCfg.DRIVE_IDLE_MODE);
    driveConfig.closedLoopRampRate(SwerveModuleCfg.CLSD_LOOP_RAMP_RATE_SECONDS);
    driveConfig.smartCurrentLimit(SwerveModuleCfg.DRIVE_CURRENT_LIMIT_AMPS);
    driveConfig.inverted(ChassisMotorCfg.DRIVE_MOTOR_REVERSED[moduleId]);
    
    //Apply Drive Encoder & Drive PID Configs to the Drive Config
    driveConfig.apply(driveEncoderConfig);
    driveConfig.apply(drivePIDF_Config);

    //Finally, write all the config settings to the drive controller!
    driveMotor.configure(driveConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);

    this.drivePIDController = driveMotor.getClosedLoopController();

    //Setup Turn Motor Config
    SparkMaxConfig turnConfig = new SparkMaxConfig();
    turnConfig.idleMode(SwerveModuleCfg.TURN_IDLE_MODE); 
    turnConfig.inverted(ChassisMotorCfg.ANGLE_MOTOR_REVERSED[moduleId]);

    //Finally, write all the config settings to the turn controller!
    turnMotor.configure(turnConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);

    //Set absolute encoder magnet configuration
    CANcoderConfiguration config = new CANcoderConfiguration();
    config.MagnetSensor.MagnetOffset = -CANCoderCfg.MAGNET_OFFSET[moduleId];
    config.MagnetSensor.SensorDirection = CANCoderCfg.SENSOR_DIRECTION[moduleId];
    config.MagnetSensor.AbsoluteSensorDiscontinuityPoint = CANCoderCfg.DISCONTINUITY_POINT;
    this.absEncoder.getConfigurator().apply(config);

    // Limit the Turning PID Controller's input range between -pi and pi and set the input
    // to be continuous.
    this.turningPIDController.enableContinuousInput(-Math.PI, Math.PI);
  }

  /**
   * Sets the desired state for the module
   *
   * @param desiredState Desired state with speed and angle.
   */
  public void setDesiredState(SwerveModuleState desiredState) {
    
    //Set SmartDashboard variables
    commandedSpeed = desiredState.speedMetersPerSecond;
    commandedAngle = desiredState.angle.getRadians();

    if(Math.abs(desiredState.speedMetersPerSecond) < .2){
      driveMotor.set(0);
      turningMotor.set(0);
      return;
    }else{
      // Optimize the reference state to avoid spinning further than 90 degrees
      var rotation = new Rotation2d(getAbsPositionZeroed());
      desiredState.optimize(rotation);
      
      // Scale speed by cosine of angle error. This scales down movement perpendicular to the desired
      // direction of travel that can occur when modules change directions. This results in smoother
      // driving.
      desiredState.cosineScale(rotation);

      //Set SmartDashboard variables
      commandedSpeed = desiredState.speedMetersPerSecond;
      commandedAngle = desiredState.angle.getRadians();

      //Calculate the motor speed output && feedforward and pass the values to the SPARK PID Controller object
      var desiredSpeed = desiredState.speedMetersPerSecond;
      drivePIDController.setReference(desiredSpeed, SparkMax.ControlType.kVelocity);

      // Calculate the turning motor output from the turning PID controller.
      final double turnOutput = turningPIDController.calculate(getAbsPositionZeroed(), desiredState.angle.getRadians());
      turningMotor.setVoltage(turnOutput);
    }
  }

  /**
   * Returns the current state of the module.
   *
   * @return The current state of the module.
   */
  public SwerveModuleState getState() {
    return new SwerveModuleState(driveEncoder.getVelocity(), new Rotation2d(getAbsPositionZeroed()));
  }

  public double getVelocity() {
    return driveEncoder.getVelocity();
  }

  /**
   * Returns the current position of the module.
   *
   * @return The current position of the module.
   */
  public SwerveModulePosition getPosition() {
    return new SwerveModulePosition(driveEncoder.getPosition(), new Rotation2d(getAbsPositionZeroed()));
  }

  public Distance getLinearPositionMeters() {
    return Distance.ofBaseUnits(driveEncoder.getPosition(), Meters);
  }

  public void zeroModule() {
    driveEncoder.setPosition(0);
  }

  public double getAbsPositionZeroed() {
    //CANcoders in Phoenix return rotations 0 to 1
    var angle = absEncoder.getAbsolutePosition();
    return angle.getValueAsDouble()*2.0*Math.PI;
  }

  public double getCommandedSpeed(){
    return commandedSpeed;
  }

  public LinearVelocity getModuleVelocityMetersPerSec(){
    return LinearVelocity.ofBaseUnits(driveEncoder.getVelocity(), MetersPerSecond);
  }

  public double getCommandedAngle(){
    return commandedAngle;
  }

  public void setSysIDVoltage(Voltage volts){
    //Set drive motor open-loop voltage
    driveMotor.setVoltage(volts.magnitude());
    
    // Calculate the turning motor output from the turning PID controller.  For SysID, all motors should be facing "forward"
    final double turnOutput = turningPIDController.calculate(getAbsPositionZeroed(), 0);
    turningMotor.setVoltage(turnOutput);
  }

  public Voltage getDriveMotorVoltage(){
    var busVoltage = Voltage.ofBaseUnits(driveMotor.getBusVoltage(), Volts);
    var appliedOutput = driveMotor.getAppliedOutput();
    return (busVoltage.times(appliedOutput));
  }

  public double getBusVoltage(){
    return driveMotor.getBusVoltage();
  }
}