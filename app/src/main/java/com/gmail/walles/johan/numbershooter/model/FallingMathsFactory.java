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

public class FallingMathsFactory {
    /**
     * How much faster do the simplest assignments go at the top level?
     * <p>
     * The top level is defined as the last level where we still get new challenges. After that
     * things will just go faster and faster.
     */
    private static final double SPEEDUP_FACTOR_AT_TOP_LEVEL = 6.5;

    private final ObjectiveSoundPool.SoundEffect mathsKilled;
    private final float objectSizePixels;
    private final MathsFactory mathsFactory;

    public FallingMathsFactory(GameType gameType, int level, float objectSizePixels,
            ObjectiveSoundPool.SoundEffect mathsKilled)
    {
        this.mathsFactory = MathsFactory.create(gameType, level);
        this.objectSizePixels = objectSizePixels;
        this.mathsKilled = mathsKilled;
    }

    public final FallingMaths createChallenge(Model model) {
        MathsFactory.Maths maths = mathsFactory.pickChallenge();

        double speedupPower = maths.easiness / (double)maths.topEasiness;
        double speedupFactor = Math.pow(SPEEDUP_FACTOR_AT_TOP_LEVEL, speedupPower);

        return new FallingMaths(maths.question, maths.answer, model, speedupFactor, objectSizePixels, mathsKilled);
    }
}
