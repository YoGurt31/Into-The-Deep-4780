// Coded By Gurtej Singh

package Auton;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import RoadRunner.MecanumDrive;
import Robot.Robot;

@Config
@Autonomous(name = "67PtAuton", group = "Auton")
public class FourSampleAuton extends LinearOpMode {

    private final Robot robot = new Robot();

    OuttakeState outtakeState = OuttakeState.BASE;
    enum OuttakeState {
        BASE,
        COLLECTION,
        SCORING
    }

    private final int BASE = 0;
    private final int RAISED = 750;

    private final int RETRACTED = 0;
    private final int EXTENDED = 900;

    private final double Power = 1.0;
    private final double HOLD = 0.0005;

    ClawState clawState = ClawState.CLOSE;
    enum ClawState {
        OPEN,
        CLOSE
    }

    IntakeState intakeState = IntakeState.INACTIVE;
    enum IntakeState {
        ACTIVE,
        INACTIVE
    }

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Initializing...");
        telemetry.update();

        Pose2d initialPosition = new Pose2d(0, -62, Math.toRadians(90));
        MecanumDrive Drive = new MecanumDrive(hardwareMap, initialPosition); // Run At 50%
        MecanumDrive.PARAMS.maxWheelVel = 50;
        MecanumDrive.PARAMS.maxProfileAccel = 50;

        waitForStart();

        Actions.runBlocking(Drive.actionBuilder(initialPosition)
                // Robot Setup
                .stopAndAdd(new ClawAction(ClawState.CLOSE))
                .stopAndAdd(new OutTakeAction(OuttakeState.SCORING))
                .stopAndAdd(new VerticalSlideAction(RAISED))

                // Drive To Bucket And Score Sample
                .setReversed(false)
                .strafeToLinearHeading(new Vector2d(-53, -53), Math.toRadians(45))
                .waitSeconds(1)
                .setReversed(true)
                .strafeTo(new Vector2d(-60, -60))
                .waitSeconds(2)

                // Collect Sample #1
                .setReversed(false)
                .strafeToLinearHeading(new Vector2d(-48, -50), Math.toRadians(90))
                .waitSeconds(2)

                // Score Sample #1
                .setReversed(true)
                .strafeToLinearHeading(new Vector2d(-60, -60), Math.toRadians(45))
                .waitSeconds(2)

                // Collect Sample #2
                .setReversed(false)
                .strafeToLinearHeading(new Vector2d(-60, -50), Math.toRadians(90))
                .waitSeconds(2)

                // Score Sample #2
                .setReversed(true)
                .strafeToLinearHeading(new Vector2d(-60, -60), Math.toRadians(45))
                .waitSeconds(2)

                // Collect Sample #3
                .setReversed(false)
                .strafeToLinearHeading(new Vector2d(-60, -50), Math.toRadians(110))
                .waitSeconds(2)

                // Score Sample #3
                .setReversed(true)
                .strafeToLinearHeading(new Vector2d(-60, -60), Math.toRadians(45))
                .waitSeconds(2)

                // Park
                .setReversed(false)
                .splineTo(new Vector2d(-24, 0), 0)
                .waitSeconds(2)

                .build());
    }

    public class OutTakeAction implements Action {
        private final OuttakeState targetState;

        public OutTakeAction(OuttakeState state) {
            this.targetState = state;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setOuttakeState(outtakeState);
            telemetryPacket.put("Outtake State", targetState.toString());
            return false;
        }

        private void setOuttakeState(OuttakeState state) {
            switch (state) {
                case BASE:
                    robot.scoring.outtakeArmRotation.setPosition(0.0);
                    robot.scoring.clawPrimaryPivot.setPosition(0.0);
                    break;

                case COLLECTION:
                    robot.scoring.outtakeArmRotation.setPosition(0.38);
                    robot.scoring.clawPrimaryPivot.setPosition(0.05);
                    break;

                case SCORING: // Default
                    robot.scoring.outtakeArmRotation.setPosition(0.80);
                    robot.scoring.clawPrimaryPivot.setPosition(0.75);
                    break;
            }
        }
    }

    public class ClawAction implements Action {
        private final ClawState targetState;

        public ClawAction(ClawState state) {
            this.targetState = state;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setClawState(targetState);
            telemetryPacket.put("Claw State", targetState.toString());
            return false;
        }

        private void setClawState(ClawState state) {
            switch (state) {
                case OPEN:
                    robot.scoring.clawStatus.setPosition(0.25);
                    break;
                case CLOSE:
                    robot.scoring.clawStatus.setPosition(1.00);
                    break;
            }
        }
    }

    public class HorizontalSlideAction implements Action {
        private final int horizotalTargetPosition;

        public HorizontalSlideAction(int position) {
            this.horizotalTargetPosition = position;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            slidesToPosition(horizotalTargetPosition);
            telemetryPacket.put("Slide Target Position", horizotalTargetPosition);
            return false;
        }

        private void slidesToPosition(int position) {
            robot.scoring.horizontalSlideExtension.setTargetPosition(position);

            robot.scoring.horizontalSlideExtension.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.scoring.horizontalSlideExtension.setPower(Power);

            while (opModeIsActive() && (robot.scoring.horizontalSlideExtension.isBusy())) {
            }

            robot.scoring.horizontalSlideExtension.setPower(0);
            robot.scoring.horizontalSlideExtension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            robot.scoring.horizontalSlideExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    public class VerticalSlideAction implements Action {
        private final int verticalTargetPosition;

        public VerticalSlideAction(int position) {
            this.verticalTargetPosition = position;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            slidesToPosition(verticalTargetPosition);
            telemetryPacket.put("Slide Target Position", verticalTargetPosition);
            return false;
        }

        private void slidesToPosition(int position) {
            robot.scoring.verticalSlideExtension1.setTargetPosition(position);
            robot.scoring.verticalSlideExtension2.setTargetPosition(position);

            robot.scoring.verticalSlideExtension1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            robot.scoring.verticalSlideExtension2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            robot.scoring.verticalSlideExtension1.setPower(Power);
            robot.scoring.verticalSlideExtension2.setPower(Power);

            while (opModeIsActive() && (robot.scoring.verticalSlideExtension1.isBusy() || robot.scoring.verticalSlideExtension2.isBusy())) {
            }

            robot.scoring.verticalSlideExtension1.setPower(HOLD);
            robot.scoring.verticalSlideExtension2.setPower(HOLD);

            robot.scoring.verticalSlideExtension1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            robot.scoring.verticalSlideExtension2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}