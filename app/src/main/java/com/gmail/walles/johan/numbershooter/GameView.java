/*
 * Copyright 2018, Johan Walles <johan.walles@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gmail.walles.johan.numbershooter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.gmail.walles.johan.numbershooter.model.FallingMaths;
import com.gmail.walles.johan.numbershooter.model.FallingMathsFactory;
import com.gmail.walles.johan.numbershooter.model.Model;

import java.util.Locale;

import timber.log.Timber;

public class GameView extends View {
    private static final long LOG_REPORT_EVERY_MS = 5000;
    private @Nullable Model model;

    private static class MovingAverage {
        private static final double INERTIA = 100;

        private double average;
        @Nullable private Double max;
        @Nullable private Double min;

        public void addMs(double value) {
            average = ((average * (INERTIA - 1.0)) + value) / INERTIA;

            if (max == null) {
                max = value;
            }
            if (min == null) {
                min = value;
            }
            if (value > max) {
                max = value;
            }
            if (value < min) {
                min = value;
            }
        }

        public String get() {
            assert max != null;
            assert min != null;

            String stats = String.format(Locale.ENGLISH, "%.1fms-%.1fms-%.1fms", min, average, max);
            min = null;
            max = null;
            return stats;
        }

        public String getHz() {
            assert max != null;
            assert min != null;
            String stats = String.format(Locale.ENGLISH, "%.1fHz-%.1fHz-%.1fHz",
                    1000.0 / max, 1000.0 / average, 1000.0 / min);

            min = null;
            max = null;
            return stats;
        }
    }

    public interface OnGameOverListener {
        /**
         * @param failedMaths Live maths when the player died
         */
        void onPlayerDied(Iterable<FallingMaths> failedMaths);

        void onLevelCleared();
    }
    @Nullable
    private OnGameOverListener onGameOverListener;

    private long lastFrameStart;

    private MovingAverage betweenFramesMillisRunningAverage;
    private MovingAverage updateMillisRunningAverage;
    private MovingAverage drawMillisRunningAverage;
    private MovingAverage invalidateMillisRunningAverage;
    private long lastStatsReportTimestamp;

    private final ObjectiveSoundPool soundPool;
    private final ObjectiveSoundPool.SoundEffect shotSound;
    private final ObjectiveSoundPool.SoundEffect explosionSound;
    private final ObjectiveSoundPool.SoundEffect mathsKilled;
    private final ObjectiveSoundPool.SoundEffect mathsArriving;
    private final ObjectiveSoundPool.SoundEffect wrongAnswer;
    private final ObjectiveSoundPool.SoundEffect levelCleared;

    /**
     * The actual initialization is done in {@link #GameView(Context, AttributeSet, int)}.
     */
    public GameView(Context context) {
        this(context, null);
    }

    /**
     * The actual initialization is done in {@link #GameView(Context, AttributeSet, int)}.
     */
    public GameView(Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * This is where initialization happens.
     */
    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        soundPool = new ObjectiveSoundPool();
        shotSound = soundPool.load(context, R.raw.one_fire_cracker_goes_off, "Cannon shot");
        explosionSound = soundPool.load(context, R.raw.cannon_explosion, "Cannon explosion");
        mathsKilled = soundPool.load(context, R.raw.maths_killed, "Maths killed");
        mathsArriving = soundPool.load(context, R.raw.maths_arriving, "Maths arriving");
        wrongAnswer = soundPool.load(context, R.raw.wrong_answer, "Wrong answer");
        levelCleared = soundPool.load(context, R.raw.level_cleared, "Level cleared");
    }

    public void close() {
        soundPool.close();
    }

    public void restart(GameType gameType, int level) {
        WindowManager wm = (WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE);
        assert wm != null;

        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenHeight = Math.max(size.x, size.y);

        float objectSizesInPixels = screenHeight / 15f;
        model = new Model(FallingMathsFactory.create(gameType, level, objectSizesInPixels, mathsKilled),
                objectSizesInPixels,
                shotSound, explosionSound, mathsArriving, wrongAnswer);

        lastFrameStart = 0;

        betweenFramesMillisRunningAverage = new MovingAverage();
        updateMillisRunningAverage = new MovingAverage();
        drawMillisRunningAverage = new MovingAverage();
        invalidateMillisRunningAverage = new MovingAverage();
        lastStatsReportTimestamp = 0;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (model == null) {
            // Not set up yet, try again
            invalidate();
            return;
        }

        long t0 = System.currentTimeMillis();
        if (lastFrameStart != 0) {
            long dtMs = t0 - lastFrameStart;
            betweenFramesMillisRunningAverage.addMs(dtMs);

            if (dtMs > 200) {
                Timber.w("Super bad timings, %dms since last frame", dtMs);
            }
        }
        lastFrameStart = t0;

        boolean cannonDeadBefore = model.getCannon().isDead();
        boolean modelDoneBefore = model.isDone();
        model.updateTo(System.currentTimeMillis());

        boolean cannonDeadAfter = model.getCannon().isDead();
        boolean modelDoneAfter = model.isDone();
        if (cannonDeadAfter && !cannonDeadBefore && onGameOverListener != null) {
            onGameOverListener.onPlayerDied(model.listFallingMaths());
        }
        if (modelDoneAfter && !modelDoneBefore && onGameOverListener != null) {
            onGameOverListener.onLevelCleared();
            levelCleared.play();
        }

        long t1 = System.currentTimeMillis();
        canvas.drawColor(Color.TRANSPARENT);
        model.drawOn(canvas);

        long t2 = System.currentTimeMillis();
        // Trigger the next frame
        invalidate();

        long t3 = System.currentTimeMillis();
        long updateMillis = t1 - t0;
        long drawMillis = t2 - t1;
        long invalidateMillis = t3 - t2;

        updateMillisRunningAverage.addMs(updateMillis);
        drawMillisRunningAverage.addMs(drawMillis);
        invalidateMillisRunningAverage.addMs(invalidateMillis);
        long now = System.currentTimeMillis();
        if (lastStatsReportTimestamp == 0) {
            lastStatsReportTimestamp = now;
        } else if (now - lastStatsReportTimestamp > LOG_REPORT_EVERY_MS) {
            lastStatsReportTimestamp = now;
            Timber.i("onDraw timings: update=<%s>, draw=<%s>, invalidate=<%s>, framerate=<%s>",
                    updateMillisRunningAverage.get(), drawMillisRunningAverage.get(),
                    invalidateMillisRunningAverage.get(),
                    betweenFramesMillisRunningAverage.getHz());
        }
    }

    public void insertDigit(int digit) {
        if (model == null) {
            return;
        }

        model.insertDigit(digit);
    }

    public void setOnGameOverListener(@NonNull OnGameOverListener onGameOverListener) {
        this.onGameOverListener = onGameOverListener;
    }
}
