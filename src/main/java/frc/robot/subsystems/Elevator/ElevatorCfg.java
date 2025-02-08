package frc.robot.subsystems.Elevator;

import java.io.File;

import com.fasterxml.jackson.annotation.JsonTypeInfo.None;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.config.SparkBaseConfig;

public class ElevatorCfg {

    public static final int CLIMBER_ID = 2;
    public static final SparkFlex CLIMBER = new SparkFlex(CLIMBER_ID, SparkLowLevel.MotorType.kBrushless);

    public static final double CLIMBER_GEAR_RATIO = 1/45.0;//TBD with design

    public static final SparkBaseConfig.IdleMode CLIMBER_IDLE_MODE = SparkBaseConfig.IdleMode.kBrake;
    public static final boolean CLIMBER_MOTOR_REVERSED = false;
    public static final int CLIMBER_CURRENT_LIMIT = 50;

    public static final double CLIMBER_P_GAIN = 0;
    public static final double CLIMBER_I_GAIN = 0;
    public static final double CLIMBER_D_GAIN = 0;

    public static final Double[] CLIMBER_POSITIONS = {null, 1.0, 2.0, 3.0, 4.0};

}