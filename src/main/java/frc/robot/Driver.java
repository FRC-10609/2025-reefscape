package frc.robot;

import frc.robot.Constants.OperatorConstants;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.JoystickButton;
import edu.wpi.first.wpilibj2.command.button.POVButton;

public final class Driver {
  //public static final XboxController Controller = new XboxController(OperatorConstants.kDriverControllerPort);
  public static final CommandXboxController Controller = new CommandXboxController(OperatorConstants.kDriverControllerPort);

  public static double getLeftX() { return Controller.getLeftX();}
  public static double getLeftY() { return Controller.getLeftY();}
  public static double getLeftTrigger() { return Controller.getLeftTriggerAxis();}
  
  public static double getRightX() { return Controller.getRightX();}
  public static double getRightY() { return Controller.getRightY();}
  public static double getRightTrigger() { return Controller.getRightTriggerAxis();}

  /*public static final class XboxButtons {
    
    // Left side controls
    public static final JoystickButton LeftStick = new JoystickButton(Controller,XboxController.Button.kLeftStick.value);
    public static final JoystickButton LeftBumper = new JoystickButton(Controller, XboxController.Button.kLeftBumper.value); 


    // Right side controls
    public static final JoystickButton RightStick = new JoystickButton(Controller, XboxController.Button.kRightStick.value);
    public static final JoystickButton RightBumper = new JoystickButton(Controller, XboxController.Button.kRightBumper.value); 
    
    /*  Logitech 310 Buttons
    __                      _______
      \____________________/        \
      [Back]        [Start]    Y    |
             [Logi]          X + B  |
      [Mode]                   A    |

    */
    
    // Action Buttons
    /*public static final JoystickButton A = new JoystickButton(Controller, XboxController.Button.kA.value); 
    public static final JoystickButton B = new JoystickButton(Controller, XboxController.Button.kB.value); 
    public static final JoystickButton X = new JoystickButton(Controller, XboxController.Button.kX.value); 
    public static final JoystickButton Y = new JoystickButton(Controller, XboxController.Button.kY.value); 
    
    // Other buttons
    public static final JoystickButton Back = new JoystickButton(Controller, XboxController.Button.kBack.value);
    public static final JoystickButton Start = new JoystickButton(Controller, XboxController.Button.kStart.value);
    // "Mode" button swaps the D-pad and left stick
    // "Logitech" button is like Guide or Home

    // Directional Pad
    public static final POVButton North = new POVButton(Controller, 0);
    public static final POVButton NorthEast = new POVButton(Controller, 45);
    public static final POVButton East = new POVButton(Controller, 90);
    public static final POVButton SouthEast = new POVButton(Controller, 135);
    public static final POVButton South = new POVButton(Controller, 180);
    public static final POVButton SouthWest = new POVButton(Controller, 225);
    public static final POVButton West = new POVButton(Controller, 270);
    public static final POVButton NorthWest = new POVButton(Controller, 315);
  }*/
}