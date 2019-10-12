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
import java.util.Random;

class Debris implements GameObject {
    private static final Random RANDOM = new Random();
    private static final double GRAVITY_PERCENT_PER_MS2 = 0.0002;
    private static final double MS_ACROSS_SCREEN = 1000;
    private static final double PERCENT_PER_MS = 100.0 / MS_ACROSS_SCREEN;

    private double x;
    private double y;
    private final double initialY;
    private double dx;
    private double dy;

    private String text;

    private final Paint paint;

    public Debris(String text, float sizePixels, double x, double y) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.initialY = y;

        // 45 degrees in each direction
        final double from = -Math.PI / 4.0;
        final double to = Math.PI / 4.0;
        double angle = RANDOM.nextDouble() * (to - from) + from;
        this.dx = Math.sin(angle) * PERCENT_PER_MS;
        this.dy = -Math.cos(angle) * PERCENT_PER_MS;

        if (dy >= 0) {
            throw new AssertionError(
                    String.format(
                            "dy should be upwards = higher percentages to lower = negative in our coordinate system, was %f for angle %f",
                            dy, angle));
        }

        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setTextSize(sizePixels);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void stepMs(long deltaMs) {
        dy += GRAVITY_PERCENT_PER_MS2 * deltaMs;
        y += dy * deltaMs;
        x += dx * deltaMs;
    }

    @Override
    public void drawOn(Canvas canvas) {
        double coordinatesToScreenFactor = canvas.getHeight() / 100.0;
        double xOffset = canvas.getWidth() / 2;
        float screenX = (float) (x * coordinatesToScreenFactor + xOffset);
        float screenY = (float) (y * coordinatesToScreenFactor);

        canvas.drawText(text, screenX, screenY, paint);
    }

    @Override
    public boolean isDead() {
        return y > initialY;
    }
}
