/*
 * Copyright 2018, Johan Walles <johan.walles@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.walles.johan.numbershooter.model;

import com.gmail.walles.johan.numbershooter.GameType;
import com.gmail.walles.johan.numbershooter.ObjectiveSoundPool;

import org.jetbrains.annotations.NonNls;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class FallingMathsFactory {
    /**
     * How much faster do the simplest assignments go at the top level?
     * <p>
     * The top level is defined as the last level where we still get new challenges. After that
     * things will just go faster and faster.
     */
    private static final double SPEEDUP_FACTOR_AT_TOP_LEVEL = 6.5;

    /**
     * How many new assignments are introduced at each level?
     */
    private static final int NEW_MATHS_PER_LEVEL = 5;

    private static final Random RANDOM = new Random();
    private final ObjectiveSoundPool.SoundEffect mathsKilled;
    private final List<Maths> allMaths;
    private final int level;

    public static FallingMathsFactory create(
            GameType gameType, int level, ObjectiveSoundPool.SoundEffect mathsKilled)
    {
        switch (gameType) {
            case ADDITION:
                return new AdditionFactory(level, mathsKilled);

            case MULTIPLICATION:
                return new MultiplicationFactory(level, mathsKilled);

            case SUBTRACTION:
                return new SubtractionFactory(level, mathsKilled);

            case DIVISION:
                return new DivisionFactory(level, mathsKilled);

            default:
                throw new UnsupportedOperationException("Unhandled game type: " + gameType);
        }
    }

    /**
     * List all possible maths problems. For all levels, not just one.
     */
    protected abstract List<Maths> listAllMaths();

    /**
     * -1 means o1 &lt; o2, 0 means o1 == o2, 1 means o1 &gt; o2.
     *
     * @see Comparator#compare(Object, Object)
     */
    protected abstract int compare(Maths o1, Maths o2);

    protected static class Maths {
        public final int a;
        public final int b;
        public final int answer;
        public final @NonNls String question;

        public Maths(@NonNls String question, int a, int b, int answer) {
            this.question = question;
            this.a = a;
            this.b = b;
            this.answer = answer;
        }

        @Override
        public String toString() {
            return question + "=" + answer;
        }
    }

    protected FallingMathsFactory(int level,
            ObjectiveSoundPool.SoundEffect mathsKilled)
    {
        this.level = level;
        this.mathsKilled = mathsKilled;

        List<Maths> maths = listAllMaths();
        Collections.sort(maths, this::compare);

        allMaths = maths;
    }

    public final FallingMaths createChallenge(Model model) {
        int topLevel = allMaths.size() / NEW_MATHS_PER_LEVEL;

        // 0 - topLevel
        int pickFromLevel;

        if (level == 1) {
            pickFromLevel = 1;
        } else if (level > topLevel) {
            pickFromLevel = RANDOM.nextInt(topLevel) + 1;
        } else if (RANDOM.nextBoolean()) {
            // Half of the time we pick from the current level
            pickFromLevel = level;
        } else {
            // Half of the time we pick from a random level below ourselves
            pickFromLevel = RANDOM.nextInt(level) + 1;
        }

        int index = RANDOM.nextInt(NEW_MATHS_PER_LEVEL) + (pickFromLevel - 1) * NEW_MATHS_PER_LEVEL;
        Maths maths = allMaths.get(index);

        int easiness = level - pickFromLevel;
        int topEasiness = topLevel - 1;
        double speedupPower = easiness / (double)topEasiness;
        double speedupFactor = Math.pow(SPEEDUP_FACTOR_AT_TOP_LEVEL, speedupPower);

        return new FallingMaths(maths.question, maths.answer, model, speedupFactor, mathsKilled);
    }
}
