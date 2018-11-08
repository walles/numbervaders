package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Cannon implements GameObject {
    private static final int DEBRIS_COUNT_ON_FAIL = 3;
    private static final int DEBRIS_COUNT_ON_EXPLODE = 6;
    private static final int X = 0;
    private static final int Y = 100;

    private String digits = "";
    private final Paint paint;
    private final Model model;

    private boolean dead = false;

    public Cannon(Model model) {
        this.model = model;

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
        if (dead) {
            return;
        }

        canvas.drawText(
                "[" + digits + "]",
                canvas.getWidth() / 2, canvas.getHeight(),
                paint);
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
        Shot shot = new Shot(digits, (double) X, (double) Y, target);
        digits = "";
        return shot;
    }

    public GameObject[] createErrorDebris() {
        GameObject[] debris = new GameObject[DEBRIS_COUNT_ON_FAIL];
        for (int i = 0; i < DEBRIS_COUNT_ON_FAIL; i++) {
            debris[i] = new Debris(digits, (double) X, (double) Y);
        }
        digits = "";
        return debris;
    }

    /**
     * Explode cannon and shoot the given text off in various directions.
     */
    public void explode(String text) {
        for (int i = 0; i < DEBRIS_COUNT_ON_EXPLODE; i++) {
            model.add(new Debris(text, (double) X, (double) Y));
        }

        dead = true;
    }
}
