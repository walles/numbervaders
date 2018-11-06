package com.gmail.walles.johan.multipliders.model;

import android.graphics.Canvas;

public interface GameObject {
    /**
     * Update our state by this many milliseconds.
     */
    void stepMs(long deltaMs);

    void drawOn(Canvas canvas);

    /**
     * When an object should be removed, return true here.
     */
    boolean isDead();

    /**
     * Coordinate system is Y=0%-100% where 0% is on top and 100% is on bottom.
     */
    double getY();

    /**
     * X coordinates are as wide as Y coordinates are high but go from left to right with 0% being
     * in the middle of the screen.
     */
    double getX();
}
