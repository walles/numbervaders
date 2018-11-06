package com.gmail.walles.johan.multipliders;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class PhysicalObject {
    /**
     * How long will it take this text to fall to the bottom of the screen?
     */
    private static final double MS_TO_BOTTOM = 5000;
    private static final double PERCENT_PER_MS = 100.0 / MS_TO_BOTTOM;

    private double x = 0;
    private double y = 0;
    private String text = "Johan";
    private final Paint paint;

    public PhysicalObject() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * Update our state by this many milliseconds.
     */
    public void stepMs(long deltaMs) {
        y += PERCENT_PER_MS * deltaMs;

        if (y > 100) {
            y = 0;
        }
    }

    public void drawOn(Canvas canvas) {
        double coordinatesToScreenFactor = canvas.getHeight() / 100.0;
        double xOffset = canvas.getWidth() / 2;
        float screenX = (float)(x * coordinatesToScreenFactor + xOffset);
        float screenY = (float)(y * coordinatesToScreenFactor);

        canvas.drawText(text, screenX, screenY, paint);
    }

    public double getY() {
        return y;
    }
}
