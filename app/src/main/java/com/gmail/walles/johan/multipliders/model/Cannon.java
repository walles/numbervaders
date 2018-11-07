package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

class Cannon implements GameObject {
    private static final int DEBRIS_COUNT_ON_FAIL = 3;

    private String digits = "";
    private final Paint paint;

    public Cannon() {
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);  // FIXME: Adapt to screen size
        paint.setTextAlign(Paint.Align.CENTER);
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
    }

    public String getText() {
        return digits;
    }

    public GameObject createShotFor(FallingMaths target) {
        Shot shot = new Shot(digits, getX(), getY(), target);
        digits = "";
        return shot;
    }

    public GameObject[] createErrorDebris() {
        GameObject[] debris = new GameObject[DEBRIS_COUNT_ON_FAIL];
        for (int i = 0; i < DEBRIS_COUNT_ON_FAIL; i++) {
            debris[i] = new Debris(digits, getX(), getY());
        }
        digits = "";
        return debris;
    }
}
