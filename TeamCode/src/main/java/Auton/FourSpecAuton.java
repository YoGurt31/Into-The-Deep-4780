// Coded By Gurtej Singh

package Auton;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.acmerobotics.roadrunner.ParallelAction;
import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.SequentialAction;
import com.acmerobotics.roadrunner.SleepAction;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.ftc.Actions;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import RoadRunner.MecanumDrive;
import Robot.Robot;

@Config
@Autonomous(name = "4 Specimen (Optimized)", group = "Auton")
public class FourSpecAuton extends LinearOpMode {

    private final Robot robot = new Robot();
    private MecanumDrive Drive;

    enum OuttakeState {BASE, COLLECTION, REVERSESCORING}

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

                new SleepAction(.2),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                ),

                new SleepAction(.2),


                // Move to Sample Collection Zone
                Drive.actionBuilder(new Pose2d(-6, -30, Math.toRadians(90)))
                        .setTangent(5)
                        .splineToLinearHeading(new Pose2d(24, -40, Math.toRadians(90)), .25)
                        .build(),

                // Collect Sample #1
                Drive.actionBuilder(new Pose2d(24, -40, Math.toRadians(90)))
                        .splineToConstantHeading(new Vector2d(48, -9), .5)
                        .strafeTo(new Vector2d(48, -50))
                        .build(),

                // Collect Sample #2
                Drive.actionBuilder(new Pose2d(48, -50, Math.toRadians(90)))
                        .setTangent(1.5)
                        .splineToConstantHeading(new Vector2d(56, -9), .5)
                        .strafeTo(new Vector2d(56, -50))
                        .build(),


                // Collect Specimen #2
                Drive.actionBuilder(new Pose2d(56, -50, Math.toRadians(90)))
                        .setTangent(3)
                        .splineToConstantHeading(new Vector2d(40, -62), 4.5)
                        .build(),

                new SleepAction(.2),

                // Grab Specimen
                new ClawAction(CLOSE),

                new SleepAction(.2),

                // Score Specimen #2
                new ParallelAction(
                        new OutTakeAction(OuttakeState.REVERSESCORING),
                        Drive.actionBuilder(new Pose2d(40, -62, Math.toRadians(90)))
                                .splineToLinearHeading(new Pose2d(-2, -36, Math.toRadians(270)), 1.5)
                                .strafeTo(new Vector2d(-2, -30))
                                .build()
                ),

                new SleepAction(.2),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                ),

                new SleepAction(.2),


                // Collect Specimen #3
                Drive.actionBuilder(new Pose2d(-2, -30, Math.toRadians(270)))
                        .strafeTo(new Vector2d(-2, -36))
                        .strafeToLinearHeading(new Vector2d(40, -58), Math.toRadians(90))
                        .strafeTo(new Vector2d(40, -62))
                        .build(),

                new SleepAction(.2),

                // Grab Specimen
                new ClawAction(CLOSE),

                new SleepAction(.2),

                // Score Specimen #3
                new ParallelAction(
                        new OutTakeAction(OuttakeState.REVERSESCORING),
                        Drive.actionBuilder(new Pose2d(40, -62, Math.toRadians(90)))
                                .splineToLinearHeading(new Pose2d(2, -36, Math.toRadians(270)), 1.5)
                                .strafeTo(new Vector2d(2, -30))
                                .build()
                ),

                new SleepAction(.2),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                ),

                new SleepAction(.2),


                // Collect Specimen #4
                Drive.actionBuilder(new Pose2d(2, -30, Math.toRadians(270)))
                        .strafeTo(new Vector2d(2, -36))
                        .strafeToLinearHeading(new Vector2d(40, -58), Math.toRadians(90))
                        .strafeTo(new Vector2d(40, -62))
                        .build(),

                new SleepAction(.2),

                // Grab Specimen
                new ClawAction(CLOSE),

                new SleepAction(.2),

                // Score Specimen #4
                new ParallelAction(
                        new OutTakeAction(OuttakeState.REVERSESCORING),
                        Drive.actionBuilder(new Pose2d(40, -62, Math.toRadians(90)))
                                .splineToLinearHeading(new Pose2d(6, -36, Math.toRadians(270)), 1.5)
                                .strafeTo(new Vector2d(6, -30))
                                .build()
                ),

                new SleepAction(.2),

                new ParallelAction(
                        new ClawAction(OPEN),
                        new OutTakeAction(OuttakeState.COLLECTION)
                ),

                new SleepAction(.2),

                // Park
                Drive.actionBuilder(new Pose2d(6, -30, Math.toRadians(270)))
                        .splineToLinearHeading(new Pose2d(60, -60, Math.toRadians(90)), .1)
                        .build()

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
                    robot.scoring.outtakeArmRotation.setPosition(0.5);
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
}