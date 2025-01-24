// Coded By Gurtej Singh

package Auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.teamcode.R;

import Robot.Robot;

@Autonomous(name = "19PtBucketAuton", group = "Auton")
public class OneSampleAuton extends LinearOpMode {

    private final Robot robot = new Robot();

    OuttakeState outtakeState = OuttakeState.SCORING;
    enum OuttakeState {
        BASE,
        COLLECTION,
        SCORING
    }

    private final int BASE = 0;
    private final int RAISED = 750;

    private final int RETRACTED = 0;
    private final int EXTENDED = 1000;

    private final double Power = 1.0;
    private final double HOLD = 0.0005;

    ClawState clawState = ClawState.CLOSE;
    enum ClawState {
        OPEN,
        CLOSE
    }

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addLine("Initializing...");
        telemetry.update();

        robot.init(hardwareMap);

        robot.driveTrain.resetDriveTrainEncoders();
        robot.scoring.resetScoringEncoders();

        telemetry.addLine("\nWaiting for start...");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {
            // Start Auton
            robot.driveTrain.runDriveTrainEncoders();
            robot.scoring.runScoringEncoders();

            while (opModeIsActive()) {

                // Set Robot (Should Have PreLoaded Sample)
                setClawState(ClawState.CLOSE);
                setOuttakeState(OuttakeState.SCORING);
                verticalSlidesToPosition(BASE);

                // RUN 1
                // Drive To Bucket
                encoderDrive(30, 0.5);
                encoderStrafe(-12,0.5);

                // Buffer
                sleep(250);

                // Rotate Robot 45º CW

                // Stop Driving, Raise Slides
                verticalSlidesToPosition(RAISED);

                // Reset To Collection Position
                setOuttakeState(OuttakeState.COLLECTION);

                // Open Claw
                setClawState(ClawState.OPEN);

                // Reset To Scoring Position
                setOuttakeState(OuttakeState.SCORING);
                verticalSlidesToPosition(BASE);

                // Rotate Robot To 0º CW

                // Extendo Intake Sequence
                horizontalSlidesToPosition(EXTENDED);
                runIntakeSequeunce();

                // Extendo Transfer Sequence
                horizontalSlidesToPosition(RETRACTED);
                setOuttakeState(OuttakeState.BASE);
                setClawState(ClawState.CLOSE);

                // Buffer
                sleep(250);

                // Rotate Robot 45º CW

                // Stop Driving, Raise Slides
                verticalSlidesToPosition(RAISED);

                // Reset To Collection Position
                setOuttakeState(OuttakeState.COLLECTION);

                // Open Claw
                setClawState(ClawState.OPEN);

                // Reset To Scoring Position
                setOuttakeState(OuttakeState.SCORING);
                verticalSlidesToPosition(BASE);

                // Rotate Robot To 0º CW

                // Extendo Intake Sequence
                horizontalSlidesToPosition(EXTENDED);
                runIntakeSequeunce();

                // Extendo Transfer Sequence
                horizontalSlidesToPosition(RETRACTED);
                setOuttakeState(OuttakeState.BASE);
                setClawState(ClawState.CLOSE);

                // Buffer
                sleep(250);

                // Rotate Robot 45º CW

                // Stop Driving, Raise Slides
                verticalSlidesToPosition(RAISED);

                // Reset To Collection Position
                setOuttakeState(OuttakeState.COLLECTION);

                // Open Claw
                setClawState(ClawState.OPEN);

                // Reset To Scoring Position
                setOuttakeState(OuttakeState.SCORING);
                verticalSlidesToPosition(BASE);

                // Rotate Robot To 0º CW

                // Extendo Intake Sequence
                horizontalSlidesToPosition(EXTENDED);
                runIntakeSequeunce();

                // Extendo Transfer Sequence
                horizontalSlidesToPosition(RETRACTED);
                setOuttakeState(OuttakeState.BASE);
                setClawState(ClawState.CLOSE);

                // Drive To Park

                robot.driveTrain.brake();

                telemetry.addLine("Autonomous Complete!");
                telemetry.update();

                sleep(30000);
            }
        }

        telemetry.addLine("Autonomous complete.");
        telemetry.update();
    }

    private void setOuttakeState(OuttakeState state) {
        outtakeState = state;

        switch (state) {
            case BASE:
                robot.scoring.clawPrimaryPivot.setPosition(0.00);
                robot.scoring.outtakeArmRotation.setPosition(0.00);
                break;

            case COLLECTION:
                robot.scoring.clawPrimaryPivot.setPosition(0.45);
                robot.scoring.outtakeArmRotation.setPosition(0.15);
                break;
        }
    }

    private void setClawState(ClawState state) {
        clawState = state;

        switch (state) {
            case OPEN:
                robot.scoring.clawStatus.setPosition(0.25);
                break;

            case CLOSE:
                robot.scoring.clawStatus.setPosition(1.00);
                break;
        }
    }

    private void verticalSlidesToPosition(int position) {
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

    private void horizontalSlidesToPosition(int position) {
        robot.scoring.horizontalSlideExtension.setTargetPosition(position);

        robot.scoring.horizontalSlideExtension.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        robot.scoring.horizontalSlideExtension.setPower(Power);

        while (opModeIsActive() && (robot.scoring.horizontalSlideExtension.isBusy())) {
        }

        robot.scoring.horizontalSlideExtension.setPower(0);
        robot.scoring.horizontalSlideExtension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        robot.scoring.horizontalSlideExtension.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    // Encoder Based Drive
    private void encoderDrive(double YInches, double Power) {
        final double TICKS_PER_REV = 384.5;
        final double WHEEL_DIAMETER_INCHES = 4.0;
        final double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_INCHES);

        int targetYTicks = (int) (YInches * TICKS_PER_INCH);

        int frontLeftTarget = robot.driveTrain.frontLeft.getCurrentPosition() + targetYTicks;
        int frontRightTarget = robot.driveTrain.frontRight.getCurrentPosition() + targetYTicks;
        int backLeftTarget = robot.driveTrain.backLeft.getCurrentPosition() + targetYTicks;
        int backRightTarget = robot.driveTrain.backRight.getCurrentPosition() + targetYTicks;

        robot.driveTrain.runDriveTrainEncoders();

        robot.driveTrain.frontLeft.setTargetPosition(frontLeftTarget);
        robot.driveTrain.frontRight.setTargetPosition(frontRightTarget);
        robot.driveTrain.backLeft.setTargetPosition(backLeftTarget);
        robot.driveTrain.backRight.setTargetPosition(backRightTarget);

        robot.driveTrain.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.driveTrain.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.driveTrain.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.driveTrain.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while (robot.driveTrain.frontLeft.isBusy() && robot.driveTrain.frontRight.isBusy() && robot.driveTrain.backLeft.isBusy() && robot.driveTrain.backRight.isBusy()) {
            robot.driveTrain.mecDrive(Power, Power, Power, Power);
        }

        robot.driveTrain.brake();
        robot.driveTrain.resetDriveTrainEncoders();
    }

    // Encoder Based Strafe
    private void encoderStrafe(double XInches, double Power) {
        final double TICKS_PER_REV = 384.5;
        final double WHEEL_DIAMETER_INCHES = 4.0;
        final double TICKS_PER_INCH = TICKS_PER_REV / (Math.PI * WHEEL_DIAMETER_INCHES);

        int targetXTicks = (int) (XInches * TICKS_PER_INCH);

        int frontLeftTarget = robot.driveTrain.frontLeft.getCurrentPosition() + targetXTicks;
        int frontRightTarget = robot.driveTrain.frontRight.getCurrentPosition() - targetXTicks;
        int backLeftTarget = robot.driveTrain.backLeft.getCurrentPosition() - targetXTicks;
        int backRightTarget = robot.driveTrain.backRight.getCurrentPosition() + targetXTicks;

        robot.driveTrain.runDriveTrainEncoders();

        robot.driveTrain.frontLeft.setTargetPosition(frontLeftTarget);
        robot.driveTrain.frontRight.setTargetPosition(frontRightTarget);
        robot.driveTrain.backLeft.setTargetPosition(backLeftTarget);
        robot.driveTrain.backRight.setTargetPosition(backRightTarget);

        robot.driveTrain.frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.driveTrain.frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.driveTrain.backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        robot.driveTrain.backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        while (robot.driveTrain.frontLeft.isBusy() && robot.driveTrain.frontRight.isBusy() && robot.driveTrain.backLeft.isBusy() && robot.driveTrain.backRight.isBusy()) {
            robot.driveTrain.mecDrive(Power, -Power, -Power, Power);
        }

        robot.driveTrain.brake();
        robot.driveTrain.resetDriveTrainEncoders();
    }
}