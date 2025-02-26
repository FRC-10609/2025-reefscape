package frc.robot;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Set;
import java.util.function.Supplier;

//import com.ctre.phoenix.ErrorCode;
//import com.ctre.phoenix.motorcontrol.Faults;
//import com.ctre.phoenix.motorcontrol.can.BaseTalon;
//import com.ctre.phoenix.motorcontrol.can.MotControllerJNI;
//import com.ctre.phoenix.sensors.BasePigeon;
import com.ctre.phoenix6.hardware.CANcoder;
//import com.ctre.phoenix.sensors.CANCoderFaults;
//import com.ctre.phoenix.sensors.CANCoderStickyFaults;
import com.ctre.phoenix6.hardware.Pigeon2;
//import com.ctre.phoenix.sensors.Pigeon2_Faults;
//import com.ctre.phoenix.sensors.PigeonIMU;
//import com.ctre.phoenix.sensors.PigeonIMU_Faults;
import com.revrobotics.spark.*;

import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.PneumaticHub;
import edu.wpi.first.wpilibj.PowerDistribution;
import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.shuffleboard.EventImportance;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Logger implements Runnable {
    private final double VOLTS_PER_PSI = 1.931/100; //2.431V at 100psi

    private Notifier notify;
    private static HashMap<String,Object> items = new HashMap<String,Object>();
    private static PowerDistribution pdp;
    private static String[] pdpNames;
    private static PneumaticHub ph;
    private static String[] pneumaticNames;
    private static Pigeon2 pigeon;
    private static CANcoder coder;
    private static SparkMax spark;       //used to stop warning about not closing motor, since we really don't...
    private static SparkFlex sparkFlex;

    private NetworkTable currentTable;
    private NetworkTable commandTable;
    private NetworkTable tempTable;
    private NetworkTable faultTable;
    private NetworkTable stickyTable;
    private NetworkTable warningTable;
    private NetworkTable stickyWarningTable;
    private NetworkTable sensorTable;
    private NetworkTable canStatusTable;
    private static NetworkTable taskTimings;

    private static boolean faultSet;
    private static boolean sfaultSet;
    //solenoids, compressor

    public Logger() {
        // Starts recording to data log
        DataLogManager.start();
        // Record both DS control and joystick data
        var log = DataLogManager.getLog();
        DriverStation.startDataLog(log);

        //create our logging table references
        canStatusTable = NetworkTableInstance.getDefault().getTable("CAN_Status");
        currentTable = NetworkTableInstance.getDefault().getTable("Motor_Currents");
        tempTable = NetworkTableInstance.getDefault().getTable("Motor_Temps");
        commandTable = NetworkTableInstance.getDefault().getTable("Device_Commands");
        faultTable = NetworkTableInstance.getDefault().getTable("Device_Faults");
        stickyTable = NetworkTableInstance.getDefault().getTable("Device_Faults_Sticky");
        warningTable = NetworkTableInstance.getDefault().getTable("Device_Warnings_Sticky");
        stickyWarningTable = NetworkTableInstance.getDefault().getTable("Device_Warnings");
        sensorTable = NetworkTableInstance.getDefault().getTable("Sensors");
        //taskTimings = NetworkTableInstance.getDefault().getTable("Task_Timings_ms");
        SmartDashboard.putBoolean("Clear Faults", false);

        // Set the scheduler to log Shuffleboard events for command initialize,
        // interrupt, finish
        CommandScheduler.getInstance()
                .onCommandInitialize(
                        command -> Shuffleboard.addEventMarker(
                                "Command initialized", command.getName(), EventImportance.kNormal));
        CommandScheduler.getInstance()
                .onCommandInterrupt(
                        command -> Shuffleboard.addEventMarker(
                                "Command interrupted", command.getName(), EventImportance.kNormal));
        CommandScheduler.getInstance()
                .onCommandFinish(
                        command -> Shuffleboard.addEventMarker(
                                "Command finished", command.getName(), EventImportance.kNormal));
    }

    public void start() {
        //register with the robot to schedule our task
        notify = new Notifier(this);
        notify.startPeriodic(0.1);
        notify.setName("Logging");
    }

    public static void RegisterSparkMax(String name, SparkMax spark) {
        items.put(name, spark);
    }

    public static void RegisterSparkFlex(String name, SparkFlex spark) {
        items.put(name, spark);
    }

    public static void RegisterCanCoder(String name, CANcoder coder) {
        items.put(name, coder);
    }

    public static void RegisterSensor(String name, Supplier<Double> value) {
        items.put(name, value);
    }

    public static void RegisterPdp(PowerDistribution pdp, String[] channelNames) {
        Logger.pdp = pdp;
        pdpNames = channelNames;
    }

    public static void RegisterPneumaticHub(PneumaticHub ph, String[] channelNames) {
        Logger.ph = ph;
        pneumaticNames = channelNames;
    }

    public static void RegisterLoopTimes(Robot robot) {
        new LoopTimeLogger(robot, taskTimings);
    }

    public static void RegisterPigeon(Pigeon2 pigeon) {
        Logger.pigeon = pigeon;
    }

    public static void PushSwerveStates(SwerveModuleState[] state, SwerveModuleState[] request) {
        //CDL - Rework this for 1502's swerve
        var size = state.length;
        var states = new double[size * 2];
        var requests = new double[size * 2];
        for(var i=0; i<size; i++) {
            states[(i * 2)] = state[i].angle.getDegrees();
            states[(i * 2)+1] = state[i].speedMetersPerSecond;
            requests[(i * 2)] = request[i].angle.getDegrees();
            requests[(i * 2)+1] = request[i].speedMetersPerSecond;
        }
        SmartDashboard.putNumberArray("Swerve State", states);
        SmartDashboard.putNumberArray("Swerve Request", requests);
    }

    public void run() {
        // Print keys
        for (String i : items.keySet()) {
            var item = items.get(i);

            if(item instanceof Supplier<?>) {
                sensorTable.getEntry(i).setDouble(((Supplier<Double>)item).get());
            } else if(item instanceof CANcoder) {
                coder = (CANcoder)item;
                sensorTable.getEntry(i + " Angle").setDouble(coder.getAbsolutePosition().getValueAsDouble());
                sensorTable.getEntry(i + " Mag Str").setString(coder.getMagnetHealth().toString());

                //faultTable.getEntry(i).setString(readCANCoderFaults(coder));
                //stickyTable.getEntry(i).setString(readCANCoderStickyFaults(coder));
                
            } else if(item instanceof SparkMax) {
                spark = (SparkMax)item;

                commandTable.getEntry(i).setDouble(spark.getAppliedOutput()*spark.getBusVoltage());
                currentTable.getEntry(i).setDouble(spark.getOutputCurrent());
                faultTable.getEntry(i).setString(readFaultStruct(spark.getFaults()));
                stickyTable.getEntry(i).setString(readFaultStruct(spark.getStickyFaults()));
                warningTable.getEntry(i).setString(readFaultStruct(spark.getWarnings()));
                stickyWarningTable.getEntry(i).setString(readFaultStruct(spark.getStickyWarnings()));
                tempTable.getEntry(i).setDouble(spark.getMotorTemperature());
                canStatusTable.getEntry(i).setString(spark.getLastError().name());
            } else if(item instanceof SparkFlex) {
                sparkFlex = (SparkFlex)item;

                commandTable.getEntry(i).setDouble(sparkFlex.getAppliedOutput()*sparkFlex.getBusVoltage());
                currentTable.getEntry(i).setDouble(sparkFlex.getOutputCurrent());
                faultTable.getEntry(i).setString(readFaultStruct(sparkFlex.getFaults()));
                stickyTable.getEntry(i).setString(readFaultStruct(sparkFlex.getStickyFaults()));
                warningTable.getEntry(i).setString(readFaultStruct(sparkFlex.getWarnings()));
                stickyWarningTable.getEntry(i).setString(readFaultStruct(sparkFlex.getStickyWarnings()));
                tempTable.getEntry(i).setDouble(sparkFlex.getMotorTemperature());
                canStatusTable.getEntry(i).setString(sparkFlex.getLastError().name());
            }  else {
                //unknown table
            }
        }

        if(pdp != null) {
            for(int i=0; i<pdpNames.length; i++) {
                if(pdpNames[i] != null) {
                    currentTable.getEntry("PDP Current " + pdpNames[i]).setDouble(pdp.getCurrent(i));
                } else {
                }
            }
            sensorTable.getEntry("PDP Voltage").setDouble(pdp.getVoltage());
            sensorTable.getEntry("PDP Total Current").setDouble(pdp.getTotalCurrent());
            tempTable.getEntry("PDP").setDouble(pdp.getTemperature());
            faultTable.getEntry("PDP").setString(readFaultStruct(pdp.getFaults()));
            stickyTable.getEntry("PDP").setString(readFaultStruct(pdp.getStickyFaults()));
            //no CAN status to report
        }

        if(ph != null) {
            var solenoids = ph.getSolenoids();
            for(int i=0; i<pneumaticNames.length; i++) {
                if(pneumaticNames[i] != null) {
                    commandTable.getEntry(pneumaticNames[i]).setBoolean((solenoids & (1 << i)) == 1);
                }
            }
            var volts = ph.getAnalogVoltage(0);
            sensorTable.getEntry("Pressure Sensor").setDouble((volts - 0.5) / VOLTS_PER_PSI);
            sensorTable.getEntry("Pressure Sensor Voltage").setDouble(volts);
            currentTable.getEntry("Compressor").setDouble(ph.getCompressorCurrent());
            currentTable.getEntry("Solenoids").setDouble(ph.getSolenoidsTotalCurrent());
            faultTable.getEntry("PH").setString(readFaultStruct(ph.getFaults()));
            stickyTable.getEntry("PH").setString(readFaultStruct(ph.getStickyFaults()));
            //no CAN status to report
        }

        if(pigeon != null) {
            var yaw = pigeon.getYaw().getValueAsDouble();
            var pitch = pigeon.getPitch().getValueAsDouble();
            var roll = pigeon.getRoll().getValueAsDouble();

            sensorTable.getEntry("Pigeon Yaw").setDouble(yaw);
            sensorTable.getEntry("Pigeon Pitch").setDouble(pitch);
            sensorTable.getEntry("Pigeon Roll").setDouble(roll);

            var accel_x = pigeon.getAccelerationX().getValueAsDouble();
            var accel_y = pigeon.getAccelerationY().getValueAsDouble();
            var accel_z = pigeon.getAccelerationZ().getValueAsDouble();

            sensorTable.getEntry("Pigeon Ax").setDouble(accel_x);
            sensorTable.getEntry("Pigeon Ay").setDouble(accel_y);
            sensorTable.getEntry("Pigeon Az").setDouble(accel_z);

            //canStatusTable.getEntry("Pigeon").setString(pigeon.getLastError().name());

            /*if (pigeon instanceof PigeonIMU) {
                var p1 = (PigeonIMU)pigeon;
                PigeonIMU_Faults p1Faults = new PigeonIMU_Faults();
                p1.getFaults(p1Faults);
                faultTable.getEntry("Pigeon").setString(readFaultStruct(p1Faults));

                p1.getStickyFaults(p1Faults);
                stickyTable.getEntry("Pigeon").setString(readFaultStruct(p1Faults));
            } else if (pigeon instanceof Pigeon2) {
                var p2 = (Pigeon2)pigeon;
                Pigeon2_Faults p2Faults = new Pigeon2_Faults();
                p2.getFaults(p2Faults);
                faultTable.getEntry("Pigeon").setString(readFaultStruct(p2Faults));

                p2.getStickyFaults(p2Faults);
                stickyTable.getEntry("Pigeon").setString(readFaultStruct(p2Faults));
            } else {
                //unknown pigeon
            }*/
        }

        var canStatus = RobotController.getCANStatus();
        canStatusTable.getEntry("CAN Bandwidth").setDouble(canStatus.percentBusUtilization);
        canStatusTable.getEntry("CAN Bus Off Count").setDouble(canStatus.busOffCount);
        canStatusTable.getEntry("CAN RX Error Count").setDouble(canStatus.receiveErrorCount);
        canStatusTable.getEntry("CAN Tx Error Count").setDouble(canStatus.transmitErrorCount);
        canStatusTable.getEntry("CAN Tx Full Count").setDouble(canStatus.txFullCount);

        sensorTable.getEntry("Rio 3.3V Voltage").setDouble(RobotController.getVoltage3V3());
        sensorTable.getEntry("Rio 5V Voltage").setDouble(RobotController.getVoltage5V());
        sensorTable.getEntry("Rio 6V Voltage").setDouble(RobotController.getVoltage6V());
        sensorTable.getEntry("Rio 3.3V Current").setDouble(RobotController.getCurrent3V3());
        sensorTable.getEntry("Rio 5V Current").setDouble(RobotController.getCurrent5V());
        sensorTable.getEntry("Rio 6V Current").setDouble(RobotController.getCurrent6V());

        checkClearFaults(false);

        Set<String> keys0 = faultTable.getKeys();
        Set<String> stickyKeys0 = stickyTable.getKeys();

        String[] keys = new String[keys0.size()];
        String[] stickyKeys = new String[stickyKeys0.size()];
        
        keys0.toArray(keys);
        stickyKeys0.toArray(stickyKeys);
        
        faultSet = false;
        for(String i: keys) {
            var faultName = faultTable.getEntry(i).getString("EROR");
            if(!faultName.equals("Ok")) {
                faultSet = true;
            }
        }
        
        sfaultSet = false;
        for(String i: stickyKeys) {
            var faultName = stickyTable.getEntry(i).getString("EROR");
            if(!faultName.equals("Ok")) {
                sfaultSet = true;
            }
        }
    }

    private String readFaultStruct(Object obj) {
        StringBuilder work = new StringBuilder();

        //iterate through all the fields and find the boolean ones
        Field fieldlist[] = obj.getClass().getDeclaredFields();
        for (int i = 0; fieldlist.length > i; i++) {
            Field fld = fieldlist[i];
            if (fld.getType().equals(boolean.class)) {
                try {
                    boolean value = fld.getBoolean(obj);
                    if(value) {
                        work.append(fld.getName() + " ");
                    }
                } catch (IllegalArgumentException e) {
                    System.out.println("IllegalFaultReadArg");
                } catch (IllegalAccessException e) {
                    System.out.println("IllegalFaultReadAccess");
                }
            }
        }

        //check if string is empty
        if(work.length() == 0) {
            work.append("Ok");
        }
        return work.toString();
    }

    private String readCANCoderFaults(CANcoder coder){
        if(coder.getFaultField().getValue() == 0){
            //No Faults
            return "No Active Faults";
        }
        StringBuilder work = new StringBuilder();

        if(coder.getFault_BadMagnet().getValue()){work.append("BadMagnet ");}
        if(coder.getFault_BootDuringEnable().getValue()){work.append("BootDuringEnable ");}
        if(coder.getFault_Hardware().getValue()){work.append("Hardware ");}
        if(coder.getFault_Undervoltage().getValue()){work.append("UnderVoltage ");}
        if(coder.getFault_UnlicensedFeatureInUse().getValue()){work.append("UnlicensedFeatureInUse ");}

        return work.toString();
    }

    private String readCANCoderStickyFaults(CANcoder coder){
        if(coder.getFaultField().getValue() == 0){
            //No Faults
            return "No Active Faults";
        }
        StringBuilder work = new StringBuilder();

        if(coder.getStickyFault_BadMagnet().getValue()){work.append("BadMagnet ");}
        if(coder.getStickyFault_BootDuringEnable().getValue()){work.append("BootDuringEnable ");}
        if(coder.getStickyFault_Hardware().getValue()){work.append("Hardware ");}
        if(coder.getStickyFault_Undervoltage().getValue()){work.append("UnderVoltage ");}
        if(coder.getStickyFault_UnlicensedFeatureInUse().getValue()){work.append("UnlicensedFeatureInUse ");}
        
        return work.toString();
    }

    private String readSparkFaults(SparkBase.Faults faults) {
        if(faults.rawBits == 0) {
            //No Active Faults
            return "No Active Faults";
        }else{
            return "Device Faulted";
        }
        /*StringBuilder work = new StringBuilder();

        if(faults.other){work.append("Other ");}
        if(faults.motorType){work.append("MotorType ");}
        if(faults.sensor){work.append("Sensor ");}
        if(faults.can){work.append("CAN ");}
        if(faults.temperature){work.append("Temperature ");}
        if(faults.gateDriver){work.append("GateDriver ");}
        if(faults.escEeprom){work.append("ESC_EEPROM ");}
        if(faults.firmware){work.append("Firmware ");}

        return work.toString();*/
    }
    
    private String readSparkWarnings(SparkBase.Warnings warnings) {
        if(warnings.rawBits == 0) {
            //No Active warnings
            return "No Active Warnings";
        }
        StringBuilder work = new StringBuilder();

        if(warnings.brownout){work.append("Brownout ");}
        if(warnings.escEeprom){work.append("ESC_EEPROM ");}
        if(warnings.extEeprom){work.append("EXT_EEPROM ");}
        if(warnings.hasReset){work.append("HasReset ");}
        if(warnings.other){work.append("Other ");}
        if(warnings.overcurrent){work.append("Overcurrent ");}
        if(warnings.sensor){work.append("Sensor ");}
        if(warnings.stall){work.append("Stall ");}

        return work.toString();
    }

    public static void checkClearFaults(boolean clear) {
        var clearFaults = SmartDashboard.getBoolean("Clear Faults", false);

        if(clearFaults == false && clear == false) {
            return;
        }
        SmartDashboard.putBoolean("Clear Faults", false);

        for (String name : items.keySet()) {
            var item = items.get(name);
            /*if(item instanceof BaseTalon) {
                var talon = (BaseTalon)item;
                talon.clearStickyFaults();
            } else if(item instanceof CANCoder) {*/
            if(item instanceof CANcoder) {
                var coder = (CANcoder)item;
                coder.clearStickyFaults();
            } else if(item instanceof SparkMax) {
                spark = (SparkMax)item;
                spark.clearFaults();
            } else {
                //unknown table
            }
        }

        if(pdp != null) {
            pdp.clearStickyFaults();
        }

        if(ph != null) {
            ph.clearStickyFaults();
        }

        if(pigeon != null) {
            pigeon.clearStickyFaults();
        }
    }

    public static boolean FaultSet() {
        return faultSet;
    }

    public static boolean StickyFaultSet() {
        return sfaultSet;
    }
}
