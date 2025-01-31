package frc.testmode.swerve;

import com.ctre.phoenix6.configs.MagnetSensorConfigs;
import com.ctre.phoenix6.hardware.CANcoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

final class CANCoders {
    public static final CANcoder FRONT_LEFT_CAN_CODER = new CANcoder(16);
    public static final CANcoder FRONT_RIGHT_CAN_CODER = new CANcoder(10);
    public static final CANcoder BACK_LEFT_CAN_CODER = new CANcoder(4);
    public static final CANcoder BACK_RIGHT_CAN_CODER = new CANcoder(8);
}
  
public class AbsoluteEncoderAlignment {
    public AbsoluteEncoderAlignment() {
        zeroPositions();
    }
    
    public void testInit() {
        zeroPositions();
    }
    
    public void testPeriodic() {
        updateDashboard();
    }

    public void updateDashboard(){
        SmartDashboard.putNumber("16", CANCoders.FRONT_LEFT_CAN_CODER.getAbsolutePosition().getValueAsDouble());
        SmartDashboard.putNumber("10", CANCoders.FRONT_RIGHT_CAN_CODER.getAbsolutePosition().getValueAsDouble());
        SmartDashboard.putNumber("4", CANCoders.BACK_LEFT_CAN_CODER.getAbsolutePosition().getValueAsDouble());
        SmartDashboard.putNumber("8", CANCoders.BACK_RIGHT_CAN_CODER.getAbsolutePosition().getValueAsDouble());
    }
    
    public void zeroPositions(){
        //Reset magnet configs to factory defaults
        CANCoders.FRONT_LEFT_CAN_CODER.getConfigurator().apply(new MagnetSensorConfigs());
        CANCoders.FRONT_RIGHT_CAN_CODER.getConfigurator().apply(new MagnetSensorConfigs());
        CANCoders.BACK_LEFT_CAN_CODER.getConfigurator().apply(new MagnetSensorConfigs());
        CANCoders.BACK_RIGHT_CAN_CODER.getConfigurator().apply(new MagnetSensorConfigs());
    }    
}
