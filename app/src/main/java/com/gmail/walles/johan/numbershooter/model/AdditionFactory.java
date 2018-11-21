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

import com.gmail.walles.johan.numbershooter.ObjectiveSoundPool;

import java.util.ArrayList;
import java.util.List;

class AdditionFactory extends FallingMathsFactory {
    public AdditionFactory(int level, ObjectiveSoundPool.SoundEffect mathsKilled) {
        super(level, mathsKilled);
    }

    @Override
    protected List<Maths> listAllMaths() {
        List<Maths> maths = new ArrayList<>(100);
        for (int a = 1; a <= 15; a++) {
            for (int b = 1; b <= 15; b++) {
                maths.add(new Maths(a + "+" + b, a, b, a + b));
            }
        }

        return maths;
    }

    @Override
    protected int compare(Maths o1, Maths o2) {
        int o1Min = Math.min(o1.a, o1.b);
        int o2Min = Math.min(o2.a, o2.b);
        int minComparison = Integer.compare(o1Min, o2Min);
        if (minComparison != 0) {
            return minComparison;
        }

        int o1Max = Math.max(o1.a, o1.b);
        int o2Max = Math.max(o2.a, o2.b);
        return Integer.compare(o1Max, o2Max);
    }
}
