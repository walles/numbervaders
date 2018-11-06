package com.gmail.walles.johan.multipliders;

import android.content.Context;
import android.graphics.Canvas;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

public class GameView extends View {
    private static final Random random = new Random();
    private final Model model = new Model();

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
        // FIXME: Clear the canvas with a black background
        canvas.drawColor(random.nextInt());

        model.updateTo(System.currentTimeMillis());

        model.drawOn(canvas);

        // Trigger the next frame
        invalidate();
    }
}
