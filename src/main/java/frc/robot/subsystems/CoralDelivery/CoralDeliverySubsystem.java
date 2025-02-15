// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.CoralDelivery;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.EncoderConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.Logger;

public class CoralDeliverySubsystem extends SubsystemBase {
  /** Creates a new CoralDSubsystem. */
  private final SparkMax delivery;

  private RelativeEncoder deliveryEncoder;
  
  public CoralDeliverySubsystem() {
    delivery = CoralDeliveryCfg.DELIVERY_MOTOR;

    //Configure the delivery controller (and distance sensors)
    configureCoralDelivery();
    
    reset();
    registerLoggerObjects();
  }


  private void configureCoralDelivery(){
    deliveryEncoder = delivery.getEncoder();
    EncoderConfig deliveryEncoderConfig = new EncoderConfig();
    deliveryEncoderConfig.positionConversionFactor(CoralDeliveryCfg.DELIVERY_GEAR_RATIO);
    deliveryEncoderConfig.velocityConversionFactor(CoralDeliveryCfg.DELIVERY_GEAR_RATIO);

    SparkMaxConfig deliveryConfig = new SparkMaxConfig();
    deliveryConfig.idleMode(CoralDeliveryCfg.DELIVERY_IDLE_MODE);
    deliveryConfig.inverted(CoralDeliveryCfg.DELIVERY_MOTOR_REVERSED);
    deliveryConfig.smartCurrentLimit(CoralDeliveryCfg.DELIVERY_CURRENT_LIMIT);

    deliveryConfig.apply(deliveryEncoderConfig);
  }

  private void registerLoggerObjects(){
    Logger.RegisterSparkMax("Coral Delivery", CoralDeliveryCfg.DELIVERY_MOTOR);
  }

  private void reset(){
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
  }
  
 
  public void setDeliveryPower(double power){
    delivery.set(power);
  } 
}