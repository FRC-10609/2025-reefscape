package frc.robot.subsystems.Vision;

import edu.wpi.first.math.geometry.Transform3d;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.subsystems.Vision.AprilTagVisionIO.UnloggableAprilTagVisionIOInputs;
import frc.robot.subsystems.Vision.AprilTagVisionIO.LoggableAprilTagVisionIOInputs;
import java.util.Optional;
import org.photonvision.EstimatedRobotPose;

public class AprilTagVision extends SubsystemBase {

  private AprilTagVisionIO io;
  private LoggableAprilTagVisionIOInputs loggableInputs =
      new LoggableAprilTagVisionIOInputs();
  private UnloggableAprilTagVisionIOInputs unloggableInputs =
      new UnloggableAprilTagVisionIOInputs();

  public AprilTagVision(AprilTagVisionIO io) {
    this.io = io;
  }

  public double autoRotate() {
    return io.autoRotate();
  }

  public double autoTranslateX() {
    return io.autoTranslateX();
  }

  public double autoTranslateY() {
    return io.autoTranslateY();
  }

  public boolean hasTargets() {
    return loggableInputs.hasTargets;
  }

  public double getArea() {
    return io.getArea();
  }

  public void periodic() {
    updateInputs();
  }

  public void updateInputs() {
    io.updateInputs(loggableInputs, unloggableInputs);
  }

  public Optional<EstimatedRobotPose> getEstimatedPose() {
    return unloggableInputs.latestEstimatedPose;
  }

  public Transform3d getCamToTag() {
    return loggableInputs.latestCamToTagTranslation;
  }
}