package frc.robot.subsystems.Algae;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.EncoderConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.subsystems.Algae.AlgaeCfg;

public class AlgaeSubsystem extends SubsystemBase  {
    private final SparkMax leftMotor;
    private final SparkMax rightMotor;

    private final RelativeEncoder leftEncoder;
    private final RelativeEncoder rightEncoder;

    public AlgaeSubsystem() {
        leftMotor = AlgaeCfg.LEFT_MOTOR;
        rightMotor = AlgaeCfg.RIGHT_MOTOR;
        
        leftEncoder = leftMotor.getEncoder();
        rightEncoder = rightMotor.getEncoder();

        SparkMaxConfig leftConfig = new SparkMaxConfig();
        leftConfig.idleMode(AlgaeCfg.LEFT_IDLE_MODE);
        leftConfig.inverted(AlgaeCfg.LEFT_INVERTED);
        leftConfig.smartCurrentLimit(AlgaeCfg.LEFT_AMP_LIMIT);
        leftMotor.configure(leftConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
        
        SparkMaxConfig rightConfig = new SparkMaxConfig();
        rightConfig.idleMode(AlgaeCfg.RIGHT_IDLE_MODE);
        rightConfig.inverted(AlgaeCfg.RIGHT_INVERTED);
        rightConfig.smartCurrentLimit(AlgaeCfg.RIGHT_AMP_LIMIT);
        rightMotor.configure(rightConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);
        
        leftEncoder.setPosition(0);
        rightEncoder.setPosition(0);
    }

    public void inTake(int in){
        leftMotor.set(in);
        rightMotor.set(in);
    }
}
