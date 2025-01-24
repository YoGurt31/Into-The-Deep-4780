// Coded By Gurtej Singh

package TeleOp;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;

import Robot.Robot;

@TeleOp(name = "TeleOp", group = "TeleOp")
public class TeleOp4780 extends LinearOpMode {

    private final Robot robot = new Robot();

    OuttakeState outtakeState = OuttakeState.SCORING;

    private final double intakeLiftedPosition = 0.00;
    private final double intakeLoweredPosition = 0.40;

    boolean lastAButtonState = false;
    boolean lastBButtonState = false;
    boolean lastXButtonState = false;

    enum OuttakeState {
        BASE,
        COLLECTION,
        SCORING
    }

    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addLine("Initialized! Activate Controller(s)...");

        robot.init(hardwareMap);
        robot.driveTrain.brake();

        robot.driveTrain.resetDriveTrainEncoders();
        robot.scoring.resetScoringEncoders();

        waitForStart();

        ElapsedTime Timer = new ElapsedTime();
        Timer.reset();

        if (Timer.seconds() > 120) {
            requestOpModeStop();
        }

        while (opModeIsActive() && Timer.seconds() < 120) {

            robot.driveTrain.runWithoutDriveTrainEncoders();
            robot.scoring.runScoringEncoders();

            // Drive System
            double drive  = -gamepad1.left_stick_y;
            double strafe = gamepad1.left_stick_x;
            double turn   = -gamepad1.right_stick_x;

            double theta = Math.atan2(drive, strafe);
            double power = Math.hypot(strafe, drive);
            double sin = Math.sin(theta - Math.PI / 4);
            double cos = Math.cos(theta - Math.PI / 4);
            double max = Math.max(Math.abs(sin), Math.abs(cos));

            double frontLeftPower  = power * cos / max - turn;
            double frontRightPower = power * sin / max + turn;
            double backLeftPower   = power * sin / max - turn;
            double backRightPower  = power * cos / max + turn;

            if (power + Math.abs(turn) > 1) {
                frontLeftPower  /= power + Math.abs(turn);
                frontRightPower /= power + Math.abs(turn);
                backLeftPower   /= power + Math.abs(turn);
                backRightPower  /= power + Math.abs(turn);
            }

            robot.driveTrain.mecDrive(frontLeftPower, frontRightPower, backLeftPower, backRightPower);

            if (strafe == 0 && drive == 0) {
                robot.driveTrain.brake();
            }

            // Drive Telemetry
            telemetry.addLine("----- Drive Info -----");
            telemetry.addData("FL Power", "%.2f", frontLeftPower);
            telemetry.addData("FR Power", "%.2f", frontRightPower);
            telemetry.addData("BL Power", "%.2f", backLeftPower);
            telemetry.addData("BR Power", "%.2f", backRightPower);
            telemetry.addLine();
            telemetry.addData("Drive", "%.2f", drive);
            telemetry.addData("Turn", "%.2f", turn);
            telemetry.addData("Strafe", "%.2f", strafe);

            telemetry.addLine("\n");



            // Horizontal Slide Extension Control
            double horizontalSlideExtensionPower = 0;
            if (gamepad1.dpad_down)  { horizontalSlideExtensionPower = -1.0; }
            if (gamepad1.dpad_up) { horizontalSlideExtensionPower =  1.0; }
            int currentHorizontalSlidePosition = robot.scoring.horizontalSlideExtension.getCurrentPosition();
            int horizontalSlideExtensionMax  = 2500;  // Max Value
            int horizontalSlideExtensionMin  =  -50;  // Min Value
            int horizontalToleranceThreshold =   25;  // Threshold Value (Adjust)

            if (currentHorizontalSlidePosition < horizontalSlideExtensionMax - horizontalToleranceThreshold) {
                double adjustedPower = horizontalSlideExtensionPower * (1 - (double)(currentHorizontalSlidePosition - (horizontalSlideExtensionMax - horizontalToleranceThreshold)) / horizontalToleranceThreshold);
                robot.scoring.horizontalSlideExtension.setPower(Math.min(horizontalSlideExtensionPower, adjustedPower));
            }

            else if (robot.scoring.horizontalSlideExtension.getCurrentPosition() > horizontalSlideExtensionMin) {
                double adjustedPower = horizontalSlideExtensionPower * (1 - (double)((horizontalSlideExtensionMin + horizontalToleranceThreshold) - currentHorizontalSlidePosition) / horizontalToleranceThreshold);
                robot.scoring.horizontalSlideExtension.setPower(Math.max(horizontalSlideExtensionPower, adjustedPower));
            }

            else {
                robot.scoring.horizontalSlideExtension.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                robot.scoring.horizontalSlideExtension.setPower(0);
            }

            telemetry.addLine("----- Horizontal Slide Extension -----");
            telemetry.addData("Power", "%.2f", horizontalSlideExtensionPower);
            telemetry.addData("Position", "%d", robot.scoring.horizontalSlideExtension.getCurrentPosition());

            telemetry.addLine("\n");



            // Intake Roller Control
            boolean manualForward = gamepad1.right_bumper;
            boolean manualReverse = gamepad1.left_bumper;

            if (manualForward) {
                robot.scoring.intakePivot.setPosition(intakeLoweredPosition);
                robot.scoring.rollerInOut.setDirection(DcMotorSimple.Direction.FORWARD);
                robot.scoring.rollerInOut.setPower(1);
            } else if (manualReverse) {
                robot.scoring.intakePivot.setPosition(intakeLiftedPosition);
                robot.scoring.rollerInOut.setDirection(DcMotorSimple.Direction.REVERSE);
                robot.scoring.rollerInOut.setPower(1);
            } else {
                robot.scoring.intakePivot.setPosition(intakeLiftedPosition);
                robot.scoring.rollerInOut.setPower(0);
            }

            String rollerStatus = "Stopped";
            if (robot.scoring.rollerInOut.getPower() > 0) {
                if (robot.scoring.rollerInOut.getDirection() == DcMotorSimple.Direction.FORWARD) {
                    rollerStatus = "Intaking";
                } else if (robot.scoring.rollerInOut.getDirection() == DcMotorSimple.Direction.REVERSE) {
                    rollerStatus = "Outtaking";
                }
            } else {
                rollerStatus = "Stopped";
            }

            telemetry.addLine("----- Roller Info -----");
            telemetry.addData("Intake Roller Status", rollerStatus);
            telemetry.addData("Servo Position", robot.scoring.intakePivot.getPosition());

            telemetry.addLine("\n");



            // Intaked Colored Specimen Indicator
            int Red2 = robot.scoring.colorSensor2.red();
            int Green2 = robot.scoring.colorSensor2.green();
            int Blue2 = robot.scoring.colorSensor2.blue();

            boolean isYellow   = Red2 > 200 && Green2 > 300 && Blue2 < 200;
            boolean isRed      = Red2 > 250 && Green2 < 225 && Blue2 < 125;
            boolean isBlue     = Red2 < 125 && Green2 < 225 && Blue2 > 250;

            RevBlinkinLedDriver.BlinkinPattern currentPattern = RevBlinkinLedDriver.BlinkinPattern.HEARTBEAT_WHITE;
            RevBlinkinLedDriver.BlinkinPattern targetPattern;

            ElapsedTime ColorTimer = new ElapsedTime();

            if (isYellow) {
                targetPattern = RevBlinkinLedDriver.BlinkinPattern.YELLOW;
            } else if (isRed) {
                targetPattern = RevBlinkinLedDriver.BlinkinPattern.RED;
            } else if (isBlue) {
                targetPattern = RevBlinkinLedDriver.BlinkinPattern.BLUE;
            } else {
                targetPattern = RevBlinkinLedDriver.BlinkinPattern.BLACK;
            }

            if (currentPattern != targetPattern || ColorTimer.seconds() > 2.5) {
                currentPattern = targetPattern;
                robot.scoring.blinkinLedDriver.setPattern(currentPattern);
                ColorTimer.reset();
            }

            telemetry.addLine("----- Color Sensor Info -----");
            telemetry.addData("Red: ", Red2);
            telemetry.addData("Green: ", Green2);
            telemetry.addData("Blue: ", Blue2);
            telemetry.addLine();
            telemetry.addData("Is Yellow: ", isYellow);
            telemetry.addData("Is Blue: ", isBlue);
            telemetry.addData("Is Red: ", isRed);

            telemetry.addLine("\n");



            // Vertical Slide Extension Control
            double verticalSlideExtensionPower = gamepad1.left_trigger - gamepad1.right_trigger;
            int currentVerticalSlidePosition = (robot.scoring.verticalSlideExtension1.getCurrentPosition() + robot.scoring.verticalSlideExtension2.getCurrentPosition()) / 2;
            int verticalSlideExtensionMin  =  -75;  // Min Value
            int verticalToleranceThreshold =   25;  // Threshold Value (Adjust)
            double slowFactor = 0.75;

            final double HOLD = 0.0005;
            boolean Holding = false;

            if (currentVerticalSlidePosition > 200 && robot.scoring.verticalSlideExtension1.getCurrentPosition() < 1600 && robot.scoring.verticalSlideExtension2.getCurrentPosition() < 1600 && verticalSlideExtensionPower == 0) {
                robot.scoring.verticalSlideExtension1.setPower(HOLD);
                robot.scoring.verticalSlideExtension2.setPower(HOLD);
                Holding = true;
            }

            else if (verticalSlideExtensionPower != 0 && currentVerticalSlidePosition > verticalSlideExtensionMin) {
                if (currentVerticalSlidePosition <= verticalToleranceThreshold) {
                    double adjustedPower = verticalSlideExtensionPower * slowFactor;
                    robot.scoring.verticalSlideExtension1.setPower(adjustedPower);
                    robot.scoring.verticalSlideExtension2.setPower(adjustedPower);
                } else {
                    robot.scoring.verticalSlideExtension1.setPower(verticalSlideExtensionPower);
                    robot.scoring.verticalSlideExtension2.setPower(verticalSlideExtensionPower);
                }
            }

            else {
                robot.scoring.verticalSlideExtension1.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                robot.scoring.verticalSlideExtension2.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
                robot.scoring.verticalSlideExtension1.setPower(0);
                robot.scoring.verticalSlideExtension2.setPower(0);
            }

            telemetry.addLine("----- Vertical Slide Extension -----");
            telemetry.addData("Power", "%.2f", verticalSlideExtensionPower);
            telemetry.addData("HOLD Power", Holding ? String.format("%.4f", HOLD) : "Not Holding");
            telemetry.addData("Position", "%d", currentVerticalSlidePosition);

            telemetry.addLine("\n");



            // Outtake System Control
            boolean currentXButtonState = gamepad1.x;
            if (currentXButtonState && !lastXButtonState) {
                if (robot.scoring.clawStatus.getPosition() == 0.75) {
                    robot.scoring.clawStatus.setPosition(0.25); // Open
                } else {
                    robot.scoring.clawStatus.setPosition(0.75); // Close
                }
            }
            lastXButtonState = currentXButtonState;

            boolean currentAButtonState = gamepad1.a;
            if (currentAButtonState && !lastAButtonState) {
                switch (outtakeState) {
                    case BASE:
                        outtakeState = OuttakeState.SCORING;
                        break;

                    case COLLECTION:
                        outtakeState = OuttakeState.SCORING;
                        break;

                    case SCORING:
                        outtakeState = OuttakeState.COLLECTION;
                        break;
                }
            }
            lastAButtonState = currentAButtonState;

            boolean currentBButtonState = gamepad1.b;
            if (currentBButtonState && !lastBButtonState) {
                switch (outtakeState) {
                    case BASE:
                        outtakeState = OuttakeState.COLLECTION;
                        break;

                    case COLLECTION:
                        outtakeState = OuttakeState.BASE;
                        break;

                    case SCORING:
                        outtakeState = OuttakeState.BASE;
                        break;
                }
            }
            lastBButtonState = currentBButtonState;

            switch (outtakeState) {
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

            telemetry.addLine("----- Outtake System Status -----");
            telemetry.addData("Current Case", outtakeState);
            telemetry.addData("Arm Rotation Position", "%.2f", robot.scoring.outtakeArmRotation.getPosition());
            telemetry.addData("Primary Pivot Position", "%.2f", robot.scoring.clawPrimaryPivot.getPosition());

            double clawPosition = robot.scoring.clawStatus.getPosition();
            String clawStatus = clawPosition == 0.25 ? "Open" : clawPosition == 0.75 ? "Closed" : "Partially Open";
            telemetry.addData("Claw Status", clawStatus);
            telemetry.addData("Claw Position", "%.2f", clawPosition);

            telemetry.addLine("\n");

            telemetry.update();
        }
    }
}