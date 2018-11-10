package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.gmail.walles.johan.multipliders.ObjectiveSoundPool;

import java.util.Random;

public class FallingMaths implements GameObject {
    private static final Random RANDOM = new Random();

    /**
     * How long will it take this question to fall to the bottom of the screen?
     */
    private static final double MS_TO_BOTTOM = 15_000;
    private static final double PERCENT_PER_MS = 100.0 / MS_TO_BOTTOM;

    private final Model model;

    private double x = -30.0 + 60 * RANDOM.nextDouble();
    private double y = 0;
    public final String question;
    private final Paint paint;

    private boolean dead = false;
    private boolean landing = true;

    public final int answer;
    private final ObjectiveSoundPool.SoundEffect mathsKilled;

    public FallingMaths(Model model,
            ObjectiveSoundPool.SoundEffect mathsKilled) {
        this.model = model;
        this.mathsKilled = mathsKilled;

        int a = RANDOM.nextInt(10) + 1;
        int b = RANDOM.nextInt(10) + 1;
        question = a + "⋅" + b;
        answer = a * b;

        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setTextSize(150);  // FIXME: Adapt to screen size
        paint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    public void stepMs(long deltaMs) {
        if (!landing) {
            doNotLandStepMs(deltaMs);
            return;
        }

        if (y >= 100) {
            // We have landed
            return;
        }

        y += PERCENT_PER_MS * deltaMs;
        if (y < 100) {
            return;
        }

        // Touchdown!
        model.noMoreMaths();

        // Tell our math friends to fly away or start hovering ominously or something
        for (FallingMaths friend: model.listFallingMaths()) {
            if (friend == this) {
                // We shouldn't stop ourselves
                continue;
            }

            friend.stopLanding();
        }

        // Explode the cannon with our answer as the text
        model.getCannon().explode(Integer.toString(answer));
    }

    /**
     * Update our state while not landing.
     */
    private void doNotLandStepMs(long deltaMs) {
        // FIXME: Do some spectacular dance here?
        y -= (PERCENT_PER_MS / 2.0) * deltaMs;
        if (y < 0) {
            dead = true;
        }
    }

    private void stopLanding() {
        landing = false;
    }

    @Override
    public void drawOn(Canvas canvas) {
        double coordinatesToScreenFactor = canvas.getHeight() / 100.0;
        double xOffset = canvas.getWidth() / 2;
        float screenX = (float)(x * coordinatesToScreenFactor + xOffset);
        float screenY = (float)(y * coordinatesToScreenFactor);

        canvas.drawText(question, screenX, screenY, paint);
    }

    @Override
    public boolean isDead() {
        return dead;
    }

    public double getY() {
        return y;
    }

    public double getX() { return x; }

    public void explode() {
        mathsKilled.play();
        dead = true;
    }
}
