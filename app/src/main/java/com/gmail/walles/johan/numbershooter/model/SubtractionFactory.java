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

class SubtractionFactory extends FallingMathsFactory {
    public SubtractionFactory(int level, float objectSizePixels, ObjectiveSoundPool.SoundEffect mathsKilled) {
        super(level, objectSizePixels, mathsKilled);
    }

    @Override
    protected List<Maths> listAllMaths() {
        List<Maths> maths = new ArrayList<>();

        for (int answer = 1; answer <= 15; answer++) {
            for (int b = 1; b <= 15; b++) {
                int a = answer + b;
                maths.add(new Maths(a + "-" + b, a, b, answer));
            }
        }

        return maths;
    }

    @Override
    protected int compare(Maths o1, Maths o2) {
        int primary = Integer.compare(o1.b, o2.b);
        if (primary != 0) {
            return primary;
        }

        return Integer.compare(o1.a, o2.a);
    }
}
