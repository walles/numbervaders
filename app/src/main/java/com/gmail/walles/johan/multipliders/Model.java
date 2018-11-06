package com.gmail.walles.johan.multipliders;

import android.graphics.Canvas;

/**
 * Coordinate system is Y=[0%-100%] where 0% is on top and 100% is on bottom, X coordinates are as
 * wide as Y coordinates are high but go from left to right with 0% being in the middle of the
 * screen.
 */
public class Model {
    private static final long NEVER_UPDATED = 0L;
    private static final long MAX_STEP_MS = 100L;

    private PhysicalObject physicalObject = new PhysicalObject();
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

        physicalObject.stepMs(deltaMs);
        lastUpdatedToMs = timestampMillis;
    }

    /**
     * Render the model onto the given canvas.
     */
    public void drawOn(Canvas canvas) {
        physicalObject.drawOn(canvas);
    }
}
