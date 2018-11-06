package com.gmail.walles.johan.multipliders;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

public class KeyboardView extends View {
    /**
     * This is how high we want the keyboard.
     */
    private static final double HEIGHT_MILLIMETERS = 40;

    /**
     * We never give the keyboard more height than this.
     */
    private static final double MAX_HEIGHT_PERCENT = 30;

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

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        double millimetersToPixelsFactor = displayMetrics.ydpi / 25.4;
        int height = (int)(HEIGHT_MILLIMETERS * millimetersToPixelsFactor);

        // Limit height to a percentage of the allowed height
        int maxHeight = (int)((MAX_HEIGHT_PERCENT / 100.0) * MeasureSpec.getSize(heightMeasureSpec));
        if (height > maxHeight) {
            height = maxHeight;
        }

        setMeasuredDimension(width, height);
    }
}
