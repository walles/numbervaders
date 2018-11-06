package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Cannon implements GameObject {
    private String digits = "";
    private final Paint paint;
    private Shooter shooter;

    public Cannon(Shooter shooter) {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);  // FIXME: Adapt to screen size
        paint.setTextAlign(Paint.Align.CENTER);

        this.shooter = shooter;
    }

    @Override
    public void stepMs(long deltaMs) {
        // This method intentionally left blank
    }

    @Override
    public void drawOn(Canvas canvas) {
        canvas.drawText(
                "[" + digits + "]",
                canvas.getWidth() / 2, canvas.getHeight(),
                paint);
    }

    @Override
    public boolean isDead() {
        return false;
    }

    @Override
    public double getX() {
        return 0;
    }

    @Override
    public double getY() {
        return 100;
    }

    public void addDigit(int digit) {
        digits += digit;

        if (digits.length() >= 2) {
            shooter.fireShot(digits);
            digits = "";
        }
    }
}
