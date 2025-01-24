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
@Autonomous(name = "100PtAuton", group = "Auton")
public class FiveSpecAuton extends LinearOpMode {

    private final Robot robot = new Robot();

    private final double Power = 1.0;
    private final double HOLD = 0.0005;

    OuttakeState outtakeState = OuttakeState.BASE;

    enum OuttakeState {
        BASE,
        COLLECTION,
        SCORING
    }

    ClawState clawState = ClawState.CLOSE;

    enum ClawState {
        OPEN,
        CLOSE
    }

    private final int BASE = 0;
    private final int RISE = 350;
    private final int RAISED = 750;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Initializing...");
        telemetry.update();

        Pose2d initialPosition = new Pose2d(0, -62, Math.toRadians(90));
        MecanumDrive Drive = new MecanumDrive(hardwareMap, initialPosition);

        waitForStart();

        Actions.runBlocking(Drive.actionBuilder(initialPosition)
//                // Robot Setup
//                .stopAndAdd(new ClawAction(ClawState.CLOSE))
//                .stopAndAdd(new OutTakeAction(OuttakeState.SCORING))
//                .stopAndAdd(new VerticalSlideAction(RISE))
//
//                // Drive To Bar And Score Specimen #1
//                .strafeToConstantHeading(new Vector2d(-5, -32))
//                .stopAndAdd(new VerticalSlideAction(RAISED))
//                .stopAndAdd(new ClawAction(ClawState.OPEN))
//                .stopAndAdd(new OutTakeAction(OuttakeState.COLLECTION))
//                .stopAndAdd(new VerticalSlideAction(BASE))
//
//                // Drive To Collect Samples
//                .strafeToConstantHeading(new Vector2d(36, -48))
//
//                .splineToConstantHeading(new Vector2d(44, -12), 0) // In Front of Sample 1
//                .waitSeconds(0.025)
//                .lineToY(-50) // Sample 1 Retrieved
//
//                .splineToConstantHeading(new Vector2d(56, -12), 0) // In Front of Sample 2
//                .waitSeconds(0.025)
//                .lineToY(-50) // Sample 2 Retrieved
//
//                .splineToConstantHeading(new Vector2d(62, -12), 0) // In Front of Sample 3
//                .waitSeconds(0.025)
//                .lineToY(-50) // Sample 3 Retrieved
//
//                // Collect Specimen #2
//                .lineToY(-62)
//                .stopAndAdd(new ClawAction(ClawState.CLOSE))
//                .stopAndAdd(new VerticalSlideAction(RISE))
//                .stopAndAdd(new OutTakeAction(OuttakeState.SCORING))
//
//                // Score Specimen #2
//                .strafeToConstantHeading(new Vector2d(-2.5, -32))
//                .stopAndAdd(new VerticalSlideAction(RAISED))
//                .stopAndAdd(new ClawAction(ClawState.OPEN))
//                .stopAndAdd(new OutTakeAction(OuttakeState.COLLECTION))
//                .stopAndAdd(new VerticalSlideAction(BASE))
//
//                // Collect Specimen #3
//                .strafeToConstantHeading(new Vector2d(40, -58))
//                .waitSeconds(0.025)
//                .lineToY(-62)
//                .stopAndAdd(new ClawAction(ClawState.CLOSE))
//                .stopAndAdd(new VerticalSlideAction(RISE))
//                .stopAndAdd(new OutTakeAction(OuttakeState.SCORING))
//
//                // Score Specimen #3
//                .strafeToConstantHeading(new Vector2d(0, -32))
//                .stopAndAdd(new VerticalSlideAction(RAISED))
//                .stopAndAdd(new ClawAction(ClawState.OPEN))
//                .stopAndAdd(new OutTakeAction(OuttakeState.COLLECTION))
//                .stopAndAdd(new VerticalSlideAction(BASE))
//
//                // Collect Specimen #4
//                .strafeToConstantHeading(new Vector2d(40, -58))
//                .waitSeconds(0.025)
//                .lineToY(-62)
//                .stopAndAdd(new ClawAction(ClawState.CLOSE))
//                .stopAndAdd(new VerticalSlideAction(RISE))
//                .stopAndAdd(new OutTakeAction(OuttakeState.SCORING))
//
//                // Score Specimen #4
//                .strafeToConstantHeading(new Vector2d(2.5, -32))
//                .stopAndAdd(new VerticalSlideAction(RAISED))
//                .stopAndAdd(new ClawAction(ClawState.OPEN))
//                .stopAndAdd(new OutTakeAction(OuttakeState.COLLECTION))
//                .stopAndAdd(new VerticalSlideAction(BASE))
//
//                // Collect Specimen #5
//                .strafeToConstantHeading(new Vector2d(40, -58))
//                .waitSeconds(0.025)
//                .lineToY(-62)
//                .stopAndAdd(new ClawAction(ClawState.CLOSE))
//                .stopAndAdd(new VerticalSlideAction(RISE))
//                .stopAndAdd(new OutTakeAction(OuttakeState.SCORING))
//
//                // Score Specimen #5
//                .strafeToConstantHeading(new Vector2d(5, -32))
//                .stopAndAdd(new VerticalSlideAction(RAISED))
//                .stopAndAdd(new ClawAction(ClawState.OPEN))
//                .stopAndAdd(new OutTakeAction(OuttakeState.COLLECTION))
//                .stopAndAdd(new VerticalSlideAction(BASE))
//
//                // Park
//                .strafeToConstantHeading(new Vector2d(62, -60))


                // Drive To Bar And Score Specimen #1
                .strafeTo(new Vector2d(-6, -30))
                .waitSeconds(.8)

                // Drive To Collect Samples
                .setTangent(5)
                .splineToConstantHeading(new Vector2d(24, -48), 0)
                .splineToConstantHeading(new Vector2d(38, -24), 1.5)
                .setTangent(1.5)
                .splineToConstantHeading(new Vector2d(48, -9), 0) // In Front of Sample 1
                .strafeTo(new Vector2d(48, -56)) // Retrieved Sample 1

                .setTangent(1.5)
                .splineToConstantHeading(new Vector2d(56, -9), 0) // In Front of Sample 2
                .strafeTo(new Vector2d(56, -56)) // Retrieved Sample 2

                .setTangent(1.5)
                .splineToConstantHeading(new Vector2d(66, -9), 0) // In Front of Sample 3
                .strafeTo(new Vector2d(66, -56)) // Retrieved Sample 3

                // Collect Specimen #2
                .strafeTo(new Vector2d(62,-62))
                .waitSeconds(.5)

                // Score Specimen #2
                .strafeTo(new Vector2d(-3, -30))
                .waitSeconds(.8)

                // Collect Specimen #3
                .setTangent(30)
                .splineToConstantHeading(new Vector2d(24, -48), 0)
                .splineToConstantHeading(new Vector2d(40, -62), 30)
                .waitSeconds(.5)

                // Score Specimen #3
                .strafeTo(new Vector2d(0, -30))
                .waitSeconds(.8)

                // Collect Specimen #4
                .setTangent(30)
                .splineToConstantHeading(new Vector2d(24, -48), 0)
                .splineToConstantHeading(new Vector2d(40, -62), 30)
                .waitSeconds(.5)

                // Score Specimen #4
                .strafeTo(new Vector2d(3, -30))
                .waitSeconds(.8)

                // Collect Specimen #5
                .setTangent(30)
                .splineToConstantHeading(new Vector2d(24, -48), 0)
                .splineToConstantHeading(new Vector2d(40, -62), 30)
                .waitSeconds(.5)

                // Score Specimen #5
                .strafeTo(new Vector2d(6, -30))
                .waitSeconds(.8)

//                // Park
//                .strafeTo(new Vector2d(62, -60))

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