package frc.robot.subsystems.Elevator;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.config.SparkBaseConfig;

public class ElevatorCfg {
    public static final int ELEVATOR_MOTOR_ID = 2;

    public static final SparkFlex ELEVATOR_MOTOR = new SparkFlex(ELEVATOR_MOTOR_ID, SparkLowLevel.MotorType.kBrushless);

    public static final double ELEVATOR_GEAR_RATIO = 1.0/9.0;
    public static final double SPROCKET_DIA_CM = 4.47;
    public static final double ELEVATOR_POS_CONVERSION_CM = 2*(ELEVATOR_GEAR_RATIO*SPROCKET_DIA_CM*Math.PI);

    public static final int ELEVATOR_CURRENT_LIMIT = 60;
    public static final boolean ELEVATOR_MOTOR_REVERSED = true;
    public static final SparkBaseConfig.IdleMode ELEVATOR_IDLE_MODE = SparkBaseConfig.IdleMode.kBrake;

    public static final double ELEVATOR_P_GAIN = 0.2;
    public static final double ELEVATOR_I_GAIN = 0;
    public static final double ELEVATOR_D_GAIN = 0;
    public static final double ELEVATOR_MAX_OUTPUT = 0.5;
    public static final double ELEVATOR_MIN_OUTPUT = -0.25;

    public static final double ELEVATOR_LOAD_POSITION = 0.0;
    public static final double ELEVATOR_LONE_POSITION = 50;
    public static final double ELEVATOR_LTWO_POSITION = 70;
    public static final double ELEVATOR_LTHREE_POSITION = 90;
    public static final double ELEVATOR_LFOUR_POSITION =110;

    public static final double ELEVATOR_POSITIONS[] = {ELEVATOR_LOAD_POSITION, ELEVATOR_LONE_POSITION, ELEVATOR_LTWO_POSITION, ELEVATOR_LTHREE_POSITION, ELEVATOR_LFOUR_POSITION};
}
