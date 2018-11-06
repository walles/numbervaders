package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Shot implements GameObject {
    private static final double MS_ACROSS_SCREEN = 500;
    private static final double PERCENT_PER_MS = 100.0 / MS_ACROSS_SCREEN;
    private final String text;
    private final double targetX;
    private final double targetY;
    private final double dx;
    private final double dy;

    private boolean dead = false;

    private double x;
    private double y;
    private final Paint paint;

    public Shot(String text, double x, double y, double targetX, double targetY) {
        this.text = text;
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;

        double angle = Math.atan2(targetY - y, targetX - x);
        this.dx = Math.sin(angle);
        this.dy = -Math.cos(angle);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);  // FIXME: Let our creator set this
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void stepMs(long deltaMs) {
        x += PERCENT_PER_MS * deltaMs * dx;
        y += PERCENT_PER_MS * deltaMs * dy;

        // FIXME: Do something sensible when hitting our target

        if (y < 0) {
            // Gone above the clouds
            dead = true;
        }
    }

    @Override
    public void drawOn(Canvas canvas) {
        double coordinatesToScreenFactor = canvas.getHeight() / 100.0;
        double xOffset = canvas.getWidth() / 2;
        float screenX = (float)(x * coordinatesToScreenFactor + xOffset);
        float screenY = (float)(y * coordinatesToScreenFactor);

        canvas.drawText(text, screenX, screenY, paint);
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    @Override
    public double getY() {
        return y;
    }
}
