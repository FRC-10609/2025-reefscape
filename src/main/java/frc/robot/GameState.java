package frc.robot;

public final class GameState {
    private enum Mode {
        kNone,
        kDisabled,
        kAutonomous,
        kTeleop,
        kTest
      }
    
    private static Mode mode = Mode.kNone;
  
    
    // after subsystems are called, clear the first flag
    public static void robotPeriodic()
    {
        _isFirst = false;
    }
    
    public static boolean isDisabled() {
        return mode == Mode.kDisabled;
    }
    
    private static boolean _isEnabled;
    public static boolean isEnabled() {
        return _isEnabled;
    }
    
    private static boolean _isReal = true;
    public static boolean isReal() {
        return _isReal;
    }
    
    private static boolean _isEStopped;
    public static boolean isEStopped() {
        return _isEStopped;
    }
    
    private static boolean _isFirst = true;
    public static boolean isFirst() {
        return _isFirst;
    }

    public static boolean isTeleop() {
        return mode == Mode.kTeleop;
    }

    public static boolean isAutonomous() {
        return mode == Mode.kAutonomous;
    }

    public static boolean isTest() {
        return mode == Mode.kTest;
    }

    public static void disabledInit() {
        mode = Mode.kDisabled;
        _isFirst = true;
    }

    public static void autonomousInit() {
        mode = Mode.kAutonomous;
        _isFirst = true;
    }

    public static void teleopInit() {
        mode = Mode.kTeleop;
        _isFirst = true;
    }

    public static void testInit() {
        mode = Mode.kTest;
        _isFirst = true;
    }

    public static void simulationInit() {
        _isReal = false;
    }

}
