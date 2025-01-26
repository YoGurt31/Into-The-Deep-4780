// Coded By Gurtej Singh

package Auton.Testers;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import Robot.Robot;

@Autonomous(name = "FunctionTester", group = "Auton")
public class FunctionTester extends LinearOpMode {

    private final Robot robot = new Robot();

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

        robot.init(hardwareMap);

        telemetry.addLine("\nWaiting for start...");
        telemetry.update();

        waitForStart();

        if (opModeIsActive()) {

            while (opModeIsActive()) {
                horizontalSlidesToPosition(EXTENDED);
//                setIntakeState(IntakeState.ACTIVE);

                horizontalSlidesToPosition(RETRACTED);
//                setIntakeState(IntakeState.INACTIVE);
            }
        }
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

    private void setIntakeState(IntakeState state) {
        intakeState = state;

        switch (state) {
            case INACTIVE:
                robot.scoring.rollerInOut.setPower(0.0);
                break;

            case ACTIVE:
                robot.scoring.rollerInOut.setPower(1.00);
                break;
        }
    }
}