package com.gmail.walles.johan.multipliders;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class PhysicalObject {
    private double x = 0;
    private double y = 50;
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

    }

    public void drawOn(Canvas canvas) {
        double coordinatesToScreenFactor = canvas.getHeight() / 100.0;
        double xOffset = canvas.getWidth() / 2;
        float screenX = (float)(x * coordinatesToScreenFactor + xOffset);
        float screenY = (float)(y * coordinatesToScreenFactor);

        // FIXME: Center the text vertically?
        canvas.drawText(text, screenX, screenY, paint);
    }
}
