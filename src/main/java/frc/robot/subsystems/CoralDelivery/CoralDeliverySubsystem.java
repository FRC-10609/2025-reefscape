// // Copyright (c) FIRST and other WPILib contributors.
// // Open Source Software; you can modify and/or share it under the terms of
// // the WPILib BSD license file in the root directory of this project.

// package frc.robot.subsystems.CoralDelivery;

// import com.revrobotics.RelativeEncoder;
// import com.revrobotics.spark.SparkBase;
// import com.revrobotics.spark.SparkClosedLoopController;
// import com.revrobotics.spark.SparkMax;
// import com.revrobotics.spark.config.ClosedLoopConfig;
// import com.revrobotics.spark.config.EncoderConfig;
// import com.revrobotics.spark.config.SparkMaxConfig;

// // import au.grapplerobotics.LaserCan;
// import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.first.wpilibj2.command.SubsystemBase;
// import frc.robot.Logger;

// public class CoralDeliverySubsystem extends SubsystemBase {
//   /** Creates a new CoralDSubsystem. */
//   private final SparkMax elevator;
//   // private final SparkMax pivot;
//   // private final SparkMax delivery;

//   private RelativeEncoder elevatorEncoder;
//   // private RelativeEncoder pivotEncoder;
//   // private RelativeEncoder deliveryEncoder;
  
//   // private SparkClosedLoopController pivotPIDController;
//   private SparkClosedLoopController elevatorPIDController;

//   // private LaserCan fwdCoralDeliveryTracker;//Change this name and duplicate for the 2nd sensor
//   // private LaserCan rwdCoralDeliveryTracker;
//   private double elevatorSetPosition = 0;
//   // private double pivotSetPosition = 0;

//   private double elevator_p_gain = CoralDeliveryCfg.ELEVATOR_P_GAIN;
//   private double elevator_i_gain = CoralDeliveryCfg.ELEVATOR_I_GAIN;
//   private double elevator_d_gain = CoralDeliveryCfg.ELEVATOR_D_GAIN;

//   private double elevator_p_gain_prev = CoralDeliveryCfg.ELEVATOR_P_GAIN;
//   private double elevator_i_gain_prev = CoralDeliveryCfg.ELEVATOR_I_GAIN;
//   private double elevator_d_gain_prev = CoralDeliveryCfg.ELEVATOR_D_GAIN;

//   // private double pivot_p_gain = CoralDeliveryCfg.PIVOT_P_GAIN;
//   // private double pivot_i_gain = CoralDeliveryCfg.PIVOT_I_GAIN;
//   // private double pivot_d_gain = CoralDeliveryCfg.PIVOT_D_GAIN;

//   // private double pivot_p_gain_prev = CoralDeliveryCfg.PIVOT_P_GAIN;
//   // private double pivot_i_gain_prev = CoralDeliveryCfg.PIVOT_I_GAIN;
//   // private double pivot_d_gain_prev = CoralDeliveryCfg.PIVOT_D_GAIN;

//   private EncoderConfig elevatorEncoderConfig = new EncoderConfig();
//   private ClosedLoopConfig elevatorPID_Config = new ClosedLoopConfig();
//   private SparkMaxConfig elevatorConfig = new SparkMaxConfig();

//   // private EncoderConfig pivotEncoderConfig = new EncoderConfig();
//   // private ClosedLoopConfig pivotPID_Config = new ClosedLoopConfig();
//   // private SparkMaxConfig pivotConfig = new SparkMaxConfig();

//   // private enum CoralDeliveryState{
//   //   INIT,
//   //   UNLOADED,
//   //   LOADING_FROM_INDEX1,
//   //   LOADING_FROM_INDEX2,
//   //   LOADED,
//   //   UNLOADING
//   // }

//   // CoralDeliveryState deliveryState = CoralDeliveryState.INIT;

//   public CoralDeliverySubsystem() {
//     elevator = CoralDeliveryCfg.ELEVATOR_MOTOR;
//     // pivot = CoralDeliveryCfg.PIVOT_MOTOR;
//     // delivery = CoralDeliveryCfg.DELIVERY_MOTOR;

//     //Configure the elevator controller
//     configureElevator();

//     //Configure the pivot controller
//     // configureCoralPivot();

//     //Configure the delivery controller (and distance sensors)
//     // configureCoralDelivery();
    
//     SmartDashboard.putNumber("Elevator P Gain", elevator_p_gain);
//     SmartDashboard.putNumber("Elevator I Gain", elevator_i_gain);
//     SmartDashboard.putNumber("Elevator D Gain", elevator_d_gain);

//     // SmartDashboard.putNumber("Pivot P Gain", pivot_p_gain);
//     // SmartDashboard.putNumber("Pivot I Gain", pivot_i_gain);
//     // SmartDashboard.putNumber("Pivot D Gain", pivot_d_gain);

//     reset();
//     registerLoggerObjects();
//   }

//   private void updateDashboard(){
//     boolean elevatorCfgChanged = false;
//     // boolean pivotCfgChanged = false;
    
//     elevator_p_gain = SmartDashboard.getNumber("Elevator P Gain",0);
//     elevator_i_gain = SmartDashboard.getNumber("Elevator I Gain",0);
//     elevator_d_gain = SmartDashboard.getNumber("Elevator D Gain",0);

//     if(elevator_p_gain != elevator_p_gain_prev){elevatorPID_Config.p(elevator_p_gain); elevatorCfgChanged = true;}
//     if(elevator_i_gain != elevator_i_gain_prev){elevatorPID_Config.i(elevator_i_gain); elevatorCfgChanged = true;}
//     if(elevator_d_gain != elevator_d_gain_prev){elevatorPID_Config.d(elevator_i_gain); elevatorCfgChanged = true;}

//     if(elevatorCfgChanged){
//       elevatorConfig.apply(elevatorPID_Config);
//       elevator.configure(elevatorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
//       elevatorCfgChanged = false;
//     }

//     // pivot_p_gain = SmartDashboard.getNumber("Pivot P Gain", 0);
//     // pivot_i_gain = SmartDashboard.getNumber("Pivot I Gain", 0);
//     // pivot_p_gain = SmartDashboard.getNumber("Pivot D Gain", 0);

//     // if(pivot_p_gain != pivot_p_gain_prev){pivotPID_Config.p(pivot_p_gain); pivotCfgChanged = true;}
//     // if(pivot_i_gain != pivot_i_gain_prev){pivotPID_Config.i(pivot_i_gain); pivotCfgChanged = true;}
//     // if(pivot_d_gain != pivot_d_gain_prev){pivotPID_Config.d(pivot_i_gain); pivotCfgChanged = true;}

//     // if(pivotCfgChanged){
//     // //pivotConfig.apply(elevatorPID_Config);
//     // //pivot.configure(elevatorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
//     // //elevatorCfgChanged = false;
//     // }

//     SmartDashboard.putNumber("ELEVATOR_CURRENT", elevator.getOutputCurrent());
//     SmartDashboard.putNumber("ELEVATOR_POS", getElevatorPosition());
//     // SmartDashboard.putNumber("PIVOT_POS", getPivotPosition());
//     SmartDashboard.putNumber("ElevatorSetPosition", elevatorSetPosition);
//     // SmartDashboard.putNumber("PivotSetPosition", pivotSetPosition);

//     // SmartDashboard.putNumber("PIVOT_CURRENT", pivot.getOutputCurrent());
//     // SmartDashboard.putNumber("Forward Sensor Distance", getFwdLaserCanDistance());
//     // SmartDashboard.putNumber("Rearward Sensor Distance", getRwdLaserCanDistance());
//     // SmartDashboard.putBoolean("Is Forward Present", isFwdCoralPresent());
//     // SmartDashboard.putBoolean("Is Rearward Present", isRwdCoralPresent());
//     // SmartDashboard.putString("Delivery State", deliveryState.name());
//   }

//   private void configureElevator(){
//     //Setup the Elevator motor config
//     elevatorEncoder = elevator.getEncoder();
//     elevatorEncoderConfig.positionConversionFactor(CoralDeliveryCfg.ELEVATOR_POS_CONVERSION_CM);
//     elevatorEncoderConfig.velocityConversionFactor(CoralDeliveryCfg.ELEVATOR_POS_CONVERSION_CM);
    
//     elevatorPIDController = elevator.getClosedLoopController();
//     elevatorPID_Config.p(CoralDeliveryCfg.ELEVATOR_P_GAIN);
//     elevatorPID_Config.i(CoralDeliveryCfg.ELEVATOR_I_GAIN);
//     elevatorPID_Config.d(CoralDeliveryCfg.ELEVATOR_D_GAIN);
//     elevatorPID_Config.outputRange(-.25,0.75);
    
//     elevatorConfig.idleMode(CoralDeliveryCfg.ELEVATOR_IDLE_MODE);
//     elevatorConfig.inverted(CoralDeliveryCfg.ELEVATOR_MOTOR_REVERSED);
//     elevatorConfig.smartCurrentLimit(CoralDeliveryCfg.ELEVATOR_CURRENT_LIMIT);
    
//     //Apply the encoder and PID configs to the Spark config
//     elevatorConfig.apply(elevatorEncoderConfig);
//     elevatorConfig.apply(elevatorPID_Config);
    
//     //Finally write the config to the spark
//     elevator.configure(elevatorConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
//   }

//   // private void configureCoralPivot(){
//   //   //Setup the Pivot motor config
//   //   pivotEncoder = pivot.getEncoder();
    
//   //   pivotEncoderConfig.positionConversionFactor(CoralDeliveryCfg.PIVOT_ANGLE_CONVERSION_DEG);
//   //   pivotEncoderConfig.velocityConversionFactor(CoralDeliveryCfg.PIVOT_ANGLE_CONVERSION_DEG);

//   //   pivotPIDController = pivot.getClosedLoopController();
//   //   pivotPID_Config.p(CoralDeliveryCfg.PIVOT_P_GAIN);
//   //   pivotPID_Config.i(CoralDeliveryCfg.PIVOT_I_GAIN);
//   //   pivotPID_Config.d(CoralDeliveryCfg.PIVOT_D_GAIN);
//   //   pivotPID_Config.outputRange(-.25,0.4);

//   //   pivotConfig.idleMode(CoralDeliveryCfg.PIVOT_IDLE_MODE);
//   //   pivotConfig.inverted(CoralDeliveryCfg.PIVOT_MOTOR_REVERSED);
//   //   pivotConfig.smartCurrentLimit(CoralDeliveryCfg.PIVOT_CURRENT_LIMIT);

//   //   //Apply the encoder and PID configs on Spark config
//   //   pivotConfig.apply(pivotEncoderConfig);
//   //   pivotConfig.apply(pivotPID_Config);

//   //   //Finally write the config to the spark
//   //   pivot.configure(pivotConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
//   // }

//   // private void configureCoralDelivery(){
//   //   deliveryEncoder = delivery.getEncoder();
//   //   EncoderConfig deliveryEncoderConfig = new EncoderConfig();
//   //   deliveryEncoderConfig.positionConversionFactor(CoralDeliveryCfg.DELIVERY_GEAR_RATIO);
//   //   deliveryEncoderConfig.velocityConversionFactor(CoralDeliveryCfg.DELIVERY_GEAR_RATIO);

//   //   SparkMaxConfig deliveryConfig = new SparkMaxConfig();
//   //   deliveryConfig.idleMode(CoralDeliveryCfg.DELIVERY_IDLE_MODE);
//   //   deliveryConfig.inverted(CoralDeliveryCfg.DELIVERY_MOTOR_REVERSED);
//   //   deliveryConfig.smartCurrentLimit(CoralDeliveryCfg.DELIVERY_CURRENT_LIMIT);

//   //   deliveryConfig.apply(deliveryEncoderConfig);

//   //   //Initialize LaserCan objects here (stuff from RobotInit() in example)
//   //   fwdCoralDeliveryTracker = CoralDeliveryCfg.FWD_LASER_CAN;
//   //   rwdCoralDeliveryTracker = CoralDeliveryCfg.RWD_LASER_CAN;
//   // }

//   private void registerLoggerObjects(){
//     Logger.RegisterSparkMax("Elevator", CoralDeliveryCfg.ELEVATOR_MOTOR);
//     // Logger.RegisterSparkMax("Coral Pivot", CoralDeliveryCfg.PIVOT_MOTOR);
//     // Logger.RegisterSparkMax("Coral Delivery", CoralDeliveryCfg.DELIVERY_MOTOR);
//   }

//   private void reset(){
//     elevatorEncoder.setPosition(0);
//     // pivotEncoder.setPosition(0);
//   }

//   @Override
//   public void periodic() {
//     // This method will be called once per scheduler run
//   //   setElevatorPosition(elevatorSetPosition);
//   //   // setPivotPosition(pivotSetPosition);
//   //   // updateCoralDeliveryState();
//   //   updateDashboard();
//   }

//   // private void updateCoralDeliveryState(){
//   //   switch(deliveryState){
//   //     case INIT:
//   //       if(isFwdCoralPresent()){
//   //         deliveryState = CoralDeliveryState.LOADED;
//   //       }else{
//   //         deliveryState = CoralDeliveryState.UNLOADED;
//   //       }
//   //       break;
//   //     case UNLOADED:
//   //       //State transition will be handled by setDeliveryStateLoading
//   //       break;
//   //     case LOADING_FROM_INDEX1:
//   //       if((isFwdCoralPresent())&&
//   //          (isRwdCoralPresent())){
//   //           deliveryState = CoralDeliveryState.LOADING_FROM_INDEX2;
//   //          }
//   //       break;
//   //     case LOADING_FROM_INDEX2:
//   //       if((isFwdCoralPresent())&&
//   //          (!isRwdCoralPresent())){
//   //           delivery.set(CoralDeliveryCfg.DELIVERY_OFF_SPEED);
//   //           deliveryState = CoralDeliveryState.LOADED;
//   //          }
//   //       break;
//   //     case LOADED:
//   //       //State Transition will be handled from setDeliveryStateUnloading
//   //       break;
//   //     case UNLOADING:
//   //       if((!isFwdCoralPresent())&&
//   //          (!isRwdCoralPresent())){
//   //             delivery.set(CoralDeliveryCfg.DELIVERY_OFF_SPEED);
//   //             deliveryState = CoralDeliveryState.UNLOADED;
//   //          }
//   //   }
//   // }

//   // public void setDeliveryStateUnloading(){
//   //   if(deliveryState == CoralDeliveryState.LOADED){
//   //     if(elevatorSetPosition == CoralDeliveryCfg.ELEVATOR_LFOUR_POSITION){
//   //       delivery.set(CoralDeliveryCfg.DELIVERY_UNLOAD_SPD);
//   //     }else{
//   //       delivery.set(CoralDeliveryCfg.DELIVERY_FWD_SPEED);
//   //     }
//   //     deliveryState = CoralDeliveryState.UNLOADING;
//   //   }
//   // }

//   // public void setDeliveryStateLoading(){
//   //   if(deliveryState == CoralDeliveryState.UNLOADED){
//   //     delivery.set(CoralDeliveryCfg.DELIVERY_LOAD_SPD);
//   //     deliveryState = CoralDeliveryState.LOADING_FROM_INDEX1;
//   //   }
//   // }

//   public void setElevatorPower(double power){
//     elevator.set(power);
//   }

//   // public void setPivotPower(double power){
//   //   pivot.set(power);
//   // }

//   // public void setDeliveryPower(double power){
//   //   delivery.set(power);
//   // }
 
//   public double getElevatorPosition(){
//     return elevatorEncoder.getPosition();
//   }

//   // public double getPivotPosition(){
//   //   return pivotEncoder.getPosition();
//   // }

//   // public double getDeliveryPosition(){
//   //   return deliveryEncoder.getPosition();
//   // }

//   // public int getFwdLaserCanDistance(){
//   //   // Put example code from robotPeriodic() here
//   //   LaserCan.Measurement measurement = fwdCoralDeliveryTracker.getMeasurement();
//   //   if (measurement != null && measurement.status == LaserCan.LASERCAN_STATUS_VALID_MEASUREMENT) {
//   //     return (measurement.distance_mm);
//   //   } else {
//   //     return Integer.MAX_VALUE;
//   //   }
//   // }

//   // public boolean isFwdCoralPresent(){
//   //   boolean isPresent;
//   //   if(getFwdLaserCanDistance() < CoralDeliveryCfg.CORAL_PRESENT_THRESH_MM){
//   //     isPresent = true;
//   //   }else{
//   //     isPresent = false;
//   //   }
//   //   return isPresent;
//   // }

//   // public int getRwdLaserCanDistance(){
//   //   // Put example code from robotPeriodic() here
//   //   LaserCan.Measurement measurement = rwdCoralDeliveryTracker.getMeasurement();
//   //   if (measurement != null && measurement.status == LaserCan.LASERCAN_STATUS_VALID_MEASUREMENT) {
//   //     return (measurement.distance_mm);
//   //   } else {
//   //     return Integer.MAX_VALUE;
//   //   }
//   // }

//   // public boolean isRwdCoralPresent(){
//   //   boolean isPresent;
//   //   if(getRwdLaserCanDistance() < CoralDeliveryCfg.CORAL_PRESENT_THRESH_MM){
//   //     isPresent = true;
//   //   }else{
//   //     isPresent = false;
//   //   }
//   //   return isPresent;
//   // }
    
//   public void setElevatorPosition(double position){
//     elevatorPIDController.setReference(position, SparkMax.ControlType.kPosition);
//   }

//   public void setElevatorLoadPosition(){
//     elevatorSetPosition = CoralDeliveryCfg.ELEVATOR_LOAD_POSITION;
//     // pivotSetPosition = CoralDeliveryCfg.PIVOT_LOAD_POSITION;
//   }

//   public void setElevatorLevel(int level){
//     elevatorSetPosition = CoralDeliveryCfg.ELEVATOR_POSITIONS[level];
//   }

//   public void setElevatorLONEPosition(){
//     elevatorSetPosition = CoralDeliveryCfg.ELEVATOR_LONE_POSITION;
//     // pivotSetPosition = CoralDeliveryCfg.PIVOT_LONE_POSITION;  
//   }

//   public void setElevatorLTWOPosition(){
//     elevatorSetPosition = CoralDeliveryCfg.ELEVATOR_LTWO_POSITION;
//     // pivotSetPosition = CoralDeliveryCfg.PIVOT_LTWO_POSITION;  
//   }

//   public void setElevatorLTHREEPosition(){
//     elevatorSetPosition = CoralDeliveryCfg.ELEVATOR_LTHREE_POSITION;
//     // pivotSetPosition = CoralDeliveryCfg.PIVOT_LTHREE_POSITION;
//   }

//   public void setElevatorLFOURPosition(){
//     elevatorSetPosition = CoralDeliveryCfg.ELEVATOR_LFOUR_POSITION;
//     // pivotSetPosition = CoralDeliveryCfg.PIVOT_LFOUR_POSITION;
//   }

//   public void setElevatorFwd(){
//     setElevatorPower(1);
//   }

//   public void setElevatorRwd(){
//     setElevatorPower(-0.5);
//   }
  
//   public void setElevatorOff(){
//     setElevatorPower(0);
//   }
  
//   // public void setPivotPosition(double position){
//   //   pivotPIDController.setReference(position, SparkMax.ControlType.kPosition);
//   // }

//   // public void setPivotDown(){//TODO: Change to "setPivotLoadPosition"
//   //   System.out.println("Pivot Down");
//   //   pivotSetPosition = 0;//Use constant values from CoralDeliveryCfg instead of "magic numbers"
//   // }

//   // public void setPivotUp(){//TODO: Change to "setPivotL1Position" and copy/paste for each setpoint L2 to L4
//   //   System.out.println("Pivot Up");
//   //   pivotSetPosition = 90;//Use constant values from CoralDeliveryCfg instead of "magic numbers"
//   // }

//   // public void setPivotLoadPosition(){//TODO: Change to "setPivotL1Position" and copy/paste for each setpoint L2 to L4
//   //   pivotSetPosition = CoralDeliveryCfg.PIVOT_LOAD_POSITION;//Use constant values from CoralDeliveryCfg instead of "magic numbers"
//   // }

//   // public void setPivotLOnePosition(){//TODO: Change to "setPivotL1Position" and copy/paste for each setpoint L2 to L4
//   //   pivotSetPosition = CoralDeliveryCfg.PIVOT_LONE_POSITION;//Use constant values from CoralDeliveryCfg instead of "magic numbers"
//   // }

//   // public void setPivotLTwoPosition(){//TODO: Change to "setPivotL1Position" and copy/paste for each setpoint L2 to L4
//   //   pivotSetPosition = CoralDeliveryCfg.PIVOT_LTWO_POSITION;//Use constant values from CoralDeliveryCfg instead of "magic numbers"
//   // }

//   // public void setPivotLTHREEPosition(){//TODO: Change to "setPivotL1Position" and copy/paste for each setpoint L2 to L4
//   //   pivotSetPosition = CoralDeliveryCfg.PIVOT_LTHREE_POSITION;//Use constant values from CoralDeliveryCfg instead of "magic numbers"
//   // }

//   // public void setPivotLFOURPosition(){//TODO: Change to "setPivotL1Position" and copy/paste for each setpoint L2 to L4
//   //   pivotSetPosition = CoralDeliveryCfg.PIVOT_LFOUR_POSITION;//Use constant values from CoralDeliveryCfg instead of "magic numbers"
//   // }
  
//   // public void setPivotOn(){
//   //   setPivotPower(1);
//   // }

//   // public void setPivotOff(){
//   //   setPivotPower(0);
//   // }
// }