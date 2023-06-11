package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.util.ElapsedTime;

import java.util.List;

@TeleOp
public class BulkReadTest extends LinearOpMode {
    private DcMotorEx frontRightMotor, frontLeftMotor, backRightMotor, backLeftMotor;
    private final int TEST_CYCLES = 1000;
    private int currentCycle = 0;
    List<LynxModule> hubs = hardwareMap.getAll(LynxModule.class);

    // cycle times
    private double t1 = 0;
    private double t2 = 0;
    private double t3 = 0;

    ElapsedTime timer;

    @Override
    public void runOpMode() throws InterruptedException {
        frontRightMotor = hardwareMap.get(DcMotorEx.class, "frontRight");
        frontLeftMotor = hardwareMap.get(DcMotorEx.class, "frontLeft");
        backRightMotor = hardwareMap.get(DcMotorEx.class, "backRight");
        backLeftMotor = hardwareMap.get(DcMotorEx.class, "backLeft");

        timer = new ElapsedTime();
        waitForStart();

        // test using bulk caching mode set to off. bulk reads are not used (i.e. each sensor is read when its get method is called)
        for (LynxModule hub : hubs){
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.OFF);
        }
        displayCycleTimes("Testing bulk caching OFF mode...");
        timer.reset();
        while (opModeIsActive() && currentCycle < TEST_CYCLES){
            frontRightMotor.getCurrentPosition();
            frontLeftMotor.getCurrentPosition();
            backRightMotor.getCurrentPosition();
            backLeftMotor.getCurrentPosition();

            frontRightMotor.getVelocity();
            frontLeftMotor.getVelocity();
            backRightMotor.getVelocity();
            backLeftMotor.getVelocity();

            currentCycle++;
        }
        t1 = timer.milliseconds() / currentCycle;

        // test using auto mode which should produce the same results as manual given we are not calling the same sensors get method twice
        for (LynxModule hub : hubs){
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.AUTO);
        }
        displayCycleTimes("Testing bulk caching AUTO mode...");
        currentCycle = 0;
        timer.reset();
        while (opModeIsActive() && currentCycle < TEST_CYCLES){
            frontRightMotor.getCurrentPosition();
            frontLeftMotor.getCurrentPosition();
            backRightMotor.getCurrentPosition();
            backLeftMotor.getCurrentPosition();

            frontRightMotor.getVelocity();
            frontLeftMotor.getVelocity();
            backRightMotor.getVelocity();
            backLeftMotor.getVelocity();

            currentCycle++;
        }
        t2 = timer.milliseconds() / currentCycle;

        // test using manual mode which should produce the same results as auto mode given wwe arent calling any get methods twice
        for (LynxModule hub : hubs){
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }
        displayCycleTimes("Testing bulk caching MANUAL mode...");
        currentCycle = 0;
        timer.reset();
        while (opModeIsActive() && currentCycle < TEST_CYCLES){
            for (LynxModule hub : hubs){
                hub.clearBulkCache();
            }

            frontRightMotor.getCurrentPosition();
            frontLeftMotor.getCurrentPosition();
            backRightMotor.getCurrentPosition();
            backLeftMotor.getCurrentPosition();

            frontRightMotor.getVelocity();
            frontLeftMotor.getVelocity();
            backRightMotor.getVelocity();
            backLeftMotor.getVelocity();

            currentCycle++;
        }
        t3 = timer.milliseconds() / currentCycle;

        displayCycleTimes("Finished testing.");

        while(opModeIsActive());
    }
    // Display three comparison times.
    private void displayCycleTimes(String status) {
        telemetry.addData("Testing", status);
        telemetry.addData("Cache = OFF",    "%5.1f mS/cycle", t1);
        telemetry.addData("Cache = AUTO",   "%5.1f mS/cycle", t2);
        telemetry.addData("Cache = MANUAL", "%5.1f mS/cycle", t3);
        telemetry.update();
    }
}
