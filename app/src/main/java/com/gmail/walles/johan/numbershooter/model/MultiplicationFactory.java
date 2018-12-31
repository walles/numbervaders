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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiplicationFactory extends MathsFactory {
    public MultiplicationFactory(int level) {
        super(level);
    }

    public static List<Maths> getMathsUpToLevelExclusive(int nextLevel) {
        int completedLevel = nextLevel - 1;
        int getCount = completedLevel * NEW_MATHS_PER_LEVEL;
        if (getCount <= 0) {
            return Collections.emptyList();
        }

        MultiplicationFactory multiplicationFactory = new MultiplicationFactory(completedLevel);
        List<Maths> allMaths = multiplicationFactory.allMaths;
        if (getCount > allMaths.size()) {
            getCount = allMaths.size();
        }

        return allMaths.subList(0, getCount);
    }

    @Override
    protected List<Maths> listAllMaths() {
        List<Maths> maths = new ArrayList<>(100);
        for (int a = 1; a <= 10; a++) {
            for (int b = 1; b <= 10; b++) {
                maths.add(new Maths(a + "⋅" + b, a, b, a * b));
            }
        }

        return maths;
    }

    @Override
    protected int compare(Maths o1, Maths o2) {
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
    }

    private static int getDifficulty(int number) {
        if (number == 1) {
            return 0;
        }

        // FIXME: Should 10 be on level 1?
        return number;
    }
}
