package com.example.meepmeeptesting;

import com.acmerobotics.roadrunner.Pose2d;
import com.acmerobotics.roadrunner.Vector2d;
import com.acmerobotics.roadrunner.Vector2dDual;
import com.noahbres.meepmeep.MeepMeep;
import com.noahbres.meepmeep.roadrunner.DefaultBotBuilder;
import com.noahbres.meepmeep.roadrunner.entity.RoadRunnerBotEntity;

public class MeepMeepTesting {
    public static void main(String[] args) {
        MeepMeep meepMeep = new MeepMeep(600);

        RoadRunnerBotEntity myBot = new DefaultBotBuilder(meepMeep)
                // Set bot constraints: maxVel, maxAccel, maxAngVel, maxAngAccel, track width
                .setConstraints(80, 80, Math.toRadians(180), Math.toRadians(180), 16)
                .build();

        myBot.setDimensions(16,17);

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(0, -62, Math.toRadians(90)))
                // SPECIMEN PATH (Sweeping) (new Pose2d(0, -62, Math.toRadians(90)))
                // Drive To Bar And Score Specimen #1
                .setReversed(false)
                .strafeTo(new Vector2d(-6, -30))

                // Drive To Collect Samples
                .setReversed(true)
                .setTangent(5)
                .splineToLinearHeading(new Pose2d(30, -40, Math.toRadians(55)), .8)

                // Collect Sample #1
                .turnTo(Math.toRadians(-35))
                .turnTo(Math.toRadians(45))

                // Collect Sample #2
                .turnTo(Math.toRadians(-35))
                .turnTo(Math.toRadians(35))

                // Collect Sample #3
                .turnTo(Math.toRadians(-35))
                .turnTo(Math.toRadians(90))

                // Collect Specimen #2
                .setReversed(true)
                .strafeTo(new Vector2d(40, -62))

                // Score Specimen #2
                .setReversed(false)
                .strafeTo(new Vector2d(-3, -30))

                // Collect Specimen #3
                .setReversed(true)
                .setTangent(30)
                .splineToConstantHeading(new Vector2d(24, -48), 0)
                .splineToConstantHeading(new Vector2d(40, -62), 30)

                // Score Specimen #3
                .setReversed(false)
                .strafeTo(new Vector2d(0, -30))

                // Collect Specimen #4
                .setReversed(true)
                .setTangent(30)
                .splineToConstantHeading(new Vector2d(24, -48), 0)
                .splineToConstantHeading(new Vector2d(40, -62), 30)

                // Score Specimen #4
                .setReversed(false)
                .strafeTo(new Vector2d(3, -30))

                // Collect Specimen #5
                .setReversed(true)
                .setTangent(30)
                .splineToConstantHeading(new Vector2d(24, -48), 0)
                .splineToConstantHeading(new Vector2d(40, -62), 30)

                // Score Specimen #5
                .setReversed(false)
                .strafeTo(new Vector2d(7, -30))

                .build());

//                // SPECIMEN PATH (Sweeping) (new Pose2d(0, -62, Math.toRadians(90)))
//                // Drive To Bar And Score Specimen #1
//                .strafeTo(new Vector2d(-6, -30))
//
//                // Drive To Collect Samples
//                .setTangent(5)
//                .splineToConstantHeading(new Vector2d(24, -40), .25)
//
//                // Collect Sample #1
//                .splineToConstantHeading(new Vector2d(48, -9), .5)
//                .strafeTo(new Vector2d(48, -50))
//
//                // Collect Sample #2
//                .setTangent(1.5)
//                .splineToConstantHeading(new Vector2d(56, -9), .5)
//                .strafeTo(new Vector2d(56, -50))
//
////                // Collect Sample #3
////                .setTangent(1.5)
////                .splineToConstantHeading(new Vector2d(64, -9), .5)
////                .strafeTo(new Vector2d(64, -50))
//
//                // Collect Specimen #2
//                .setTangent(3)
//                .splineToConstantHeading(new Vector2d(40,-62),4.5)
//
//                // Score Specimen #2
//                .strafeTo(new Vector2d(-3, -30))
//
//                // Collect Specimen #3
//                .setTangent(30)
//                .splineToConstantHeading(new Vector2d(24, -48), 0)
//                .splineToConstantHeading(new Vector2d(40, -62), 30)
//
//                // Score Specimen #3
//                .strafeTo(new Vector2d(0, -30))
//
//                // Collect Specimen #4
//                .setTangent(30)
//                .splineToConstantHeading(new Vector2d(24, -48), 0)
//                .splineToConstantHeading(new Vector2d(40, -62), 30)
//
//                // Score Specimen #4
//                .strafeTo(new Vector2d(3, -30))
//
////                // Collect Specimen #5
////                .setTangent(30)
////                .splineToConstantHeading(new Vector2d(24, -48), 0)
////                .splineToConstantHeading(new Vector2d(40, -62), 30)
////
////                // Score Specimen #5
////                .strafeTo(new Vector2d(7, -30))
//
//                // Park
//                .strafeTo(new Vector2d(60, -60))
//
//                .build());



//                // SAMPLE PATH (4 Samples) (new Pose2d(-39, -62, Math.toRadians(90)))
//                // Drive To Bucket And Score Sample
//                .setReversed(false)
//                .strafeToLinearHeading(new Vector2d(-53, -53), Math.toRadians(45))
//                .waitSeconds(1)
//                .setReversed(true)
//                .strafeTo(new Vector2d(-60, -60))
//                .waitSeconds(2)
//
//                // Collect Sample #1
//                .setReversed(false)
//                .strafeToLinearHeading(new Vector2d(-48, -50), Math.toRadians(90))
//                .waitSeconds(2)
//
//                // Score Sample #1
//                .setReversed(true)
//                .strafeToLinearHeading(new Vector2d(-60, -60), Math.toRadians(45))
//                .waitSeconds(2)
//
//                // Collect Sample #2
//                .setReversed(false)
//                .strafeToLinearHeading(new Vector2d(-60, -50), Math.toRadians(90))
//                .waitSeconds(2)
//
//                // Score Sample #2
//                .setReversed(true)
//                .strafeToLinearHeading(new Vector2d(-60, -60), Math.toRadians(45))
//                .waitSeconds(2)
//
//                // Collect Sample #3
//                .setReversed(false)
//                .strafeToLinearHeading(new Vector2d(-60, -50), Math.toRadians(110))
//                .waitSeconds(2)
//
//                // Score Sample #3
//                .setReversed(true)
//                .strafeToLinearHeading(new Vector2d(-60, -60), Math.toRadians(45))
//                .waitSeconds(2)
//
//                // Park
//                .setReversed(false)
//                .splineTo(new Vector2d(-24, 0), 0)
//                .waitSeconds(2)
//
//                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_JUICE_DARK)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}