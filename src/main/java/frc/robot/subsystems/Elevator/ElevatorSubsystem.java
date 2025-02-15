// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Elevator;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.EncoderConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

// import au.grapplerobotics.LaserCan;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Logger;

public class ElevatorSubsystem extends SubsystemBase {
  /** Creates a new CoralDSubsystem. */
  private final SparkMax elevator;
  // private final SparkMax pivot;
  // private final SparkMax delivery;

  private RelativeEncoder elevatorEncoder;
  // private RelativeEncoder pivotEncoder;
  // private RelativeEncoder deliveryEncoder;
  
  // private SparkClosedLoopController pivotPIDController;
  private SparkClosedLoopController elevatorPIDController;

  // private LaserCan fwdCoralDeliveryTracker;//Change this name and duplicate for the 2nd sensor
  // private LaserCan rwdCoralDeliveryTracker;
  private double elevatorSetPosition = 0;
  // private double pivotSetPosition = 0;

  private double elevator_p_gain = ElevatorCfg.ELEVATOR_P_GAIN;
  private double elevator_i_gain = ElevatorCfg.ELEVATOR_I_GAIN;
  private double elevator_d_gain = ElevatorCfg.ELEVATOR_D_GAIN;

  private double elevator_p_gain_prev = ElevatorCfg.ELEVATOR_P_GAIN;
  private double elevator_i_gain_prev = ElevatorCfg.ELEVATOR_I_GAIN;
  private double elevator_d_gain_prev = ElevatorCfg.ELEVATOR_D_GAIN;

  // private double pivot_p_gain = ElevatorCfg.PIVOT_P_GAIN;
  // private double pivot_i_gain = ElevatorCfg.PIVOT_I_GAIN;
  // private double pivot_d_gain = ElevatorCfg.PIVOT_D_GAIN;

  // private double pivot_p_gain_prev = ElevatorCfg.PIVOT_P_GAIN;
  // private double pivot_i_gain_prev = ElevatorCfg.PIVOT_I_GAIN;
  // private double pivot_d_gain_prev = ElevatorCfg.PIVOT_D_GAIN;

  private EncoderConfig elevatorEncoderConfig = new EncoderConfig();
  private ClosedLoopConfig elevatorPID_Config = new ClosedLoopConfig();
  private SparkMaxConfig elevatorConfig = new SparkMaxConfig();

  // private EncoderConfig pivotEncoderConfig = new EncoderConfig();
  // private ClosedLoopConfig pivotPID_Config = new ClosedLoopConfig();
  // private SparkMaxConfig pivotConfig = new SparkMaxConfig();

  // private enum CoralDeliveryState{
  //   INIT,
  //   UNLOADED,
  //   LOADING_FROM_INDEX1,
  //   LOADING_FROM_INDEX2,
  //   LOADED,
  //   UNLOADING
  // }

  // CoralDeliveryState deliveryState = CoralDeliveryState.INIT;

  public ElevatorSubsystem() {
    elevator = ElevatorCfg.ELEVATOR_MOTOR;

    //Configure the elevator controller
    configureElevator();

    //Configure the pivot controller
    // configureCoralPivot();

    //Configure the delivery controller (and distance sensors)
    // configureCoralDelivery();
    
    SmartDashboard.putNumber("Elevator P Gain", elevator_p_gain);
    SmartDashboard.putNumber("Elevator I Gain", elevator_i_gain);
    SmartDashboard.putNumber("Elevator D Gain", elevator_d_gain);



    reset();
    registerLoggerObjects();
  }

  private void updateDashboard(){
    boolean elevatorCfgChanged = false;
    // boolean pivotCfgChanged = false;
    
    elevator_p_gain = SmartDashboard.getNumber("Elevator P Gain",0);
    elevator_i_gain = SmartDashboard.getNumber("Elevator I Gain",0);
    elevator_d_gain = SmartDashboard.getNumber("Elevator D Gain",0);

    if(elevator_p_gain != elevator_p_gain_prev){elevatorPID_Config.p(elevator_p_gain); elevatorCfgChanged = true;}
    if(elevator_i_gain != elevator_i_gain_prev){elevatorPID_Config.i(elevator_i_gain); elevatorCfgChanged = true;}
    if(elevator_d_gain != elevator_d_gain_prev){elevatorPID_Config.d(elevator_i_gain); elevatorCfgChanged = true;}

    if(elevatorCfgChanged){
      elevatorConfig.apply(elevatorPID_Config);
      elevator.configure(elevatorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
      elevatorCfgChanged = false;
    }



    SmartDashboard.putNumber("ELEVATOR_CURRENT", elevator.getOutputCurrent());
    SmartDashboard.putNumber("ELEVATOR_POS", getElevatorPosition());
    SmartDashboard.putNumber("ElevatorSetPosition", elevatorSetPosition);
  }

  private void configureElevator(){
    //Setup the Elevator motor config
    elevatorEncoder = elevator.getEncoder();
    elevatorEncoderConfig.positionConversionFactor(ElevatorCfg.ELEVATOR_POS_CONVERSION_CM);
    elevatorEncoderConfig.velocityConversionFactor(ElevatorCfg.ELEVATOR_POS_CONVERSION_CM);
    
    elevatorPIDController = elevator.getClosedLoopController();
    elevatorPID_Config.p(ElevatorCfg.ELEVATOR_P_GAIN);
    elevatorPID_Config.i(ElevatorCfg.ELEVATOR_I_GAIN);
    elevatorPID_Config.d(ElevatorCfg.ELEVATOR_D_GAIN);
    elevatorPID_Config.outputRange(-.25,0.75);
    
    elevatorConfig.idleMode(ElevatorCfg.ELEVATOR_IDLE_MODE);
    elevatorConfig.inverted(ElevatorCfg.ELEVATOR_MOTOR_REVERSED);
    elevatorConfig.smartCurrentLimit(ElevatorCfg.ELEVATOR_CURRENT_LIMIT);
    
    //Apply the encoder and PID configs to the Spark config
    elevatorConfig.apply(elevatorEncoderConfig);
    elevatorConfig.apply(elevatorPID_Config);
    
    //Finally write the config to the spark
    elevator.configure(elevatorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
  }

  private void registerLoggerObjects(){
    Logger.RegisterSparkMax("Elevator", ElevatorCfg.ELEVATOR_MOTOR);

  }

  private void reset(){
    elevatorEncoder.setPosition(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    // setElevatorPosition(elevatorSetPosition);
    updateDashboard();
  }


  public void setElevatorPower(double power){
    elevator.set(power);
  }

  
 
  public double getElevatorPosition(){
    return elevatorEncoder.getPosition();
  }


    
  public void setElevatorPosition(double position){
    elevatorPIDController.setReference(position, SparkMax.ControlType.kPosition);
  }

  public void setElevatorLoadPosition(){
    elevatorSetPosition = ElevatorCfg.ELEVATOR_LOAD_POSITION;
  }

  public void setElevatorLevel(int level){
    elevatorSetPosition = ElevatorCfg.ELEVATOR_POSITIONS[level];
  }

  public void setElevatorLONEPosition(){
    elevatorSetPosition = ElevatorCfg.ELEVATOR_LONE_POSITION; 
  }

  public void setElevatorLTWOPosition(){
    elevatorSetPosition = ElevatorCfg.ELEVATOR_LTWO_POSITION;
  }

  public void setElevatorLTHREEPosition(){
    elevatorSetPosition = ElevatorCfg.ELEVATOR_LTHREE_POSITION;
  }

  public void setElevatorLFOURPosition(){
    elevatorSetPosition = ElevatorCfg.ELEVATOR_LFOUR_POSITION;
  }

  public void setElevatorFwd(){
    setElevatorPower(1);
  }

  public void setElevatorRwd(){
    setElevatorPower(-0.5);
  }
  
  public void setElevatorOff(){
    setElevatorPower(0);
  }
  

}