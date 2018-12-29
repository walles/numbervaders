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

public class Cannon implements GameObject {
    private static final int DEBRIS_COUNT_ON_FAIL = 3;
    private static final int DEBRIS_COUNT_ON_EXPLODE = 6;
    private static final int X = 0;
    private static final int Y = 100;

    private String digits = "";
    private final Paint paint;
    private final Model model;
    private final ObjectiveSoundPool.SoundEffect shotSound;
    private final ObjectiveSoundPool.SoundEffect explosionSound;

    private boolean dead = false;

    public Cannon(
            Model model,
            float sizePixels,
            ObjectiveSoundPool.SoundEffect shotSound,
            ObjectiveSoundPool.SoundEffect explosionSound) {
        this.model = model;
        this.shotSound = shotSound;
        this.explosionSound = explosionSound;

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(sizePixels);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void stepMs(long deltaMs) {
        // This method intentionally left blank
    }

    @Override
    public void drawOn(Canvas canvas) {
        if (dead) {
            return;
        }

        canvas.drawText("/" + digits + "\\", canvas.getWidth() / 2, canvas.getHeight(), paint);
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    public void addDigit(int digit) {
        digits += digit;
    }

    public String getText() {
        return digits;
    }

    public GameObject createShotFor(FallingMaths target) {
        this.shotSound.play();

        Shot shot = new Shot(digits, paint.getTextSize(), (double) X, (double) Y, target);
        digits = "";
        return shot;
    }

    public GameObject[] createErrorDebris() {
        shotSound.play();

        GameObject[] debris = new GameObject[DEBRIS_COUNT_ON_FAIL];
        for (int i = 0; i < DEBRIS_COUNT_ON_FAIL; i++) {
            debris[i] = new Debris(digits, paint.getTextSize(), (double) X, (double) Y);
        }
        digits = "";
        return debris;
    }

    /** Explode cannon and shoot the given text off in various directions. */
    public void explode(String text) {
        explosionSound.play();

        for (int i = 0; i < DEBRIS_COUNT_ON_EXPLODE; i++) {
            model.add(new Debris(text, paint.getTextSize(), (double) X, (double) Y));
        }

        dead = true;
    }
}
