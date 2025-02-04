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
import com.qualcomm.robotcore.hardware.DcMotor;

import RoadRunner.MecanumDrive;
import Robot.Robot;

@Config
@Autonomous(name = "5 Specimen", group = "Auton")
public class FiveSpecAuton extends LinearOpMode {

    private final Robot robot = new Robot();
    private MecanumDrive Drive;

    enum OuttakeState {BASE, COLLECTION, SCORING, REVERSESCORING}

    private static final int BASE = 0, LIFTED = 320, RAISED = 700;
    private static final int RETRACTED = -50, EXTENDED1 = 500, EXTENDED2 = 400, EXTENDED3 = 300;
    private static final double OPEN = 0.25, CLOSE = 0.75;
    private static final double UP = 0, DOWN = 0.82;
    private static final double STATIONARY = 0.1;

    private double SweepBuffer = 0.250;
    private double ClawBuffer = 0.150;
    private double VerticalSlideBuffer = 0.535;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Face Backwards. Middle of Center 2 Tiles. Up Against Border.");
        telemetry.update();

        Pose2d initialPosition = new Pose2d(0, -62, Math.toRadians(270));
        Drive = new MecanumDrive(hardwareMap, initialPosition); // Run At 100%

        robot.init(hardwareMap);

        Actions.runBlocking(new ParallelAction(
                // Robot Setup
                new IntakeAction(STATIONARY),
                new ClawAction(CLOSE),
                new OutTakeAction(OuttakeState.REVERSESCORING)
        ));

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(new SequentialAction(

                new ParallelAction(
                        new IntakeAction(STATIONARY),
                        new OutTakeAction(OuttakeState.REVERSESCORING),
                        new VerticalSlideAction(LIFTED),
                        Drive.actionBuilder(initialPosition)
                                .setReversed(true)
                                .strafeToConstantHeading(new Vector2d(0, -30))
                                .build()
                ),

                new VerticalSlideAction(RAISED),
                new SleepAction(VerticalSlideBuffer),
                new ClawAction(OPEN),

                new ParallelAction(
                        new OutTakeAction(OuttakeState.COLLECTION),
                        new VerticalSlideAction(BASE)
                ),

                // Move to Sample Collection Zone
                Drive.actionBuilder(new Pose2d(0, -30, Math.toRadians(270)))
                        .setReversed(false)
                        .setTangent(5)
                        .splineToLinearHeading(new Pose2d(32, -42, Math.toRadians(70)), .8)
                        .build(),


                // Sweep Samples
                new ParallelAction(
                        new SweeperAction(DOWN),
                        new HorizontalSlideAction(EXTENDED1)
                ),

                // Sample 1
                Drive.actionBuilder(new Pose2d(32, -42, Math.toRadians(72)))
                        .waitSeconds(SweepBuffer)
                        .strafeToLinearHeading(new Vector2d(32, -44), Math.toRadians(-65))
                        .build(),

                new SweeperAction(UP),

                Drive.actionBuilder(new Pose2d(32, -44, Math.toRadians(-65)))
                        .strafeToLinearHeading(new Vector2d(32, -32), Math.toRadians(70))
                        .build(),

                new ParallelAction(
                        new SweeperAction(DOWN),
                        new HorizontalSlideAction(EXTENDED2)
                ),

                // Sample 2
                Drive.actionBuilder(new Pose2d(32, -32, Math.toRadians(70)))
                        .waitSeconds(SweepBuffer)
                        .strafeToLinearHeading(new Vector2d(40, -44), Math.toRadians(-65))
                        .build(),

                new SweeperAction(UP),

                Drive.actionBuilder(new Pose2d(40, -44, Math.toRadians(-65)))
                        .strafeToLinearHeading(new Vector2d(44, -32), Math.toRadians(70))
                        .build(),

                new ParallelAction(
                        new SweeperAction(DOWN),
                        new HorizontalSlideAction(EXTENDED3)
                ),

                // Sample 3
                Drive.actionBuilder(new Pose2d(44, -32, Math.toRadians(70)))
                        .waitSeconds(SweepBuffer)
                        .strafeToLinearHeading(new Vector2d(48, -44), Math.toRadians(-65))
                        .build(),

                new SweeperAction(UP),
                new HorizontalSlideAction(RETRACTED),


                // Collect Specimen #2
                Drive.actionBuilder(new Pose2d(48, -44, Math.toRadians(-65)))
                        .strafeToLinearHeading(new Vector2d(40, -68), Math.toRadians(90))
                        .build(),

                // Grab Specimen
                new ClawAction(CLOSE),
                new SleepAction(ClawBuffer),

                // Score Specimen #2
                new ParallelAction(
                        new OutTakeAction(OuttakeState.SCORING),
                        new VerticalSlideAction(LIFTED),
                        Drive.actionBuilder(new Pose2d(40, -65, Math.toRadians(90)))
                                .strafeToConstantHeading(new Vector2d(-4, -32))
                                .build()
                ),

                new VerticalSlideAction(RAISED),
                new SleepAction(VerticalSlideBuffer),
                new ClawAction(OPEN),

                new ParallelAction(
                        new OutTakeAction(OuttakeState.COLLECTION),
                        new VerticalSlideAction(BASE)
                ),


                // Collect Specimen #3
                Drive.actionBuilder(new Pose2d(-4, -30, Math.toRadians(90)))
                        .strafeToConstantHeading(new Vector2d(40, -68))
                        .build(),

                // Grab Specimen
                new ClawAction(CLOSE),
                new SleepAction(ClawBuffer),

                // Score Specimen #3
                new ParallelAction(
                        new OutTakeAction(OuttakeState.SCORING),
                        new VerticalSlideAction(LIFTED),
                        Drive.actionBuilder(new Pose2d(40, -68, Math.toRadians(90)))
                                .strafeToConstantHeading(new Vector2d(-2, -32))
                                .build()
                ),

                new VerticalSlideAction(RAISED),
                new SleepAction(VerticalSlideBuffer),
                new ClawAction(OPEN),

                new ParallelAction(
                        new OutTakeAction(OuttakeState.COLLECTION),
                        new VerticalSlideAction(BASE)
                ),


                // Collect Specimen #4
                Drive.actionBuilder(new Pose2d(-2, -30, Math.toRadians(90)))
                        .strafeToConstantHeading(new Vector2d(40, -68))
                        .build(),

                // Grab Specimen
                new ClawAction(CLOSE),
                new SleepAction(ClawBuffer),

                // Score Specimen #4
                new ParallelAction(
                        new OutTakeAction(OuttakeState.SCORING),
                        new VerticalSlideAction(LIFTED),
                        Drive.actionBuilder(new Pose2d(40, -68, Math.toRadians(90)))
                                .strafeToConstantHeading(new Vector2d(2, -32))
                                .build()
                ),

                new VerticalSlideAction(RAISED),
                new SleepAction(VerticalSlideBuffer),
                new ClawAction(OPEN),

                new ParallelAction(
                        new OutTakeAction(OuttakeState.COLLECTION),
                        new VerticalSlideAction(BASE)
                ),


                // Collect Specimen #5
                Drive.actionBuilder(new Pose2d(2, -30, Math.toRadians(90)))
                        .strafeToConstantHeading(new Vector2d(40, -68))
                        .build(),

                // Grab Specimen
                new ClawAction(CLOSE),
                new SleepAction(ClawBuffer),

                // Score Specimen #5
                new ParallelAction(
                        new OutTakeAction(OuttakeState.SCORING),
                        new VerticalSlideAction(LIFTED),
                        Drive.actionBuilder(new Pose2d(40, -68, Math.toRadians(90)))
                                .strafeToConstantHeading(new Vector2d(4, -32))
                                .build()
                ),

                new VerticalSlideAction(RAISED),
                new SleepAction(VerticalSlideBuffer),
                new ClawAction(OPEN),

                new ParallelAction(
                        new OutTakeAction(OuttakeState.COLLECTION),
                        new VerticalSlideAction(BASE)
                ),

                new SleepAction(30)
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
                    robot.scoring.outtakeArmRotation.setPosition(0.00);
                    robot.scoring.clawPrimaryPivot.setPosition(0.05);
                    break;

                case COLLECTION:
                    robot.scoring.outtakeArmRotation.setPosition(0.06);
                    robot.scoring.clawPrimaryPivot.setPosition(0.45);

                    break;

                case SCORING:
                    robot.scoring.outtakeArmRotation.setPosition(0.75);
                    robot.scoring.clawPrimaryPivot.setPosition(0.88);

                    break;

                case REVERSESCORING: // Default
                    robot.scoring.outtakeArmRotation.setPosition(0.30);
                    robot.scoring.clawPrimaryPivot.setPosition(0.45);

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

    public class SweeperAction implements Action {
        private final double targetState;

        public SweeperAction(double STATE) {
            this.targetState = STATE;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setSweeperState(targetState);
            telemetryPacket.put("Sweeper State", targetState);
            return false;
        }

        private void setSweeperState(double STATE) {
            robot.scoring.sweeper.setPosition(STATE);
        }
    }

    public class HorizontalSlideAction implements Action {
        private final int targetPosition;
        private boolean isStarted = false;

        public HorizontalSlideAction(int position) {
            this.targetPosition = position;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            if (!isStarted) {
                isStarted = true;

                robot.scoring.horizontalSlideExtension.setTargetPosition(targetPosition);

                robot.scoring.horizontalSlideExtension.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                robot.scoring.horizontalSlideExtension.setPower(1);
            }

            boolean slidesAreBusy = robot.scoring.horizontalSlideExtension.isBusy();

            telemetryPacket.put("Horizontal Slide Target Position", targetPosition);
            telemetryPacket.put("Horizontal Slide Busy", slidesAreBusy);

            if (!slidesAreBusy) {
                robot.scoring.horizontalSlideExtension.setPower(0);
                robot.scoring.horizontalSlideExtension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

                robot.scoring.horizontalSlideExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                return true;
            }

            return false;
        }
    }

    public class VerticalSlideAction implements Action {
        private final int targetPosition;
        private boolean isStarted = false;

        public VerticalSlideAction(int position) {
            this.targetPosition = position;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            if (!isStarted) {
                isStarted = true;

                robot.scoring.verticalSlideExtension1.setTargetPosition(targetPosition);
                robot.scoring.verticalSlideExtension2.setTargetPosition(targetPosition);

                robot.scoring.verticalSlideExtension1.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                robot.scoring.verticalSlideExtension2.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                robot.scoring.verticalSlideExtension1.setPower(1);
                robot.scoring.verticalSlideExtension2.setPower(1);
            }

            boolean slidesAreBusy = robot.scoring.verticalSlideExtension1.isBusy() || robot.scoring.verticalSlideExtension2.isBusy();

            telemetryPacket.put("Vertical Slide Target Position", targetPosition);
            telemetryPacket.put("Vertical Slide Busy", slidesAreBusy);

            if (!slidesAreBusy) {
                robot.scoring.verticalSlideExtension1.setPower(0.000375);
                robot.scoring.verticalSlideExtension2.setPower(0.000375);

                robot.scoring.verticalSlideExtension1.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                robot.scoring.verticalSlideExtension2.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
                return true;
            }

            return false;
        }
    }

    public class IntakeAction implements Action {
        private final double targetState;

        public IntakeAction(double STATE) {
            this.targetState = STATE;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            setIntakeState(targetState);
            telemetryPacket.put("Intake State", targetState);
            return false;
        }

        private void setIntakeState(double STATE) {
            robot.scoring.intakePivot.setPosition(STATE);
        }
    }
}