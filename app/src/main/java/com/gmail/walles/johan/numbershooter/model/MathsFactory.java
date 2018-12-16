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

import org.jetbrains.annotations.NonNls;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public abstract class MathsFactory {
    /**
     * How many new assignments are introduced at each level?
     */
    private static final int NEW_MATHS_PER_LEVEL = 5;

    private static final Random RANDOM = new Random();
    private final List<Maths> allMaths;
    private final int level;

    public static MathsFactory create(GameType gameType, int level) {
        switch (gameType) {
            case ADDITION:
                return new AdditionFactory(level);

            case MULTIPLICATION:
                return new MultiplicationFactory(level);

            case SUBTRACTION:
                return new SubtractionFactory(level);

            case DIVISION:
                return new DivisionFactory(level);

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

    public static class Maths {
        public final int a;
        public final int b;
        public final int answer;
        public final @NonNls String question;

        public int easiness;
        public int topEasiness;

        public Maths(@NonNls String question, int a, int b, int answer) {
            this.question = question;
            this.a = a;
            this.b = b;
            this.answer = answer;
        }

        public void setEasiness(int easiness, int topEasiness) {
            this.easiness = easiness;
            this.topEasiness = topEasiness;
        }

        @Override
        public String toString() {
            return question + "=" + answer;
        }
    }

    protected MathsFactory(int level) {
        this.level = level;

        List<Maths> maths = listAllMaths();
        Collections.sort(maths, this::compare);

        allMaths = maths;
    }

    public final Maths pickChallenge() {
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

        int easiness = level - pickFromLevel;
        int topEasiness = topLevel - 1;

        int index = RANDOM.nextInt(NEW_MATHS_PER_LEVEL) + (pickFromLevel - 1) * NEW_MATHS_PER_LEVEL;
        Maths maths = allMaths.get(index);
        maths.setEasiness(easiness, topEasiness);
        return maths;
    }
}
