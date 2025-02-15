// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.CorallIntake;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.EncoderConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Logger;

public class CoralDeliverySubsystem extends SubsystemBase {
  /** Creates a new CoralDSubsystem. */
  private final SparkMax pivot;
  private final SparkMax delivery;

  private RelativeEncoder pivotEncoder;
  private RelativeEncoder deliveryEncoder;
  
  private SparkClosedLoopController pivotPIDController;

  private double pivotSetPosition = 0;

  private double pivot_p_gain = CoralIntakeCfg.PIVOT_P_GAIN;
  private double pivot_i_gain = CoralIntakeCfg.PIVOT_I_GAIN;
  private double pivot_d_gain = CoralIntakeCfg.PIVOT_D_GAIN;

  private double pivot_p_gain_prev = CoralIntakeCfg.PIVOT_P_GAIN;
  private double pivot_i_gain_prev = CoralIntakeCfg.PIVOT_I_GAIN;
  private double pivot_d_gain_prev = CoralIntakeCfg.PIVOT_D_GAIN;

  private EncoderConfig pivotEncoderConfig = new EncoderConfig();
  private ClosedLoopConfig pivotPID_Config = new ClosedLoopConfig();
  private SparkMaxConfig pivotConfig = new SparkMaxConfig();

  private enum CoralDeliveryState{
    INIT,
    UNLOADED,
    LOADING_FROM_INDEX1,
    LOADING_FROM_INDEX2,
    LOADED,
    UNLOADING
  }

  CoralDeliveryState deliveryState = CoralDeliveryState.INIT;

  public CoralDeliverySubsystem() {
    pivot = CoralIntakeCfg.PIVOT_MOTOR;
    delivery = CoralIntakeCfg.DELIVERY_MOTOR;

    //Configure the pivot controller
    configureCoralPivot();

    //Configure the delivery controller (and distance sensors)
    configureCoralDelivery();
    
    SmartDashboard.putNumber("Pivot P Gain", pivot_p_gain);
    SmartDashboard.putNumber("Pivot I Gain", pivot_i_gain);
    SmartDashboard.putNumber("Pivot D Gain", pivot_d_gain);

    reset();
    registerLoggerObjects();
  }

  private void updateDashboard(){
    boolean pivotCfgChanged = false;
    
    pivot_p_gain = SmartDashboard.getNumber("Pivot P Gain", 0);
    pivot_i_gain = SmartDashboard.getNumber("Pivot I Gain", 0);
    pivot_p_gain = SmartDashboard.getNumber("Pivot D Gain", 0);

    if(pivot_p_gain != pivot_p_gain_prev){pivotPID_Config.p(pivot_p_gain); pivotCfgChanged = true;}
    if(pivot_i_gain != pivot_i_gain_prev){pivotPID_Config.i(pivot_i_gain); pivotCfgChanged = true;}
    if(pivot_d_gain != pivot_d_gain_prev){pivotPID_Config.d(pivot_i_gain); pivotCfgChanged = true;}

    if(pivotCfgChanged){
    //pivotConfig.apply(elevatorPID_Config);
    //pivot.configure(elevatorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
    //elevatorCfgChanged = false;
    }

    SmartDashboard.putNumber("PIVOT_POS", getPivotPosition());
    SmartDashboard.putNumber("PivotSetPosition", pivotSetPosition);

    SmartDashboard.putNumber("PIVOT_CURRENT", pivot.getOutputCurrent());
    SmartDashboard.putString("Delivery State", deliveryState.name());
  }


  private void configureCoralPivot(){
    //Setup the Pivot motor config
    pivotEncoder = pivot.getEncoder();
    
    pivotEncoderConfig.positionConversionFactor(CoralIntakeCfg.PIVOT_ANGLE_CONVERSION_DEG);
    pivotEncoderConfig.velocityConversionFactor(CoralIntakeCfg.PIVOT_ANGLE_CONVERSION_DEG);

    pivotPIDController = pivot.getClosedLoopController();
    pivotPID_Config.p(CoralIntakeCfg.PIVOT_P_GAIN);
    pivotPID_Config.i(CoralIntakeCfg.PIVOT_I_GAIN);
    pivotPID_Config.d(CoralIntakeCfg.PIVOT_D_GAIN);
    pivotPID_Config.outputRange(-.25,0.4);

    pivotConfig.idleMode(CoralIntakeCfg.PIVOT_IDLE_MODE);
    pivotConfig.inverted(CoralIntakeCfg.PIVOT_MOTOR_REVERSED);
    pivotConfig.smartCurrentLimit(CoralIntakeCfg.PIVOT_CURRENT_LIMIT);

    //Apply the encoder and PID configs on Spark config
    pivotConfig.apply(pivotEncoderConfig);
    pivotConfig.apply(pivotPID_Config);

    //Finally write the config to the spark
    pivot.configure(pivotConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
  }

  private void configureCoralDelivery(){
    deliveryEncoder = delivery.getEncoder();
    EncoderConfig deliveryEncoderConfig = new EncoderConfig();
    deliveryEncoderConfig.positionConversionFactor(CoralIntakeCfg.DELIVERY_GEAR_RATIO);
    deliveryEncoderConfig.velocityConversionFactor(CoralIntakeCfg.DELIVERY_GEAR_RATIO);

    SparkMaxConfig deliveryConfig = new SparkMaxConfig();
    deliveryConfig.idleMode(CoralIntakeCfg.DELIVERY_IDLE_MODE);
    deliveryConfig.inverted(CoralIntakeCfg.DELIVERY_MOTOR_REVERSED);
    deliveryConfig.smartCurrentLimit(CoralIntakeCfg.DELIVERY_CURRENT_LIMIT);

    deliveryConfig.apply(deliveryEncoderConfig);
  }

  private void registerLoggerObjects(){
    Logger.RegisterSparkMax("Coral Pivot", CoralIntakeCfg.PIVOT_MOTOR);
    Logger.RegisterSparkMax("Coral Delivery", CoralIntakeCfg.DELIVERY_MOTOR);
  }

  private void reset(){
    pivotEncoder.setPosition(0);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    setPivotPosition(pivotSetPosition);
    updateDashboard();
  }

  
 
  public void setPivotPower(double power){
    pivot.set(power);
  }

  public void setDeliveryPower(double power){
    delivery.set(power);
  }
 
  public double getPivotPosition(){
    return pivotEncoder.getPosition();
  }

  public double getDeliveryPosition(){
    return deliveryEncoder.getPosition();
  }

  
  public void setPivotPosition(double position){
    pivotPIDController.setReference(position, SparkMax.ControlType.kPosition);
  }

  public void setPivotDown(){//TODO: Change to "setPivotLoadPosition"
    System.out.println("Pivot Down");
    pivotSetPosition = 0;//Use constant values from CoralIntakeCfg instead of "magic numbers"
  }

  
  public void setPivotOn(){
    setPivotPower(1);
  }

  public void setPivotOff(){
    setPivotPower(0);
  }
}