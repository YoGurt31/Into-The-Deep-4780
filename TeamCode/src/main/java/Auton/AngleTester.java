//// Coded By Gurtej Singh
//
//package Auton;
//
//import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
//import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
//
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//
//import Robot.Robot;
//
//@Autonomous(name = "AngleTester", group = "Auton")
//public class AngleTester extends LinearOpMode {
//
//    private final Robot robot = new Robot();
//    double xAngle, yAngle, zAngle;
//    double xOffset, yOffset, zOffset;
//
//    @Override
//    public void runOpMode() throws InterruptedException {
//        telemetry.addLine("Initializing...");
//        telemetry.update();
//
//        robot.init(hardwareMap);
//
//        telemetry.addLine("\nWaiting for start...");
//        telemetry.update();
//
//        waitForStart();
//
//        if (opModeIsActive()) {
//
//            xOffset = robot.driveTrain.IMU.getRobotYawPitchRollAngles().getPitch(AngleUnit.DEGREES);
//            yOffset = robot.driveTrain.IMU.getRobotYawPitchRollAngles().getRoll(AngleUnit.DEGREES);
//            zOffset = robot.driveTrain.IMU.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES);
//
//            while (opModeIsActive()) {
//                xAngle = robot.driveTrain.IMU.getRobotYawPitchRollAngles().getPitch(AngleUnit.DEGREES) - xOffset;
//                yAngle = robot.driveTrain.IMU.getRobotYawPitchRollAngles().getRoll(AngleUnit.DEGREES) - yOffset;
//                zAngle = robot.driveTrain.IMU.getRobotYawPitchRollAngles().getYaw(AngleUnit.DEGREES) - zOffset;
//
//                telemetry.addData("X-Angle (Pitch)", "%.2f degrees", xAngle);
//                telemetry.addData("Y-Angle (Roll)", "%.2f degrees", yAngle);
//                telemetry.addData("Z-Angle (Yaw)", "%.2f degrees", zAngle);
//
//
//                telemetry.update();
//
//            }
//        }
//
//        telemetry.addLine("Autonomous complete.");
//        telemetry.update();
//    }
//}