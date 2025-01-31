package frc.robot.subsystems.SwerveDrive;

import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkLowLevel;
import com.revrobotics.spark.SparkMax;

final class ChassisMotorCfg {
  //User defined Configs
  //drive
  public static final int DRIVE_FRONT_LEFT_ID = 17;
  public static final int DRIVE_FRONT_RIGHT_ID = 11;
  public static final int DRIVE_BACK_RIGHT_ID = 9;
  public static final int DRIVE_BACK_LEFT_ID = 5;

  public static final boolean DRIVE_FRONT_LEFT_REVERSED   = true;
  public static final boolean DRIVE_FRONT_RIGHT_REVERSED  = true;
  public static final boolean DRIVE_BACK_LEFT_REVERSED    = true;
  public static final boolean DRIVE_BACK_RIGHT_REVERSED   = true;
    
  //turn
  public static final int ANGLE_FRONT_LEFT_ID = 16;
  public static final int ANGLE_FRONT_RIGHT_ID = 10;
  public static final int ANGLE_BACK_RIGHT_ID = 8;
  public static final int ANGLE_BACK_LEFT_ID = 4;

  public static final boolean ANGLE_FRONT_LEFT_REVERSED   = true;
  public static final boolean ANGLE_FRONT_RIGHT_REVERSED  = true;
  public static final boolean ANGLE_BACK_RIGHT_REVERSED   = true;
  public static final boolean ANGLE_BACK_LEFT_REVERSED    = true;

  //Other configs
  public static final SparkFlex DRIVE_FRONT_LEFT   = new SparkFlex(DRIVE_FRONT_LEFT_ID, SparkLowLevel.MotorType.kBrushless);
  public static final SparkFlex DRIVE_FRONT_RIGHT  = new SparkFlex(DRIVE_FRONT_RIGHT_ID, SparkLowLevel.MotorType.kBrushless);
  public static final SparkFlex DRIVE_BACK_RIGHT   = new SparkFlex(DRIVE_BACK_RIGHT_ID, SparkLowLevel.MotorType.kBrushless);
  public static final SparkFlex DRIVE_BACK_LEFT    = new SparkFlex(DRIVE_BACK_LEFT_ID, SparkLowLevel.MotorType.kBrushless);

  public static final boolean DRIVE_MOTOR_REVERSED[] = {
    DRIVE_FRONT_LEFT_REVERSED,
    DRIVE_FRONT_RIGHT_REVERSED,
    DRIVE_BACK_LEFT_REVERSED,
    DRIVE_BACK_RIGHT_REVERSED
  };

  public static final SparkMax ANGLE_FRONT_LEFT   = new SparkMax(ANGLE_FRONT_LEFT_ID, SparkLowLevel.MotorType.kBrushless);
  public static final SparkMax ANGLE_FRONT_RIGHT  = new SparkMax(ANGLE_FRONT_RIGHT_ID, SparkLowLevel.MotorType.kBrushless);
  public static final SparkMax ANGLE_BACK_RIGHT   = new SparkMax(ANGLE_BACK_RIGHT_ID, SparkLowLevel.MotorType.kBrushless);
  public static final SparkMax ANGLE_BACK_LEFT    = new SparkMax(ANGLE_BACK_LEFT_ID, SparkLowLevel.MotorType.kBrushless);

  public static final boolean ANGLE_MOTOR_REVERSED[] = {
    ANGLE_FRONT_LEFT_REVERSED,
    ANGLE_FRONT_RIGHT_REVERSED,
    ANGLE_BACK_LEFT_REVERSED,
    ANGLE_BACK_RIGHT_REVERSED
  };
}