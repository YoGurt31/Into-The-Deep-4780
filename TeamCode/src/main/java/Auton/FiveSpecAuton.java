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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import RoadRunner.MecanumDrive;
import Robot.Robot;

@Disabled
@Config
@Autonomous(name = "5 Specimen", group = "Auton")
public class FiveSpecAuton extends LinearOpMode {

    private final Robot robot = new Robot();
    private MecanumDrive Drive;

    enum OuttakeState {BASE, COLLECTION, REVERSESCORING}

    private static final int RETRACTED = 0, EXTENDED = 750;
    private static final double OPEN = 0.25, CLOSE = 0.75;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Face Backwards. Middle of Center 2 Tiles. Up Against Border.");
        telemetry.update();

        Pose2d initialPosition = new Pose2d(0, -62, Math.toRadians(270));
        Drive = new MecanumDrive(hardwareMap, initialPosition); // Run At 80%
        MecanumDrive.PARAMS.maxWheelVel = 80;
        MecanumDrive.PARAMS.maxProfileAccel = 80;

        robot.init(hardwareMap);

        Actions.runBlocking(new ParallelAction(
                // Robot Setup
                new ClawAction(CLOSE),
                new OutTakeAction(OuttakeState.REVERSESCORING)
        ));

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(new SequentialAction(

                // Drive To Bar And Score Specimen #1
                Drive.actionBuilder(initialPosition)
                        .strafeTo(new Vector2d(-6, -30))
                        .build(),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                ),


                // Move to Sample Collection Zone
                Drive.actionBuilder(new Pose2d(-6, -30, Math.toRadians(270)))
                        .setTangent(5)
                        .splineToLinearHeading(new Pose2d(36, -36, Math.toRadians(55)), .8)
                        .build(),


                // Sweep Samples
                new HorizontalSlideAction(EXTENDED),
                Drive.actionBuilder(new Pose2d(36, -36, Math.toRadians(55)))
                        .strafeToLinearHeading(new Vector2d(36, -48), Math.toRadians(-45))
                        .build(),

                Drive.actionBuilder(new Pose2d(36, -48, Math.toRadians(45)))
                        .strafeToLinearHeading(new Vector2d(42, -40), Math.toRadians(45))
                        .waitSeconds(0.2)
                        .strafeToLinearHeading(new Vector2d(42, -48), Math.toRadians(-45))
                        .build(),

                Drive.actionBuilder(new Pose2d(42, -48, Math.toRadians(45)))
                        .strafeToLinearHeading(new Vector2d(48, -40), Math.toRadians(45))
                        .waitSeconds(0.2)
                        .strafeToLinearHeading(new Vector2d(48, -48), Math.toRadians(-45))
                        .build(),

                new HorizontalSlideAction(RETRACTED),


                // Collect Specimen #2
                Drive.actionBuilder(new Pose2d(48, -48, Math.toRadians(90)))
                        .setTangent(3)
                        .splineToLinearHeading(new Pose2d(40, -58, Math.toRadians(90)), 4.5)
                        .strafeTo(new Vector2d(40, -62))
                        .build(),

                // Grab Specimen
                new ClawAction(CLOSE),

                // Score Specimen #2
                new ParallelAction(
                        new OutTakeAction(OuttakeState.REVERSESCORING),
                        Drive.actionBuilder(new Pose2d(40, -62, Math.toRadians(90)))
                                .strafeTo(new Vector2d(40, -58))
                                .strafeToLinearHeading(new Vector2d(-3, -36), Math.toRadians(270))
                                .strafeTo(new Vector2d(-3, -30))
                                .build()
                ),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                ),


                // Collect Specimen #3
                Drive.actionBuilder(new Pose2d(-3, -30, Math.toRadians(270)))
                        .strafeTo(new Vector2d(-3, -36))
                        .strafeToLinearHeading(new Vector2d(40, -58), Math.toRadians(90))
                        .strafeTo(new Vector2d(40, -62))
                        .build(),

                // Grab Specimen
                new ClawAction(CLOSE),

                // Score Specimen #3
                new ParallelAction(
                        new OutTakeAction(OuttakeState.REVERSESCORING),
                        Drive.actionBuilder(new Pose2d(40, -62, Math.toRadians(90)))
                                .strafeTo(new Vector2d(40, -58))
                                .strafeToLinearHeading(new Vector2d(0, -36), Math.toRadians(270))
                                .strafeTo(new Vector2d(0, -30))
                                .build()
                ),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                ),


                // Collect Specimen #4
                Drive.actionBuilder(new Pose2d(0, -30, Math.toRadians(270)))
                        .strafeTo(new Vector2d(0, -36))
                        .strafeToLinearHeading(new Vector2d(40, -58), Math.toRadians(90))
                        .strafeTo(new Vector2d(40, -62))
                        .build(),

                // Grab Specimen
                new ClawAction(CLOSE),

                // Score Specimen #4
                new ParallelAction(
                        new OutTakeAction(OuttakeState.REVERSESCORING),
                        Drive.actionBuilder(new Pose2d(40, -62, Math.toRadians(90)))
                                .strafeTo(new Vector2d(40, -58))
                                .strafeToLinearHeading(new Vector2d(3, -36), Math.toRadians(270))
                                .strafeTo(new Vector2d(3, -30))
                                .build()
                ),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                ),


                // Collect Specimen #5
                Drive.actionBuilder(new Pose2d(3, -30, Math.toRadians(270)))
                        .strafeTo(new Vector2d(3, -36))
                        .strafeToLinearHeading(new Vector2d(40, -58), Math.toRadians(90))
                        .strafeTo(new Vector2d(40, -62))
                        .build(),

                // Grab Specimen
                new ClawAction(CLOSE),

                // Score Specimen #5
                new ParallelAction(
                        new OutTakeAction(OuttakeState.REVERSESCORING),
                        Drive.actionBuilder(new Pose2d(40, -62, Math.toRadians(90)))
                                .strafeTo(new Vector2d(40, -58))
                                .strafeToLinearHeading(new Vector2d(6, -36), Math.toRadians(270))
                                .strafeTo(new Vector2d(6, -30))
                                .build()
                ),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                )

        ));
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

                case REVERSESCORING: // Default
                    robot.scoring.outtakeArmRotation.setPosition(0.50);
                    robot.scoring.clawPrimaryPivot.setPosition(0.05);
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

            robot.scoring.horizontalSlideExtension.setPower(1);

            while (opModeIsActive() && (robot.scoring.horizontalSlideExtension.isBusy())) {
            }

            robot.scoring.horizontalSlideExtension.setPower(0);
            robot.scoring.horizontalSlideExtension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

            robot.scoring.horizontalSlideExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
}