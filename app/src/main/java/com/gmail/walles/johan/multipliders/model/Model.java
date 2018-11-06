package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Model implements Shooter {
    private static final long NEVER_UPDATED = 0L;

    /**
     * Accept time deltas at least this big. If we don't limit them, the physics would go nuts after
     * pausing and resuming the app.
     */
    private static final long MAX_STEP_MS = 100L;

    /**
     * Add new objects at most this close to each other.
     */
    private static final int FALLING_OBJECTS_SPACING_PERCENT = 17;

    private List<GameObject> stuff = new ArrayList<>();
    private Cannon cannon = new Cannon(this);

    private long lastUpdatedToMs = NEVER_UPDATED;

    /**
     * Update model to the given timestamp.
     */
    public void updateTo(long timestampMillis) {
        if (lastUpdatedToMs == NEVER_UPDATED) {
            lastUpdatedToMs = timestampMillis;
            return;
        }

        long deltaMs = timestampMillis - lastUpdatedToMs;
        if (deltaMs < 0) {
            // Clock went backwards, keep up
            lastUpdatedToMs = timestampMillis;
            return;
        }

        if (deltaMs > MAX_STEP_MS) {
            deltaMs = MAX_STEP_MS;
        }

        if (shouldAddChallenge()) {
            addMoreChallenges();
        }

        for (GameObject object: stuff) {
            object.stepMs(deltaMs);
        }

        // Stepping can kill some objects
        Iterator<GameObject> iter = stuff.iterator();
        while (iter.hasNext()) {
            GameObject object = iter.next();
            if (object.isDead()) {
                iter.remove();
            }
        }
        cannon.stepMs(deltaMs);

        lastUpdatedToMs = timestampMillis;
    }

    private void addMoreChallenges() {
        stuff.add(new FallingText());
    }

    private boolean shouldAddChallenge() {
        for (GameObject object: stuff) {
            if (!(object instanceof FallingText)) {
                continue;
            }

            if (object.getY() <= FALLING_OBJECTS_SPACING_PERCENT) {
                // Something's in the way
                return false;
            }
        }

        return true;
    }

    /**
     * Render the model onto the given (already cleared) canvas.
     */
    public void drawOn(Canvas canvas) {
        cannon.drawOn(canvas);
        for (GameObject object: stuff) {
            object.drawOn(canvas);
        }
    }

    public void insertDigit(int digit) {
        cannon.addDigit(digit);
    }

    @Override
    public void fireShot(String digits) {
        // FIXME: Aim for an actual target
        stuff.add(new Shot(digits, cannon.getX(), cannon.getY(), 0, 100));
    }
}
