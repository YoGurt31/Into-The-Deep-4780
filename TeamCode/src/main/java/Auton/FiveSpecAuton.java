// Coded By Gurtej Singh

package Auton;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import RoadRunner.MecanumDrive;
import Robot.Robot;

@Config
@Autonomous(name = "100PtAuton", group = "Auton")
public class FiveSpecAuton extends LinearOpMode {

    private final Robot robot = new Robot();
    private MecanumDrive Drive;

    private final double Power = 1.0;
    private final double HOLD = 0.0005;

    enum OuttakeState {
        BASE,
        COLLECTION,
        SCORING
    }

    private final int BASE = 0;
    private final int RISE = 350;
    private final int RAISED = 750;

    private final int RETRACTED = 0;
    private final int EXTENDED1 = 500;
    private final int EXTENDED2 = 700;
    private final int EXTENDED3 = 900;

    private final double OPEN = 0.25;
    private final double CLOSE = 0.75;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Initializing...");
        telemetry.update();

        Pose2d initialPosition = new Pose2d(0, -62, Math.toRadians(90));
        Drive = new MecanumDrive(hardwareMap, initialPosition); // Run At 80%
        MecanumDrive.PARAMS.maxWheelVel = 80;
        MecanumDrive.PARAMS.maxProfileAccel = 80;

        robot.init(hardwareMap);

        Actions.runBlocking(new ParallelAction(
                // Robot Setup
                new ClawAction(CLOSE),
                new OutTakeAction(OuttakeState.SCORING),
                new VerticalSlideAction(RAISED)
        ));

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(new SequentialAction(

                // Drive To Bar And Score Specimen #1
                new ParallelAction(
                        Drive.actionBuilder(initialPosition)
                                .setReversed(false)
                                .strafeTo(new Vector2d(-6, -30))
                                .build(),
                        new VerticalSlideAction(RISE)
                ),

                new VerticalSlideAction(RAISED),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION),
                        new VerticalSlideAction(BASE)
                ),


                // Move to Sample Collection Zone
                Drive.actionBuilder(new Pose2d(-6, -30, Math.toRadians(90)))
                        .setReversed(true)
                        .setTangent(5)
                        .splineToLinearHeading(new Pose2d(30, -40, Math.toRadians(55)), .8)
                        .build(),


                // Sweep Samples
                new SequentialAction(
                        new HorizontalSlideAction(EXTENDED1),
                        Drive.actionBuilder(new Pose2d(30, -40, Math.toRadians(55)))
                                .turnTo(Math.toRadians(-35))
                                .turnTo(Math.toRadians(45))
                                .build(),

                        new HorizontalSlideAction(EXTENDED2),
                        Drive.actionBuilder(new Pose2d(30, -40, Math.toRadians(45)))
                                .turnTo(Math.toRadians(-35))
                                .turnTo(Math.toRadians(35))
                                .build(),

                        new HorizontalSlideAction(EXTENDED3),
                        Drive.actionBuilder(new Pose2d(30, -40, Math.toRadians(35)))
                                .turnTo(Math.toRadians(-35))
                                .turnTo(Math.toRadians(90))
                                .build(),

                        new HorizontalSlideAction(RETRACTED)
                ),


                // Collect Specimen #2
                new ParallelAction(
                        Drive.actionBuilder(new Pose2d(30, -40, Math.toRadians(90)))
                                .setReversed(true)
                                .strafeTo(new Vector2d(40,-62))
                                .build()
                ),

                // Grab Specimen
                new ClawAction(CLOSE),

                // Score Specimen #2
                new ParallelAction(
                        new VerticalSlideAction(RISE),
                        new OutTakeAction(OuttakeState.SCORING),
                        Drive.actionBuilder(new Pose2d(48, -62, Math.toRadians(90)))
                                .setReversed(false)
                                .strafeTo(new Vector2d(-3, -30))
                                .build()
                ),

                new VerticalSlideAction(RAISED),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION),
                        new VerticalSlideAction(BASE)
                ),

                CollectAndScore(0), // Specimen #3
                CollectAndScore(3), // Specimen #4
                CollectAndScore(6)  // Specimen #5

        ));
    }

    private Action CollectAndScore(double barX) {
        return new SequentialAction(

                // Move to Collection Zone
                new ParallelAction(
                        Drive.actionBuilder(new Pose2d(barX, -30, Math.toRadians(90)))
                                .setReversed(true)
                                .setTangent(30)
                                .splineToConstantHeading(new Vector2d(24, -48), 0)
                                .splineToConstantHeading(new Vector2d(40, -62), 30)
                                .build()
                ),

                // Grab Specimen
                new ClawAction(CLOSE),

                // Raise Slides and Drive to Bar
                new ParallelAction(
                        new VerticalSlideAction(RISE),
                        new OutTakeAction(OuttakeState.SCORING),
                        Drive.actionBuilder(new Pose2d(40, -62, Math.toRadians(90)))
                                .setReversed(false)
                                .strafeTo(new Vector2d(barX, -30))
                                .build()
                ),

                // Score Specimen
                new VerticalSlideAction(RAISED),

                // Reset
                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION),
                        new VerticalSlideAction(BASE)
                )
        );
    }

    public class OutTakeAction implements Action {
        private final OuttakeState targetState;

        public OutTakeAction(OuttakeState state) {
            this.targetState = state;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setOuttakeState(targetState);
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
        private final double targetState;

        public ClawAction(double STATE) {
            this.targetState = STATE;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setClawState(targetState);
            telemetryPacket.put("Claw State", targetState);
            return false;
        }

        private void setClawState(double STATE) {
            robot.scoring.clawStatus.setPosition(STATE);
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
        private final int targetPosition;

        public VerticalSlideAction(int position) {
            this.targetPosition = position;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            slidesToPosition(targetPosition);
            telemetryPacket.put("Slide Target Position", targetPosition);
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