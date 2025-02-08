// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems.Climber;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.EncoderConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.ADIS16448_IMU.CalibrationTime;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

public class ElevatorSubsystem extends SubsystemBase {
  /** Creates a new Climber. */

  private final SparkFlex climber;
  private final RelativeEncoder climberEncoder;
  
  private final SparkClosedLoopController climberPIDComController;

  public ElevatorSubsystem() {
    //Do intialization stuff here
    climber = ElevatorCfg.CLIMBER;
    climberEncoder = climber.getEncoder();

    //Setup Encoder Config
    EncoderConfig climbEncoderConfig = new EncoderConfig();
    climbEncoderConfig.positionConversionFactor(ElevatorCfg.CLIMBER_GEAR_RATIO);
    climbEncoderConfig.velocityConversionFactor(ElevatorCfg.CLIMBER_GEAR_RATIO);

    climberPIDComController = climber.getClosedLoopController();
    ClosedLoopConfig climberPIDConfig = new ClosedLoopConfig();
    climberPIDConfig.p(ElevatorCfg.CLIMBER_P_GAIN);
    climberPIDConfig.i(ElevatorCfg.CLIMBER_I_GAIN);
    climberPIDConfig.d(ElevatorCfg.CLIMBER_D_GAIN);

    //Setup Motor Config
    SparkMaxConfig climbMotorConfig = new SparkMaxConfig();
    climbMotorConfig.idleMode(ElevatorCfg.CLIMBER_IDLE_MODE);
    climbMotorConfig.inverted(ElevatorCfg.CLIMBER_MOTOR_REVERSED);
    climbMotorConfig.smartCurrentLimit(ElevatorCfg.CLIMBER_CURRENT_LIMIT);
    
    //Apply the encoder config to the SparkMax
    climbMotorConfig.apply(climbEncoderConfig);
    climbMotorConfig.apply(climberPIDConfig);

    //Finally write config to the spark
    climber.configure(climbMotorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
  }

  @Override
  public void periodic() {
    // This method will be called once per scheduler run
    SmartDashboard.putNumber("Elevator pos.:", getClimberPosition());
  }

  public void setClimberPower(double power){
    climber.set(power);
  }

  public void setClimberPosition(double position){
    climberPIDComController.setReference(position, SparkBase.ControlType.kPosition);
  }

  public double getClimberPosition(){
    return climberEncoder.getPosition();
  }
  public boolean isAtPosition(double position){
    return climberEncoder.getPosition() == position;
  }
}