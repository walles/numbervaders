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
    private final int level;

    private static class Maths {
        public final int a;
        public final int b;

        public Maths(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return a + "*" + b + "=" + (a * b);
        }
    }

    public FallingMathsFactory(Model model, int level,
            ObjectiveSoundPool.SoundEffect mathsKilled)
    {
        this.model = model;
        this.level = level;
        this.mathsKilled = mathsKilled;

        List<Maths> maths = new ArrayList<>(100);
        for (int a = 1; a <= 10; a++) {
            for (int b = 1; b <= 10; b++) {
                maths.add(new Maths(a, b));
            }
        }

        Collections.sort(maths, (o1, o2) -> {
            int o1difficulty = getDifficulty(o1.a) + getDifficulty(o1.b);
            if (o1.a == 1 || o1.b == 1) {
                o1difficulty = 0;
            }

            int o2difficulty = getDifficulty(o2.a) + getDifficulty(o2.b);
            if (o2.a == 1 || o2.b == 1) {
                o2difficulty = 0;
            }

            if (o1difficulty > o2difficulty) {
                return 1;
            }
            if (o2difficulty > o1difficulty) {
                return -1;
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
        // 0-19
        int pickFromLevel;

        if (level == 1) {
            pickFromLevel = 1;
        } else if (level > 20) {
            pickFromLevel = RANDOM.nextInt(20) + 1;
        } else if (RANDOM.nextBoolean()) {
            // Half of the time we pick from the current level
            pickFromLevel = level;
        } else {
            // Half of the time we pick from a random level below ourselves
            pickFromLevel = RANDOM.nextInt(level) + 1;
        }

        int index = RANDOM.nextInt(5) + (pickFromLevel - 1) * 5;
        Maths maths = allMaths.get(index);

        return new FallingMaths(maths.a, maths.b, level - pickFromLevel, model, mathsKilled);
    }
}
