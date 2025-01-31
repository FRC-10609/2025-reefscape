package frc.robot.subsystems.PowerManagement;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;

// since SlewRateLimiters can't change their rate, we will swap them out
public class AdaptiveSlewRateLimiter {
  private SlewRateLimiter speedLimiter;
  private double value;

  public AdaptiveSlewRateLimiter(double rateLimit) {
    speedLimiter = new SlewRateLimiter(rateLimit);
  }
  
  public double calculate(double input){
    value = speedLimiter.calculate(MathUtil.applyDeadband(input, 0.1));
    return value;
  }
  
  public void ChangeRate(double rate) {
    speedLimiter = new SlewRateLimiter(rate);
    speedLimiter.reset(value);
  }
}