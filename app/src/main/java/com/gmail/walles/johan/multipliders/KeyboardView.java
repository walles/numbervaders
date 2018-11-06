package com.gmail.walles.johan.multipliders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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

    private final Paint paint;

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

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
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
        float rowHeight = height / 4f;
        paint.setTextSize(rowHeight * 0.9f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        float keyWidth = paint.getTextSize() * 2f;

        float x0 = getWidth() / 2 - keyWidth;
        float x1 = getWidth() / 2;
        float x2 = getWidth() / 2 + keyWidth;

        float y0 = getHeight() / 4f;
        float y1 = 2 * getHeight() / 4f;
        float y2 = 3 * getHeight() / 4f;
        float y3 = 4 * getHeight() / 4f;

        canvas.drawText("0", x1, y3, paint);
        canvas.drawText("1", x0, y2, paint);
        canvas.drawText("2", x1, y2, paint);
        canvas.drawText("3", x2, y2, paint);
        canvas.drawText("4", x0, y1, paint);
        canvas.drawText("5", x1, y1, paint);
        canvas.drawText("6", x2, y1, paint);
        canvas.drawText("7", x0, y0, paint);
        canvas.drawText("8", x1, y0, paint);
        canvas.drawText("9", x2, y0, paint);
    }
}
