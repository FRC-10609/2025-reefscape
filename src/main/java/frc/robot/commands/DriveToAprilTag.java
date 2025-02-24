package frc.robot.commands;

import java.util.function.DoubleSupplier;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Transform2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.kinematics.ChassisSpeeds;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj2.command.Command;
import frc.robot.subsystems.SwerveDrive.DriveSubsystem;
import frc.robot.subsystems.Vision.AprilTagVision;

/** An example command that uses an example subsystem. */
public class DriveToAprilTag extends Command {
    @SuppressWarnings({ "PMD.UnusedPrivateField", "PMD.SingularField" })
    private final DriveSubsystem drive;
    private final AprilTagVision vision;
    private final DoubleSupplier xSupplier;
    private final DoubleSupplier ySupplier;
    private final DoubleSupplier omegaSupplier;

    /**
     * Creates a new ExampleCommand.
     *
     * @param subsystem The subsystem used by this command.
     */
    public DriveToAprilTag(DriveSubsystem drive, AprilTagVision vision, DoubleSupplier xSupplier,
            DoubleSupplier ySupplier, DoubleSupplier omegaSupplier) {
        this.drive = drive;
        this.vision = vision;
        this.xSupplier = xSupplier;
        this.ySupplier = ySupplier;
        this.omegaSupplier = omegaSupplier;
        // Use addRequirements() here to declare subsystem dependencies.
        addRequirements(drive);
        addRequirements(vision);
    }

    // Called when the command is initially scheduled.
    @Override
    public void initialize() {
    }

    // Called every time the scheduler runs while the command is scheduled.
    @Override
    public void execute() {
        final double DEADBAND = 0.1;
        double HEADING_P = .6;
        // double DRIVING_TOWARD_P = 0;
        double DRIVING_STRAFE_P = .6;

        // Apply deadband
        double linearMagnitude = MathUtil.applyDeadband(
                Math.hypot(-xSupplier.getAsDouble(), ySupplier.getAsDouble()), DEADBAND);
        Rotation2d linearDirection = new Rotation2d(-xSupplier.getAsDouble(), ySupplier.getAsDouble());
        double omega = MathUtil.applyDeadband(-omegaSupplier.getAsDouble(), DEADBAND);

        // Square values
        linearMagnitude = linearMagnitude * linearMagnitude;
        omega = Math.copySign(omega * omega, omega);

        // Calcaulate new linear velocity
        Translation2d linearVelocity = new Pose2d(new Translation2d(), linearDirection)
                .transformBy(new Transform2d(linearMagnitude, 0.0, new Rotation2d()))
                .getTranslation();

        // Convert to field relative speeds & send command
        boolean isFlipped = DriverStation.getAlliance().isPresent()
                && DriverStation.getAlliance().get() == Alliance.Red;

        if (vision.hasTargets()) {
            Transform2d cameraToTarget = new Transform2d(
                    vision.getCamToTag().getTranslation().toTranslation2d(),
                    vision.getCamToTag().getRotation().toRotation2d());

            double photonvisionCameraAngle = cameraToTarget.getRotation().getRadians();
            double robotToTagAngleDifference = Math.copySign(Math.PI - Math.abs(photonvisionCameraAngle),
                    photonvisionCameraAngle);

            // Projects vector pointing from camera to apriltag onto the plane of the
            // apriltag
            // Magnitude of the projection
            double strafeDistance = cameraToTarget.getX() * -Math.sin(photonvisionCameraAngle);
            // Projects vector pointing from camera to apriltag onto the normal of the plane
            // of the
            // apriltag
            // Magnitude of the projection
            double forwardDistance = cameraToTarget.getX() * Math.cos(photonvisionCameraAngle);

            linearVelocity = new Translation2d(
                    0, // MathUtil.clamp(-(forwardDistance - .5) * DRIVING_TOWARD_P, -1, 1),
                    MathUtil.clamp(strafeDistance * DRIVING_STRAFE_P, -1, 1))
                    .rotateBy(
                            drive
                                    .getPose().getRotation()
                                    .plus(Rotation2d.fromRadians(robotToTagAngleDifference)));

            double headingToTag = -Math.atan2(cameraToTarget.getY(), cameraToTarget.getX());
            omega = MathUtil.clamp(HEADING_P * headingToTag, -1, 1);
        }

        double driveXDecimal = MathUtil.clamp(
                MathUtil.clamp(MathUtil.applyDeadband(linearVelocity.getX(), .1), -.4, .4)
                        + MathUtil.applyDeadband(xSupplier.getAsDouble(), .02) * .3,
                -1,
                1);
        double driveYDecimal = MathUtil.clamp(
                MathUtil.clamp(MathUtil.applyDeadband(linearVelocity.getY(), .1), -.4, .4)
                        + MathUtil.applyDeadband(ySupplier.getAsDouble(), .02) * .3,
                -1,
                1);
        double omegaDecimal = MathUtil.clamp(
                MathUtil.clamp(MathUtil.applyDeadband(omega, .05), -.4, .4)
                        + MathUtil.applyDeadband(omegaSupplier.getAsDouble(), .02) * .2,
                -1,
                1);

        drive.driveRobotRelative(
                ChassisSpeeds.fromFieldRelativeSpeeds(
                        driveXDecimal * drive.getMaxLinearSpeedMetersPerSec(),
                        driveYDecimal * drive.getMaxLinearSpeedMetersPerSec(),
                        omegaDecimal * drive.getMaxAngularSpeedRadPerSec(),
                        isFlipped
                                ? drive.getPose().getRotation().plus(new Rotation2d(Math.PI))
                                : drive.getPose().getRotation()));
    }

    // Called once the command ends or is interrupted.
    @Override
    public void end(boolean interrupted) {
    }

    // Returns true when the command should end.
    @Override
    public boolean isFinished() {
        return false;
    }
}
