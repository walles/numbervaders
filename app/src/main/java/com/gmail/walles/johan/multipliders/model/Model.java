package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Model {
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

    /**
     * Don't show more than this number of challenges at once.
     */
    private static final int MAX_CHALLENGES = 4;

    private List<GameObject> stuff = new ArrayList<>();
    private Cannon cannon = new Cannon();

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
        stuff.add(new FallingMaths());
    }

    private boolean shouldAddChallenge() {
        int challengesFound = 0;

        for (GameObject object: stuff) {
            if (!(object instanceof FallingMaths)) {
                continue;
            }

            if (++challengesFound >= MAX_CHALLENGES) {
                return false;
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

        FallingMaths target = findTarget(cannon.getText());
        if (target != null) {
            // The cannon contains the correct answer for one falling maths, shoot that one down
            // FIXME: Move shots creation into Cannon
            stuff.add(new Shot(cannon.getText(), cannon.getX(), cannon.getY(), target));
            cannon.clearDigits();
            return;
        }

        if (isStartOfAnAnswer(cannon.getText())) {
            // The cannon contains the start of a correct answer for some falling maths, just
            // leave the new digit in the cannon
            return;
        }

        // FIXME: This is wrong, fire a slow red ballistic shot to mark this event
        cannon.clearDigits();
    }

    private boolean isStartOfAnAnswer(String prefix) {
        for (GameObject object: stuff) {
            if (!(object instanceof FallingMaths)) {
                continue;
            }

            FallingMaths candidate = (FallingMaths) object;
            String fallingAnswer = Integer.toString(candidate.answer);
            if (fallingAnswer.startsWith(prefix)) {
                return true;
            }
        }

        // Not found
        return false;
    }

    @Nullable
    private FallingMaths findTarget(String answer) {
        for (GameObject object: stuff) {
            if (!(object instanceof FallingMaths)) {
                continue;
            }

            FallingMaths candidate = (FallingMaths) object;
            String fallingAnswer = Integer.toString(candidate.answer);
            if (answer.equals(fallingAnswer)) {
                return candidate;
            }
        }

        // Not found
        return null;
    }
}
