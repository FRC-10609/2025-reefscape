package frc.robot.subsystems.Algae;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkBase;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.config.ClosedLoopConfig;
import com.revrobotics.spark.config.EncoderConfig;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

import frc.robot.subsystems.Algae.AlgaeCfg;

public class AlgaeSubsystem extends SubsystemBase  {
    private final SparkMax algaeMotor;
    private final SparkClosedLoopController algaePIDController;
    private final double algae_p_gain = AlgaeCfg.ALGAE_P_GAIN;
    private final double algae_i_gain = AlgaeCfg.ALGAE_I_GAIN;
    private final double algae_d_gain = AlgaeCfg.ALGAE_D_GAIN;
    private final RelativeEncoder algaeEncoder;
    private double algaePos;


    public AlgaeSubsystem() {
        algaeMotor = AlgaeCfg.MOTOR;
        algaeEncoder = algaeMotor.getEncoder();
        algaePIDController = algaeMotor.getClosedLoopController();
        algaePos = 0;

        config();            
    }

    private void config(){
        SparkMaxConfig algaeConfig = new SparkMaxConfig();
        algaeConfig.idleMode(AlgaeCfg.ALGAE_IDLE_MODE);
        algaeConfig.inverted(AlgaeCfg.ALGAE_INVERTED);
        algaeConfig.smartCurrentLimit(AlgaeCfg.ALGAE_AMP_LIMIT);
        
        ClosedLoopConfig algaePIDConfig = new ClosedLoopConfig();
        algaePIDConfig.p(algae_p_gain);
        algaePIDConfig.i(algae_i_gain);
        algaePIDConfig.d(algae_d_gain);
        algaePIDConfig.outputRange(-0.4, 0.2);
        algaeConfig.apply(algaePIDConfig);

        EncoderConfig algaeEncoderConfig = new EncoderConfig();
        algaeEncoderConfig.velocityConversionFactor(AlgaeCfg.GEAR_RATIO);
        algaeEncoderConfig.positionConversionFactor(AlgaeCfg.GEAR_RATIO);
        algaeConfig.apply(algaeEncoderConfig);
        
        algaeMotor.configure(algaeConfig, SparkBase.ResetMode.kResetSafeParameters, SparkBase.PersistMode.kPersistParameters);

    }
    
    @Override
    public void periodic(){
        updateDashboard();
        setPosition(algaePos);
    }
    
    public double getPosition(){
        return algaeEncoder.getPosition();
    }
    
    private void updateDashboard(){
        SmartDashboard.putNumber("Algae SetPosition: ", algaePos);
        SmartDashboard.putNumber("Algae Position: ", getPosition());
    }

    private void setPosition(double pos){
        algaePIDController.setReference(pos, SparkFlex.ControlType.kPosition);
    }

    public void setAlgaePosition(int pos){
        algaePos = AlgaeCfg.ALGAE_POSITIONS[pos];
    }
    

    
    
}
