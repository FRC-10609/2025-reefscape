package frc.robot.subsystems.SwerveDrive;

import static edu.wpi.first.units.Units.*;

import frc.robot.Logger;
import frc.robot.subsystems.Vision.Limelight;

import com.ctre.phoenix6.hardware.Pigeon2;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.config.RobotConfig;
import com.pathplanner.lib.controllers.PPHolonomicDriveController;
import com.pathplanner.lib.config.PIDConstants;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import edu.wpi.first.math.VecBuilder;
import edu.wpi.first.math.Vector;

import edu.wpi.first.math.estimator.SwerveDrivePoseEstimator;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.math.kinematics.SwerveDriveKinematics;
import edu.wpi.first.math.kinematics.SwerveDriveOdometry;
import edu.wpi.first.math.kinematics.SwerveModulePosition;
import edu.wpi.first.math.kinematics.SwerveModuleState;
import edu.wpi.first.math.numbers.N3;
import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.sysid.SysIdRoutine;
import frc.robot.GameState;

public class DriveSubsystem extends SubsystemBase{
  
  public static boolean isTeleOp = false;

  public boolean isTurning = false;
  public double targetAngle = 0.0;
  public double turnCommand = 0.0;
  public double fieldXCommand = 0;
  public double fieldYCommand = 0;

  ChassisSpeeds speedCommands = new ChassisSpeeds(0, 0, 0);
  ChassisSpeeds relativeCommands = new ChassisSpeeds(0,0,0);

  private final SwerveModule frontLeft = new SwerveModule(
    DrivebaseCfg.FRONT_LEFT_MOD_ID,
    ChassisMotorCfg.DRIVE_FRONT_LEFT, ChassisMotorCfg.ANGLE_FRONT_LEFT, 
    CANCoderCfg.FRONT_LEFT_CAN_CODER);

  private final SwerveModule frontRight = new SwerveModule(
    DrivebaseCfg.FRONT_RIGHT_MOD_ID,
    ChassisMotorCfg.DRIVE_FRONT_RIGHT, ChassisMotorCfg.ANGLE_FRONT_RIGHT, 
    CANCoderCfg.FRONT_RIGHT_CAN_CODER);

  private final SwerveModule backLeft = new SwerveModule(
    DrivebaseCfg.BACK_LEFT_MOD_ID,
    ChassisMotorCfg.DRIVE_BACK_LEFT, ChassisMotorCfg.ANGLE_BACK_LEFT, 
    CANCoderCfg.BACK_LEFT_CAN_CODER);

  private final SwerveModule backRight = new SwerveModule(
    DrivebaseCfg.BACK_RIGHT_MOD_ID,
    ChassisMotorCfg.DRIVE_BACK_RIGHT, ChassisMotorCfg.ANGLE_BACK_RIGHT, 
    CANCoderCfg.BACK_RIGHT_CAN_CODER);

  private final Pigeon2 gyro = IMU_Cfg.IMU;

  private final SwerveDriveKinematics kinematics = DrivebaseCfg.KINEMATICS;

  //public final SwerveDrivePoseEstimator odometry;
  public final SwerveDriveOdometry odometry;

  public final SwerveDrivePoseEstimator poseEstimator;

  private Pose2d pose = new Pose2d();
  private Pose2d estimatedPose = new Pose2d();
  private Pose2d limelightPose = new Pose2d();

  // private static Limelight limelight = new Limelight();

  //Create a SysIdRoutine object for characterizing the drive
  private final SysIdRoutine sysIdRoutine = 
  new SysIdRoutine(
    //Create a new SysID Congig with default ramp rate (0.1 V/s), step (7V), and time out values
    new SysIdRoutine.Config(), 
    new SysIdRoutine.Mechanism(
      voltage -> {
        frontLeft.setSysIDVoltage(voltage);
        frontRight.setSysIDVoltage(voltage);
        backLeft.setSysIDVoltage(voltage);
        backRight.setSysIDVoltage(voltage);},
      // Tell SysId how to record a frame of data for each motor on the mechanism being
      // characterized.
      log -> {
        //Log a frame for the frontLeft Motor
        log.motor("drive-frontLeft")
          .voltage(frontLeft.getDriveMotorVoltage())
          .linearPosition(frontLeft.getLinearPositionMeters())
          .linearVelocity(frontLeft.getModuleVelocityMetersPerSec());
        //Log a frame for the frontRight Motor
        log.motor("drive-frontRight")
          .voltage(frontRight.getDriveMotorVoltage())
          .linearPosition(frontRight.getLinearPositionMeters())
          .linearVelocity(frontRight.getModuleVelocityMetersPerSec());
        //Log a frame for the backLeft Motor
        log.motor("drive-backLeft")
          .voltage(backLeft.getDriveMotorVoltage())
          .linearPosition(backLeft.getLinearPositionMeters())
          .linearVelocity(backLeft.getModuleVelocityMetersPerSec());
        //Log a frame for the backRight Motor
        log.motor("drive-backRight")
          .voltage(backRight.getDriveMotorVoltage())
          .linearPosition(backRight.getLinearPositionMeters())
          .linearVelocity(backRight.getModuleVelocityMetersPerSec());
      },
      // Tell SysId to make generated commands require this subsystem, suffix test state in
      // WPILog with this subsystem's name ("drive")
      this));
  
  public Command sysIdQuasistatic(SysIdRoutine.Direction direction){
    return sysIdRoutine.quasistatic(direction);
  }

  public Command sysIdDynamic(SysIdRoutine.Direction direction){
    return sysIdRoutine.dynamic(direction);
  }

  public DriveSubsystem() {

    //this.odometry = new SwerveDrivePoseEstimator(kinematics, getGyroRotation2d(), getModulePositions(), pose);
    this.odometry = new SwerveDriveOdometry(kinematics, getGyroRotation2d(), getModulePositions());

    this.poseEstimator = new SwerveDrivePoseEstimator(
      kinematics, 
      getGyroRotation2d(), 
      getModulePositions(),
      estimatedPose,
      createStateStdDevs(
          PoseEstCfg.POSITION_STD_DEV_X,
          PoseEstCfg.POSITION_STD_DEV_Y,
          PoseEstCfg.POSITION_STD_DEV_THETA),
      createVisionMeasurementStdDevs(
          PoseEstCfg.VISION_STD_DEV_X,
          PoseEstCfg.VISION_STD_DEV_Y,
          PoseEstCfg.VISION_STD_DEV_THETA));

    reset();
    registerLoggerObjects();

    //Configure Auto Builder last!
    configAutoBuilder(); 
  }

  private void checkInitialAngle() {
    if (GameState.isTeleop() && GameState.isFirst()) { 
      targetAngle = getIMU_Yaw();
    }
  }

  private double getIMU_Yaw() {
    var currentHeading = gyro.getYaw(); 
    return(currentHeading.getValueAsDouble());
  }

  private void updateDashboard(){

    //Field Oriented inputs
    SmartDashboard.putNumber("Field Oriented X Command (Forward)", fieldXCommand);
    SmartDashboard.putNumber("Field Oriented Y Command (Forward)", fieldYCommand);

    //Robot Relative inputs
    /*SmartDashboard.putNumber("Robot Relative vX Speed Command", speedCommands.vxMetersPerSecond);
    SmartDashboard.putNumber("Robot Relative vY Speed Command", speedCommands.vyMetersPerSecond);
    SmartDashboard.putNumber("Robot Relative Rotation Command", speedCommands.omegaRadiansPerSecond);*/

    /*SmartDashboard.putNumber("Drive Robot Relative vX Speed Command", relativeCommands.vxMetersPerSecond);
    SmartDashboard.putNumber("Drive Robot Relative vY Speed Command", relativeCommands.vyMetersPerSecond);*/
    SmartDashboard.putNumber("Drive Robot Relative Rotation Command", relativeCommands.omegaRadiansPerSecond);

    SmartDashboard.putNumber("Gyro Yaw", getIMU_Yaw());
    SmartDashboard.putNumber("Target Angle", targetAngle);

    //Pose Info
    SmartDashboard.putString("FMS Alliance", DriverStation.getAlliance().toString());
    SmartDashboard.putNumber("Pose2D X", pose.getX());
    SmartDashboard.putNumber("Pose2D Y", pose.getY());
    SmartDashboard.putNumber("Pose2D Rotation", pose.getRotation().getDegrees());

    SmartDashboard.putNumber("EstimatedPose X", estimatedPose.getX());
    SmartDashboard.putNumber("EstimatedPose Y", estimatedPose.getY());
    SmartDashboard.putNumber("EstimatedPose Rotation", estimatedPose.getRotation().getDegrees());
  }
  
  @Override
  public void periodic() {
    checkInitialAngle();
    updateOdometry();
    updateEstimatedPose();
    // limelight.update();
    // limelightPose = limelight.getVisionBotPose();
    
    // if (limelightPose != null) { // Limelight mode
      
    //   double currentTimestamp = limelight.getTimestampSeconds(limelight.getTotalLatency());
      
    //   if (limelight.visionAccurate(limelightPose)) 
    //   {
    //     poseEstimator.addVisionMeasurement(limelightPose, currentTimestamp);
    //   }
    // }
    updateDashboard();
  }
  
  public void drive(double xSpeed, double ySpeed, double rot, boolean fieldRelative) {
    checkInitialAngle();

    if (GameState.isTeleop()) {
      if (Math.abs(rot) > 0) {
        //Driver is commanding rotation, 
        isTurning = true;
        targetAngle = getIMU_Yaw();
      } 
      else if (rot == 0 && isTurning) {
        //Driver stopped commanding a turn
        isTurning = false;
      }

      if (isTurning) {
        turnCommand = rot;
      }
      else { 
        turnCommand = (targetAngle - getIMU_Yaw()) * DrivebaseCfg.GO_STRAIGHT_GAIN;
      }
      
    }
    
    //Set Dashboard variables
    fieldXCommand = xSpeed;
    fieldYCommand = ySpeed;

    if(fieldRelative){
      speedCommands = ChassisSpeeds.fromFieldRelativeSpeeds(xSpeed, ySpeed, turnCommand, getGyroRotation2d());
    } else {
      speedCommands.omegaRadiansPerSecond = turnCommand;
      speedCommands.vxMetersPerSecond = xSpeed;
      speedCommands.vyMetersPerSecond = ySpeed;
    }

    driveRobotRelative(speedCommands);
  }

  public void driveRobotRelative(ChassisSpeeds robotRelativeSpeeds){
    //This method is a consumer of ChassisSpeed and sets the corresponding module states.  This is required for PathPlanner 2024
    //Save off to SmartDashboard
    relativeCommands.vxMetersPerSecond = robotRelativeSpeeds.vxMetersPerSecond;
    relativeCommands.vyMetersPerSecond = robotRelativeSpeeds.vyMetersPerSecond;
    relativeCommands.omegaRadiansPerSecond = robotRelativeSpeeds.omegaRadiansPerSecond;
    
    //Convert from robot frame of reference (ChassisSpeeds) to swerve module frame of reference (SwerveModuleState)
    var swerveModuleStates = kinematics.toSwerveModuleStates(robotRelativeSpeeds);
    //Normalize wheel speed commands to make sure no speed is greater than the maximum achievable wheel speed.
    SwerveDriveKinematics.desaturateWheelSpeeds(swerveModuleStates, DrivebaseCfg.MAX_SPEED_METERS_PER_SECOND);

    //Set the speed and angle of each module
    setDesiredState(swerveModuleStates);
  }

  public ChassisSpeeds getRobotRelativeSpeeds(){
    //This method is a supplier of ChassisSpeeds as determined by the module states.  This is required for PathPlanner 2024
    return kinematics.toChassisSpeeds(getModuleStates());
  }

  public void setDesiredState(SwerveModuleState[] swerveModuleStates) {
    frontLeft.setDesiredState(swerveModuleStates[0]);
    frontRight.setDesiredState(swerveModuleStates[1]);
    backLeft.setDesiredState(swerveModuleStates[2]);
    backRight.setDesiredState(swerveModuleStates[3]);
  }

  public SwerveModuleState[] getModuleStates(){
    return new SwerveModuleState[] {
      frontLeft.getState(),
      frontRight.getState(),
      backLeft.getState(),
      backRight.getState()};
  }

  public void updateOdometry() {
    pose = odometry.update(
        getGyroRotation2d(),
        new SwerveModulePosition[] {
          frontLeft.getPosition(),
          frontRight.getPosition(),
          backLeft.getPosition(),
          backRight.getPosition()
        });
  }

  public void updateEstimatedPose(){
    estimatedPose = poseEstimator.update(
      getGyroRotation2d(), 
      getModulePositions());
  }


  public void resetOdometry(Pose2d pose) {
    odometry.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
    poseEstimator.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
  }

  public void resetPoseEstimation(Pose2d pose) {
    poseEstimator.resetPosition(getGyroRotation2d(), getModulePositions(), pose);
  }
  
  public SwerveModulePosition[] getModulePositions() {
    //Returns 
    return new SwerveModulePosition[] {
      frontLeft.getPosition(),
      frontRight.getPosition(),
      backLeft.getPosition(),
      backRight.getPosition()
    };
  }
  
  public SwerveModuleState[] makeSwerveModuleState(double[] speeds, double[] angles) {
    SwerveModuleState[] moduleStates = new SwerveModuleState[angles.length];
    for(int i = 0; i < angles.length; i++) moduleStates[i] = new SwerveModuleState(speeds[i], new Rotation2d(Units.degreesToRadians(angles[i])));
    return moduleStates;
  }

  public void setToBrake() {
    resetModules();
    double[] speeds = {0, 0, 0, 0};
    double[] angles = {90, 90, 90, 90};
    SwerveModuleState[] moduleStates = makeSwerveModuleState(speeds, angles);
    setDesiredState(moduleStates);
  }

  public Rotation2d getGyroRotation2d() {
    return new Rotation2d(Units.degreesToRadians(getIMU_Yaw()));
  }

  public Pose2d getPose2d() {
    //return odometry.getEstimatedPosition();
    return odometry.getPoseMeters();
  }

  public double getPoseRotationDegrees(){
    return pose.getRotation().getDegrees();
  }

  public void resetGyro(double angle) {
    gyro.setYaw(angle);
    targetAngle = angle;
  }

  public void resetModules() {
    frontLeft.zeroModule();
    frontRight.zeroModule();
    backLeft.zeroModule();
    backRight.zeroModule();
  }

  public void reset() {
    resetGyro(0);
    resetModules();
    resetOdometry(pose);
  }

  @SuppressWarnings("unused")
  private void configAutoBuilder(){
    //Wrapper for AutoBuilder.configure, must be called from DriveTrain config....

    /*From Path Planner example code 
    https://github.com/mjansen4857/pathplanner/blob/main/examples/java/src/main/java/frc/robot/subsystems/SwerveSubsystem.java*/

    // Load the RobotConfig from the GUI settings. You should probably
    // store this in your Constants file
    RobotConfig config;
    try{
      config = RobotConfig.fromGUISettings();

      AutoBuilder.configure(
        this::getPose2d, //Robot pose supplier
        this::resetOdometry, //Method to reset odometry (will be called if the robot has a starting pose)
        this::getRobotRelativeSpeeds, //ChassisSpeeds provider.  MUST BE ROBOT RELATIVE!!! 
        this::driveRobotRelative, //ChassisSpeeds consumer.  MUST BE ROBOT RELATIVE!!!
        new PPHolonomicDriveController(
                new PIDConstants(5.0, 0, 0), //Translation PID constants
                new PIDConstants(5.0, 0, 0)), //Rotation PID constants
        config,
        () -> {
          // Boolean supplier that controls when the path will be mirrored for the red alliance
          // This will flip the path being followed to the red side of the field.
          // THE ORIGIN WILL REMAIN ON THE BLUE SIDE
        
          var alliance = DriverStation.getAlliance();
          if ((alliance.isPresent()) &&
              (!DrivebaseCfg.IS_FIELD_MIRRORED)){//This causes a warning for dead code if configuration is true
                return alliance.get() == DriverStation.Alliance.Red;
            }
              return false;
          },
        this //Reference to this subsystem to set 
      );

    } catch (Exception e) {
      // Handle exception as needed
      DriverStation.reportError("Failed to load PathPlanner config and configure AutoBuilder", e.getStackTrace());
    }
  }

  private void registerLoggerObjects(){
    Logger.RegisterSparkFlex("FL Drive", ChassisMotorCfg.DRIVE_FRONT_LEFT);
    Logger.RegisterSparkFlex("FR Drive", ChassisMotorCfg.DRIVE_FRONT_RIGHT);
    Logger.RegisterSparkFlex("RL Drive", ChassisMotorCfg.DRIVE_BACK_LEFT);
    Logger.RegisterSparkFlex("RR Drive", ChassisMotorCfg.DRIVE_BACK_RIGHT);

    Logger.RegisterSparkMax("FL Turn", ChassisMotorCfg.ANGLE_FRONT_LEFT);
    Logger.RegisterSparkMax("FR Turn", ChassisMotorCfg.ANGLE_FRONT_RIGHT);
    Logger.RegisterSparkMax("RL Turn", ChassisMotorCfg.ANGLE_BACK_LEFT);
    Logger.RegisterSparkMax("RR Turn", ChassisMotorCfg.ANGLE_BACK_RIGHT);

    Logger.RegisterPigeon(IMU_Cfg.IMU);

    Logger.RegisterCanCoder("FL Abs Position", CANCoderCfg.FRONT_LEFT_CAN_CODER);
    Logger.RegisterCanCoder("FR Abs Position", CANCoderCfg.FRONT_RIGHT_CAN_CODER);
    Logger.RegisterCanCoder("RL Abs Position", CANCoderCfg.BACK_LEFT_CAN_CODER);
    Logger.RegisterCanCoder("RR Abs Position", CANCoderCfg.BACK_RIGHT_CAN_CODER);

    Logger.RegisterSensor("Front Left Angle Command",   ()->frontLeft.getCommandedAngle());
    Logger.RegisterSensor("Front Right Angle Command",  ()->frontRight.getCommandedAngle());
    Logger.RegisterSensor("Back Left Angle Command",    ()->backLeft.getCommandedAngle());
    Logger.RegisterSensor("Back Right Angle Command",   ()->backRight.getCommandedAngle());

    Logger.RegisterSensor("Front Left Angle (radians)",  ()->frontLeft.getAbsPositionZeroed());
    Logger.RegisterSensor("Front Right Angle (radians)", ()->frontRight.getAbsPositionZeroed());
    Logger.RegisterSensor("Back Left Angle (radians)",   ()->backLeft.getAbsPositionZeroed());
    Logger.RegisterSensor("Back Right Angle (radians)",  ()->backRight.getAbsPositionZeroed());

    Logger.RegisterSensor("FL Drive Speed Command", ()->frontLeft.getCommandedSpeed());
    Logger.RegisterSensor("FR Drive Speed Command", ()->frontRight.getCommandedSpeed());
    Logger.RegisterSensor("RL Drive Speed Command", ()->backLeft.getCommandedSpeed());
    Logger.RegisterSensor("RR Drive Speed Command", ()->backRight.getCommandedSpeed());

    Logger.RegisterSensor("FL Drive Speed", ()->frontLeft.getVelocity());
    Logger.RegisterSensor("FR Drive Speed", ()->frontRight.getVelocity());
    Logger.RegisterSensor("RL Drive Speed", ()->backLeft.getVelocity());
    Logger.RegisterSensor("RR Drive Speed", ()->backRight.getVelocity());
  }

  /**
   * Creates a vector of standard deviations for the states. Standard deviations of model states.
   * Increase these numbers to trust your model's state estimates less.
   *
   * @param x in meters
   * @param y in meters
   * @param theta in degrees
   * @return the Vector of standard deviations need for the poseEstimator
   */
  public Vector<N3> createStateStdDevs(double x, double y, double theta) {
    return VecBuilder.fill(x, y, Units.degreesToRadians(theta));
  }

  /**
   * Creates a vector of standard deviations for the vision measurements. Standard deviations of
   * global measurements from vision. Increase these numbers to trust global measurements from
   * vision less.
   *
   * @param x in meters
   * @param y in meters
   * @param theta in degrees
   * @return the Vector of standard deviations need for the poseEstimator
   */
  public Vector<N3> createVisionMeasurementStdDevs(double x, double y, double theta) {
    return VecBuilder.fill(x, y, Units.degreesToRadians(theta));
  }


}
