// Coded By Gurtej Singh

package Auton.Testers;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

import Robot.Robot;

@Autonomous(name = "FunctionTester", group = "Auton")
public class FunctionTester extends LinearOpMode {

    private final Robot robot = new Robot();

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

            }
        }
    }
}