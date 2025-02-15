package frc.robot.subsystems.Algae;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

public class AlgaeCfg {
     public static final int LEFT_MOTOR_ID = 18;
     public static final int RIGHT_MOTOR_ID = 19;
     public static final SparkMax LEFT_MOTOR = new SparkMax(LEFT_MOTOR_ID, SparkLowLevel.MotorType.kBrushless);
     public static final SparkMax RIGHT_MOTOR = new SparkMax(RIGHT_MOTOR_ID, SparkLowLevel.MotorType.kBrushless);

     public static final double R_GEAR_RATIO = 1/1.0;
     public static final double L_GEAR_RATIO = 1/1.0;

     public static final SparkMaxConfig.IdleMode LEFT_IDLE_MODE = SparkMaxConfig.IdleMode.kBrake;
     public static final SparkMaxConfig.IdleMode RIGHT_IDLE_MODE = SparkMaxConfig.IdleMode.kBrake;
     public static final boolean LEFT_INVERTED = false;
    public static final boolean RIGHT_INVERTED = false;
    public static final int LEFT_AMP_LIMIT = 50;
    public static final int RIGHT_AMP_LIMIT = 50;
}
