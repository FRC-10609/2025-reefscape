package frc.robot.subsystems.CoralDelivery;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;


public class CoralDeliveryCfg {

     public static final int DELIVERY_MOTOR_ID = 19;

    public static final SparkMax DELIVERY_MOTOR = new SparkMax(DELIVERY_MOTOR_ID, SparkLowLevel.MotorType.kBrushless);

    public static final double DELIVERY_GEAR_RATIO = 1/5.0;//TBD with design

    public static final SparkBaseConfig.IdleMode DELIVERY_IDLE_MODE = SparkBaseConfig.IdleMode.kBrake;
    public static final boolean DELIVERY_MOTOR_REVERSED = false;
    public static final int DELIVERY_CURRENT_LIMIT = 20;
    
     public static final double DELIVERY_OFF_SPEED = 0;
     public static final double DELIVERY_FWD_SPEED = 1;
     public static final double DELIVERY_RWD_SPEED = -1;
     public static final double DELIVERY_UNLOAD_SPD = -0.25;
     public static final double DELIVERY_LOAD_SPD = 0.15;

     public static final int CORAL_PRESENT_THRESH_MM = 15;


}