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

        myBot.runAction(myBot.getDrive().actionBuilder(new Pose2d(-36, -62, Math.toRadians(90)))
//                // SPECIMEN PATH (new Pose2d(0, -62, Math.toRadians(90)))
//                // Drive To Bar And Score Specimen #1
//                .strafeTo(new Vector2d(-6, -30))
//                .waitSeconds(.8)
//
//                // Drive To Collect Samples
//                .setTangent(5)
//                .splineToConstantHeading(new Vector2d(24, -48), 0)
//                .splineToConstantHeading(new Vector2d(38, -24), 1.5)
//                .setTangent(1.5)
//                .splineToConstantHeading(new Vector2d(48, -9), 0) // In Front of Sample 1
//                .strafeTo(new Vector2d(48, -56)) // Retrieved Sample 1
//
//                .setTangent(1.5)
//                .splineToConstantHeading(new Vector2d(56, -9), 0) // In Front of Sample 2
//                .strafeTo(new Vector2d(56, -56)) // Retrieved Sample 2
//
//                .setTangent(1.5)
//                .splineToConstantHeading(new Vector2d(66, -9), 0) // In Front of Sample 3
//                .strafeTo(new Vector2d(66, -56)) // Retrieved Sample 3
//
//                // Collect Specimen #2
//                .strafeTo(new Vector2d(62,-62))
//                .waitSeconds(.5)
//
//                // Score Specimen #2
//                .strafeTo(new Vector2d(-3, -30))
//                .waitSeconds(.8)
//
//                // Collect Specimen #3
//                .setTangent(30)
//                .splineToConstantHeading(new Vector2d(24, -48), 0)
//                .splineToConstantHeading(new Vector2d(40, -62), 30)
//                .waitSeconds(.5)
//
//                // Score Specimen #3
//                .strafeTo(new Vector2d(0, -30))
//                .waitSeconds(.8)
//
//                // Collect Specimen #4
//                .setTangent(30)
//                .splineToConstantHeading(new Vector2d(24, -48), 0)
//                .splineToConstantHeading(new Vector2d(40, -62), 30)
//                .waitSeconds(.5)
//
//                // Score Specimen #4
//                .strafeTo(new Vector2d(3, -30))
//                .waitSeconds(.8)
//
//                // Collect Specimen #5
//                .setTangent(30)
//                .splineToConstantHeading(new Vector2d(24, -48), 0)
//                .splineToConstantHeading(new Vector2d(40, -62), 30)
//                .waitSeconds(.5)
//
//                // Score Specimen #5
//                .strafeTo(new Vector2d(7, -30))
//                .waitSeconds(.8)
//
//                .build());


                // SAMPLE PATH (new Pose2d(0, -62, Math.toRadians(90)))
                // Drive To Bar And Score Specimen #1
                .waitSeconds(8)
                .strafeTo(new Vector2d(-6, -30))

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
                .strafeTo(new Vector2d(7, -30))
                .waitSeconds(.8)

                .build());

        meepMeep.setBackground(MeepMeep.Background.FIELD_INTO_THE_DEEP_OFFICIAL)
                .setDarkMode(true)
                .setBackgroundAlpha(0.95f)
                .addEntity(myBot)
                .start();
    }
}