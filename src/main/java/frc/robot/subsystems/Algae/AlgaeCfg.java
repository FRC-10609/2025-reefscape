package frc.robot.subsystems.Algae;

import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.SparkMaxConfig;

public class AlgaeCfg {
     public static final int MOTOR_ID = 30;
     public static final SparkMax MOTOR = new SparkMax(MOTOR_ID, SparkLowLevel.MotorType.kBrushless);

     public static final double GEAR_RATIO = 1/9.0;

     public static final SparkMaxConfig.IdleMode ALGAE_IDLE_MODE = SparkMaxConfig.IdleMode.kBrake;
    public static final boolean ALGAE_INVERTED = false;
    public static final int ALGAE_AMP_LIMIT = 50;

    public static final double ALGAE_P_GAIN = 0.2;
    public static final double ALGAE_I_GAIN = 0;
    public static final double ALGAE_D_GAIN = 0;

    public static final double ALGAE_POS_1 = 0;
    public static final double ALGAE_POS_2 = 1;
    public static final double[] ALGAE_POSITIONS =  {ALGAE_POS_1, ALGAE_POS_2};
}
