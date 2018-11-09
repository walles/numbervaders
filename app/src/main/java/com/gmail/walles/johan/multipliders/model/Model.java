package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Coordinate system is Y=0%-100% where 0% is on top and 100% is on bottom.
 * <p>
 * X coordinates are as wide as Y coordinates are high but go from left to right with 0% being
 * in the middle of the screen.
 */
public class Model {
    private static final int MATHS_PER_LEVEL = 20;

    private static final long UNSET = 0L;

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
    private List<GameObject> newObjects = new ArrayList<>();
    private Cannon cannon = new Cannon(this);

    private long lastUpdatedToMs = UNSET;

    /**
     * When this is true no more maths will drop down from the sky.
     */
    private boolean mathsStopped = false;

    /**
     * How many maths have we dropped on the player?
     */
    private int droppedMaths;

    /**
     * Update model to the given timestamp.
     */
    public void updateTo(long timestampMillis) {
        if (lastUpdatedToMs == UNSET) {
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
        cannon.stepMs(deltaMs);

        // Stepping can kill some objects
        Iterator<GameObject> iter = stuff.iterator();
        while (iter.hasNext()) {
            GameObject object = iter.next();
            if (object.isDead()) {
                iter.remove();
            }
        }

        // Stepping can create new objects
        stuff.addAll(newObjects);
        newObjects.clear();

        lastUpdatedToMs = timestampMillis;
    }

    private void addMoreChallenges() {
        stuff.add(new FallingMaths(this));
        droppedMaths++;
    }

    private boolean shouldAddChallenge() {
        if (droppedMaths >= MATHS_PER_LEVEL) {
            return false;
        }

        if (mathsStopped) {
            return false;
        }

        int challengesFound = 0;

        for (GameObject object: stuff) {
            if (!(object instanceof FallingMaths)) {
                continue;
            }

            if (++challengesFound >= MAX_CHALLENGES) {
                return false;
            }

            FallingMaths fallingMaths = (FallingMaths)object;
            if (fallingMaths.getY() <= FALLING_OBJECTS_SPACING_PERCENT) {
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
            stuff.add(cannon.createShotFor(target));
            return;
        }

        if (isStartOfAnAnswer(cannon.getText())) {
            // The cannon contains the start of a correct answer for some falling maths, just
            // leave the new digit in the cannon
            return;
        }

        // Wrong answer, clear the cannon
        Collections.addAll(stuff, cannon.createErrorDebris());
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

    public void noMoreMaths() {
        mathsStopped = true;
    }

    public List<FallingMaths> listFallingMaths() {
        List<FallingMaths> fallingMaths = new ArrayList<>();

        for (GameObject object: stuff) {
            if (!(object instanceof FallingMaths)) {
                continue;
            }

            fallingMaths.add((FallingMaths)object);
        }

        return fallingMaths;
    }

    public Cannon getCannon() {
        return cannon;
    }

    /**
     * Add a new object to the simulation.
     */
    public void add(GameObject object) {
        // Without the intermediate newObjects collection we trigger
        // ConcurrentModificationExceptions when adding objects to the list while traversing it to
        // step.
        newObjects.add(object);
    }

    public boolean isDone() {
        if (droppedMaths >= MATHS_PER_LEVEL && listFallingMaths().isEmpty()) {
            return true;
        }

        return false;
    }
}
