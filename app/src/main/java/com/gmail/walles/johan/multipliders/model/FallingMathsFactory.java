package com.gmail.walles.johan.multipliders.model;

import com.gmail.walles.johan.multipliders.ObjectiveSoundPool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class FallingMathsFactory {
    private static final Random RANDOM = new Random();
    private final Model model;
    private final ObjectiveSoundPool.SoundEffect mathsKilled;
    private final List<Maths> allMaths;

    private static class Maths {
        public final int a;
        public final int b;

        public Maths(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }

    public FallingMathsFactory(Model model, int level,
            ObjectiveSoundPool.SoundEffect mathsKilled)
    {
        this.model = model;
        this.mathsKilled = mathsKilled;

        List<Maths> maths = new ArrayList<>(100);
        for (int a = 1; a <= 10; a++) {
            for (int b = 1; b <= 10; b++) {
                maths.add(new Maths(a, b));
            }
        }

        Collections.sort(maths, (o1, o2) -> {
            int o1difficulty = getDifficulty(o1.a) + getDifficulty(o1.b);
            int o2difficulty = getDifficulty(o2.a) + getDifficulty(o2.b);
            if (o1difficulty > o2difficulty) {
                return -1;
            }
            if (o2difficulty > o1difficulty) {
                return 1;
            }

            int o1MaxDifficulty = Math.max(getDifficulty(o1.a), getDifficulty(o1.b));
            int o2MaxDifficulty = Math.max(getDifficulty(o2.a), getDifficulty(o2.b));
            return Integer.compare(o1MaxDifficulty, o2MaxDifficulty);
        });

        allMaths = maths;
    }

    private static int getDifficulty(int number) {
        if (number == 1) {
            return 0;
        }

        // FIXME: Should 10 be on level 1?
        return number;
    }

    public FallingMaths createChallenge() {
        Maths maths = allMaths.get(RANDOM.nextInt(allMaths.size()));

        // FIXME: Pick something from the Nth chunk of five maths, based on the level
        return new FallingMaths(maths.a, maths.b, model, mathsKilled);
    }
}
