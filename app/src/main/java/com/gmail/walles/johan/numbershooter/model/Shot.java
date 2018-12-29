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

class Shot implements GameObject {
    private static final double MS_ACROSS_SCREEN = 500;
    private static final double PERCENT_PER_MS = 100.0 / MS_ACROSS_SCREEN;
    private final String text;
    private final FallingMaths target;
    private final double dx;
    private final double dy;

    private final double initialSignumX;
    private final double initialSignumY;

    private boolean dead = false;

    private double x;
    private double y;
    private final Paint paint;

    public Shot(String text, float sizePixels, double x, double y, FallingMaths target) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.target = target;

        // Must match the signum calculations in stepMs()
        initialSignumX = Math.signum(x - target.getX());
        initialSignumY = Math.signum(y - target.getY());

        double angle = Math.atan2(target.getY() - y, target.getX() - x);
        this.dx = Math.cos(angle);
        this.dy = Math.sin(angle);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(sizePixels);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void stepMs(long deltaMs) {
        x += PERCENT_PER_MS * deltaMs * dx;
        y += PERCENT_PER_MS * deltaMs * dy;

        // Must match the signum calculations in the constructor
        double signumX = Math.signum(x - target.getX());
        double signumY = Math.signum(y - target.getY());

        if (signumX != initialSignumX || signumY != initialSignumY) {
            target.explode();
            dead = true;
        }
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
        return dead;
    }
}
