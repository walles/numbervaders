package com.gmail.walles.johan.multipliders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.gmail.walles.johan.multipliders.model.Model;

import timber.log.Timber;

public class GameView extends View {
    private static final long LOG_REPORT_EVERY_MS = 5000;
    private final Model model = new Model();

    private static class MovingAverage {
        private static final double INERTIA = 10;

        private double current;

        public void add(double value) {
            current = ((current * (INERTIA - 1.0)) + value) / INERTIA;
        }

        public double get() {
            return current;
        }
    }

    private long lastFrameStart;

    private final MovingAverage betweenFramesMillisRunningAverage = new MovingAverage();
    private final MovingAverage updateMillisRunningAverage = new MovingAverage();
    private final MovingAverage drawMillisRunningAverage = new MovingAverage();
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
            betweenFramesMillisRunningAverage.add(t0 - lastFrameStart);
        }
        lastFrameStart = t0;

        model.updateTo(System.currentTimeMillis());

        long t1 = System.currentTimeMillis();
        canvas.drawColor(Color.BLACK);
        model.drawOn(canvas);

        long t2 = System.currentTimeMillis();
        long updateMillis = t1 - t0;
        long drawMillis = t2 - t1;

        updateMillisRunningAverage.add(updateMillis);
        drawMillisRunningAverage.add(drawMillis);
        long now = System.currentTimeMillis();
        if (lastStatsReportTimestamp == 0) {
            lastStatsReportTimestamp = now;
        } else if (now - lastStatsReportTimestamp > LOG_REPORT_EVERY_MS) {
            lastStatsReportTimestamp = now;
            Timber.i("onDraw timings: update=%.1fms, draw=%.1fms, framerate=%.0fHz",
                    updateMillisRunningAverage.get(), drawMillisRunningAverage.get(),
                    1000 / betweenFramesMillisRunningAverage.get());
        }

        // Trigger the next frame
        invalidate();
    }

    public void insertDigit(int digit) {
        model.insertDigit(digit);
    }
}
