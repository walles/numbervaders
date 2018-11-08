package com.gmail.walles.johan.multipliders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gmail.walles.johan.multipliders.model.FallingMaths;
import com.gmail.walles.johan.multipliders.model.Model;

import java.util.Locale;

import timber.log.Timber;

public class GameView extends View {
    private static final long LOG_REPORT_EVERY_MS = 5000;
    private final Model model = new Model();

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
    }
    @Nullable
    private OnGameOverListener onGameOverListener;

    private long lastFrameStart;

    private final MovingAverage betweenFramesMillisRunningAverage = new MovingAverage();
    private final MovingAverage updateMillisRunningAverage = new MovingAverage();
    private final MovingAverage drawMillisRunningAverage = new MovingAverage();
    private final MovingAverage invalidateMillisRunningAverage = new MovingAverage();
    private long lastStatsReportTimestamp;

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

    public GameView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
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
        model.updateTo(System.currentTimeMillis());
        boolean cannonDeadAfter = model.getCannon().isDead();
        if (cannonDeadAfter && !cannonDeadBefore && onGameOverListener != null) {
            onGameOverListener.onPlayerDied(model.listFallingMaths());
        }

        long t1 = System.currentTimeMillis();
        canvas.drawColor(Color.BLACK);
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
        model.insertDigit(digit);
    }

    public void setOnGameOverListener(@NonNull OnGameOverListener onGameOverListener) {
        this.onGameOverListener = onGameOverListener;
    }
}
