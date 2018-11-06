package com.gmail.walles.johan.multipliders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class KeyboardView extends View {
    /**
     * The actual initialization is done in {@link #KeyboardView(Context, AttributeSet, int)}.
     */
    public KeyboardView(Context context) {
        this(context, null);
    }

    /**
     * The actual initialization is done in {@link #KeyboardView(Context, AttributeSet, int)}.
     */
    public KeyboardView(Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = 200;
        setMeasuredDimension(width, height);
    }
}
