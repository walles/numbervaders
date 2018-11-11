package com.gmail.walles.johan.multipliders.model;

import com.gmail.walles.johan.multipliders.ObjectiveSoundPool;

class FallingMathsFactory {
    private final Model model;
    private final ObjectiveSoundPool.SoundEffect mathsKilled;

    public FallingMathsFactory(Model model, int level,
            ObjectiveSoundPool.SoundEffect mathsKilled)
    {
        this.model = model;
        this.mathsKilled = mathsKilled;
    }

    public FallingMaths createChallenge() {
        return new FallingMaths(model, mathsKilled);
    }
}
