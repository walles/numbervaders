package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.Random;

public class FallingMaths implements GameObject {
    private static final Random RANDOM = new Random();

    /**
     * How long will it take this text to fall to the bottom of the screen?
     */
    private static final double MS_TO_BOTTOM = 15_000;
    private static final double PERCENT_PER_MS = 100.0 / MS_TO_BOTTOM;

    private double x = -30.0 + 60 * RANDOM.nextDouble();
    private double y = 0;
    private final String text;
    private final Paint paint;
    private boolean dead = false;

    public final int answer;

    public FallingMaths() {
        int a = RANDOM.nextInt(10) + 1;
        int b = RANDOM.nextInt(10) + 1;
        text = a + "⋅" + b;
        answer = a * b;

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);  // FIXME: Adapt to screen size
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void stepMs(long deltaMs) {
        if (y >= 100) {
            // We have landed
            return;
        }

        y += PERCENT_PER_MS * deltaMs;
        if (y < 100) {
            return;
        }

        // Touchdown!

        // FIXME: Tell model to stop adding new maths

        // FIXME: Tell our math friends to fly away or start hovering ominously or something

        // FIXME: Explode the cannon with our answer as the text
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

    @Override
    public double getX() { return x; }

    public void explode() {
        // FIXME: Do something more spectacular
        dead = true;
    }
}
