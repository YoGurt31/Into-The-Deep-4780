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
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import RoadRunner.MecanumDrive;
import Robot.Robot;

@Config
@Autonomous(name = "Sample", group = "Auton")
public class FourSampleAuton extends LinearOpMode {

    private final Robot robot = new Robot();
    private MecanumDrive Drive;

    enum OuttakeState {BASE, COLLECTION, SCORING}

    enum IntakeState {ACTIVE, INACTIVE}

    private static final int BASE = 0, RAISED = 1500;
    private static final int RETRACTED = 0, EXTENDED = 900;
    private static final double OPEN = 0.25, CLOSE = 0.75;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Face Forward. Left Line of Tile 2 from Bucket. Up Against Border.");
        telemetry.update();

        Pose2d initialPosition = new Pose2d(-39, -62, Math.toRadians(90));
        MecanumDrive Drive = new MecanumDrive(hardwareMap, initialPosition); // Run At 80%

        robot.init(hardwareMap);

        Actions.runBlocking(new ParallelAction(
                        // Robot Setup
                        new ClawAction(CLOSE),
                        new OutTakeAction(OuttakeState.SCORING)
                )
        );

        waitForStart();

        if (isStopRequested()) return;

        Actions.runBlocking(new SequentialAction(

                Drive.actionBuilder(initialPosition)
                        .strafeToLinearHeading(new Vector2d(-53, -53), Math.toRadians(-135))
                        .build(),

                new ParallelAction(
                        new VerticalSlideAction(RAISED),
                        Drive.actionBuilder(new Pose2d(-53, -53, Math.toRadians(-135)))
                                .strafeTo(new Vector2d(-60, -60))
                                .build()
                ),

                new ClawAction(OPEN),

                new ParallelAction(
                        new OutTakeAction(OuttakeState.BASE),
                        new VerticalSlideAction(BASE)
                ),

//                CollectAndScore(-48, Math.toRadians(0)), // Sample 1
//                CollectAndScore(-60, Math.toRadians(0)), // Sample 2
//                CollectAndScore(-60, Math.toRadians(110)), // Sample 3
//
//                // **Park**
//                Drive.actionBuilder(new Pose2d(-60, -60, Math.toRadians(45)))
//                        .strafeToLinearHeading(new Vector2d(-24, 0), Math.toRadians(0))
//                        .build()


                new SleepAction(30)
        ));
    }

    private Action CollectAndScore(double sampleX, double sampleHeading) {
        return new SequentialAction(

                new ParallelAction(
                        Drive.actionBuilder(new Pose2d(-60, -60, Math.toRadians(-135)))
                                .turnTo(sampleHeading)
                                .strafeTo(new Vector2d(sampleX, -50))
                                .build()
                ),

                new ParallelAction(
                        new HorizontalSlideAction(EXTENDED),
                        new IntakeAction(IntakeState.ACTIVE)
                ),

                new WaitForSampleIntake(),

                new ParallelAction(
                        new HorizontalSlideAction(RETRACTED),
                        new ClawAction(CLOSE)
                ),

                new SequentialAction(
                        Drive.actionBuilder(new Pose2d(sampleX, -50, sampleHeading))
                                .strafeTo(new Vector2d(-60, -60))
                                .build(),

                        new ParallelAction(
                                new VerticalSlideAction(RAISED),
                                new OutTakeAction(OuttakeState.COLLECTION)
                        ),

                        new ClawAction(OPEN),

                        new ParallelAction(
                                new OutTakeAction(OuttakeState.BASE),
                                new VerticalSlideAction(BASE)
                        )
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

    public class WaitForSampleIntake implements Action {
        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            int R = robot.scoring.colorSensor.red();
            int G = robot.scoring.colorSensor.green();
            int B = robot.scoring.colorSensor.blue();
            boolean sampleDetected = (R > 2500 && G > 3000 && B < 2500) || (R > 1500 && G < 3000 && B < 2000) || (R < 2000 && G < 3000 && B > 1500);
            telemetryPacket.put("Sample Detected", sampleDetected);
            return sampleDetected;
        }
    }

    public class IntakeAction implements Action {
        private final IntakeState targetState;

        public IntakeAction(IntakeState state) {
            this.targetState = state;
        }

        @Override
        public boolean run(@NonNull TelemetryPacket telemetryPacket) {
            robot.scoring.rollerInOut.setPower(targetState == IntakeState.ACTIVE ? 1 : 0);
            return false;
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
}