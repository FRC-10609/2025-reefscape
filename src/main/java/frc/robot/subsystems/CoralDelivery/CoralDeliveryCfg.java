package frc.robot.subsystems.CoralDelivery;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkBaseConfig;


public class CoralDeliveryCfg {

     public static final int PIVOT_MOTOR_ID = 18;
     public static final int DELIVERY_MOTOR_ID = 19;

    public static final SparkFlex PIVOT_MOTOR = new SparkFlex(PIVOT_MOTOR_ID, SparkLowLevel.MotorType.kBrushless);
    public static final SparkMax DELIVERY_MOTOR = new SparkMax(DELIVERY_MOTOR_ID, SparkLowLevel.MotorType.kBrushless);

    public static final double PIVOT_GEAR_RATIO = 1/36.0;//TBD with design
    public static final double DELIVERY_GEAR_RATIO = 1/5.0;//TBD with design
    
    public static final int ENCODER_CPR = 42;

    public static final double PIVOT_ANGLE_CONVERSION_DEG = (1/360.0)*(1/PIVOT_GEAR_RATIO);

    public static final SparkBaseConfig.IdleMode PIVOT_IDLE_MODE = SparkBaseConfig.IdleMode.kBrake;
    public static final SparkBaseConfig.IdleMode DELIVERY_IDLE_MODE = SparkBaseConfig.IdleMode.kBrake;
    public static final boolean PIVOT_MOTOR_REVERSED = false;
    public static final boolean DELIVERY_MOTOR_REVERSED = false;
    public static final int PIVOT_CURRENT_LIMIT = 40;
    public static final int DELIVERY_CURRENT_LIMIT = 20;
    
    public static final double PIVOT_P_GAIN = 0.05;
    public static final double PIVOT_I_GAIN = 0;
    public static final double PIVOT_D_GAIN = 0;
    public static final double PIVOT_MAX_OUTPUT = 1;
    public static final double PIVOT_MIN_OUTPUT = -0.25;

     public static final double DELIVERY_OFF_SPEED = 0;
     public static final double DELIVERY_FWD_SPEED = 1;
     public static final double DELIVERY_RWD_SPEED = -1;
     public static final double DELIVERY_UNLOAD_SPD = -0.25;
     public static final double DELIVERY_LOAD_SPD = 0.15;

     public static final int CORAL_PRESENT_THRESH_MM = 15;

     public static final double PIVOT_LOAD_POSITION = 0.5;
     public static final double PIVOT_LONE_POSITION = 40*PIVOT_ANGLE_CONVERSION_DEG;
     public static final double PIVOT_LTWO_POSITION = 80*PIVOT_ANGLE_CONVERSION_DEG;
     public static final double PIVOT_LTHREE_POSITION = 140*PIVOT_ANGLE_CONVERSION_DEG;
     public static final double PIVOT_LFOUR_POSITION = 250*PIVOT_ANGLE_CONVERSION_DEG;
     
     public static final double PIVOT_POSITIONS[] = {PIVOT_LOAD_POSITION, PIVOT_LONE_POSITION, PIVOT_LTWO_POSITION, PIVOT_LTHREE_POSITION, PIVOT_LFOUR_POSITION};

}