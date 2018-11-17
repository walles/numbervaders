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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.gmail.walles.johan.numbershooter.ObjectiveSoundPool;

import java.util.Random;

public class FallingMaths implements GameObject {
    private static final Random RANDOM = new Random();

    /**
     * How long will it take this question to fall to the bottom of the screen?
     */
    private static final double MS_TO_BOTTOM = 15_000;
    private static final double BASE_PERCENT_PER_MS = 100.0 / MS_TO_BOTTOM;

    /**
     * How many percent faster will the fall be for every level we're easier than the player's own?
     */
    private static final double SPEEDUP_PERCENT_PER_NUMBER = 10;

    private final double percentPerMs;

    private final Model model;

    private double x = -30.0 + 60 * RANDOM.nextDouble();
    private double y = 0;
    public final String question;
    private final Paint paint;

    private boolean dead = false;
    private boolean landing = true;

    public final int answer;
    private final ObjectiveSoundPool.SoundEffect mathsKilled;

    public FallingMaths(int a, int b, int speedupNumber, Model model,
            ObjectiveSoundPool.SoundEffect mathsKilled) {
        this.model = model;
        this.mathsKilled = mathsKilled;

        double speedupFactor = Math.pow(1.0 + SPEEDUP_PERCENT_PER_NUMBER / 100.0, speedupNumber);
        percentPerMs = BASE_PERCENT_PER_MS * speedupFactor;
        question = a + "⋅" + b;
        answer = a * b;

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);  // FIXME: Adapt to screen size
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void stepMs(long deltaMs) {
        if (!landing) {
            doNotLandStepMs(deltaMs);
            return;
        }

        if (y >= 100) {
            // We have landed
            return;
        }

        y += percentPerMs * deltaMs;
        if (y < 100) {
            return;
        }

        // Touchdown!
        model.noMoreMaths();

        // Tell our math friends to fly away or start hovering ominously or something
        for (FallingMaths friend: model.listFallingMaths()) {
            if (friend == this) {
                // We shouldn't stop ourselves
                continue;
            }

            friend.stopLanding();
        }

        // Explode the cannon with our answer as the text
        model.getCannon().explode(Integer.toString(answer));
    }

    /**
     * Update our state while not landing.
     */
    private void doNotLandStepMs(long deltaMs) {
        // FIXME: Do some spectacular dance here?
        y -= (BASE_PERCENT_PER_MS / 2.0) * deltaMs;
        if (y < 0) {
            dead = true;
        }
    }

    private void stopLanding() {
        landing = false;
    }

    @Override
    public void drawOn(Canvas canvas) {
        double coordinatesToScreenFactor = canvas.getHeight() / 100.0;
        double xOffset = canvas.getWidth() / 2;
        float screenX = (float)(x * coordinatesToScreenFactor + xOffset);
        float screenY = (float)(y * coordinatesToScreenFactor);

        canvas.drawText(question, screenX, screenY, paint);
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    public double getY() {
        return y;
    }

    public double getX() { return x; }

    public void explode() {
        mathsKilled.play();
        dead = true;
    }
}
