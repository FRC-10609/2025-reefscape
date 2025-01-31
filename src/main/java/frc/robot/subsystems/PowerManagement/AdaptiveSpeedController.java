package frc.robot.subsystems.PowerManagement;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AdaptiveSpeedController {
  private final IBrownOutDetector brownOutDetector;
  private double slewRate;
  private double brakeRatio;
  private double maxRatio;
  private AdaptiveSlewRateLimiter forwardSpeedlimiter;
  private AdaptiveSlewRateLimiter strafespeedlimiter;
  private AdaptiveSlewRateLimiter turnrateLimiter;

  public AdaptiveSpeedController(IBrownOutDetector brownOutDetector, double slewRate, double brakeRatio, double maxRatio) {
    this.brownOutDetector = brownOutDetector;
    this.slewRate = slewRate;
    this.brakeRatio = brakeRatio;
    this.maxRatio = maxRatio;
    forwardSpeedlimiter = new AdaptiveSlewRateLimiter(slewRate);
    strafespeedlimiter = new AdaptiveSlewRateLimiter(slewRate * .95);
    turnrateLimiter = new AdaptiveSlewRateLimiter(slewRate * 1.8);
  }
  
  public SpeedCommand GetSpeedCommand(double forward, double strafe, double rotation, Boolean brake) {
    var ratio = maxRatio;
    if (brake) {
      ratio *= brakeRatio;
    }
    var forwardSpeed = forwardSpeedlimiter.calculate(forward) * ratio;
    var strafeSpeed = strafespeedlimiter.calculate(strafe) * ratio;
    var rot = turnrateLimiter.calculate(rotation) * ratio;
    
    CheckBrownouts();

    return new SpeedCommand(forwardSpeed, strafeSpeed, rot);
  }

  void CheckBrownouts() {
    if (brownOutDetector.NeedsLimiting()) {
      if (slewRate > 2.0) {
        ChangeSlewRate(slewRate -= 0.1);
      } else if (maxRatio > 0.5) {
        maxRatio -= 0.05;
      }
    }
    SmartDashboard.putNumber("SlewRate", slewRate);
    SmartDashboard.putNumber("Max Ratio", maxRatio);
  }

  void ChangeSlewRate(double rate) {
    forwardSpeedlimiter.ChangeRate(rate);
    strafespeedlimiter.ChangeRate(rate * .95);
    turnrateLimiter.ChangeRate(rate * 1.8);
  }
}