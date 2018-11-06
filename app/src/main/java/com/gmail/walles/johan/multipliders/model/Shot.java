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

    private final double initialSignumX;
    private final double initialSignumY;

    private boolean dead = false;

    private double x;
    private double y;
    private final Paint paint;

    public Shot(String text, double x, double y, double targetX, double targetY) {
        if (targetY == y) {
            throw new IllegalArgumentException("Y and targetY must not be equal");
        }

        this.text = text;
        this.x = x;
        this.y = y;
        this.targetX = targetX;
        this.targetY = targetY;

        // Must match the signum calculations in stepMs()
        initialSignumX = Math.signum(x - targetX);
        initialSignumY = Math.signum(y - targetY);

        double angle = Math.atan2(targetY - y, targetX - x);
        this.dx = Math.cos(angle);
        this.dy = Math.sin(angle);

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);  // FIXME: Let our creator set this
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void stepMs(long deltaMs) {
        x += PERCENT_PER_MS * deltaMs * dx;
        y += PERCENT_PER_MS * deltaMs * dy;

        // Must match the signum calculations in the constructor
        double signumX = Math.signum(x - targetX);
        double signumY = Math.signum(y - targetY);

        if (signumX != initialSignumX || signumY != initialSignumY) {
            // FIXME: Found our target, do something more interesting than just disappearing
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
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }
}
