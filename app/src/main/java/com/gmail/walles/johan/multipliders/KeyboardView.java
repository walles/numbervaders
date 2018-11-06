package com.gmail.walles.johan.multipliders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

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

    private static class Key {
        private final int number;
        private final float x;
        private final float y;

        /**
         * Key height in pixels.
         */
        private float height;

        private Key(int number, float x, float y, float height) {
            this.number = number;
            this.x = x;
            this.y = y;
            this.height = height;
        }

        public float distanceSquaredTo(float x, float y) {
            float dx = x - this.x;
            float dy = y - (this.y - height / 2f);
            return dx * dx + dy * dy;
        }

        public void drawOn(Canvas canvas, Paint paint) {
            canvas.drawText(Integer.toString(number), x, y, paint);
        }
    }
    private List<Key> keys;

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

        setOnTouchListener((v, event) -> handleTouch(event));

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
    }

    /**
     * @see android.view.View.OnTouchListener#onTouch(View, MotionEvent)
     */
    private boolean handleTouch(MotionEvent event) {
        float x;
        float y;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                // Required to get the up events:
                // https://stackoverflow.com/a/16495363/473672
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                x = event.getX(event.getActionIndex());
                y = event.getY(event.getActionIndex());
                break;

            case MotionEvent.ACTION_UP:
                x = event.getX();
                y = event.getY();
                break;

            default:
                // We only handle up events
                return false;
        }

        Key closestKey = keys.get(0);
        float closestDistance = closestKey.distanceSquaredTo(x, y);
        for (Key key: keys) {
            float distance = key.distanceSquaredTo(x, y);
            if (distance < closestDistance) {
                closestKey = key;
                closestDistance = distance;
            }
        }

        Timber.i("Key pressed: %d", closestKey.number);
        return true;
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

        configureKeys(width, height);
    }

    private void configureKeys(int width, int height) {
        float keyWidth = paint.getTextSize() * 2f;

        float x0 = width / 2 - keyWidth;
        float x1 = width / 2;
        float x2 = width / 2 + keyWidth;

        float rowHeight = height / 4f;
        float y0 = rowHeight;
        float y1 = 2 * rowHeight;
        float y2 = 3 * rowHeight;
        float y3 = 4 * rowHeight;

        List<Key> newKeys = new ArrayList<>();
        newKeys.add(new Key(0, x1, y3, rowHeight));
        newKeys.add(new Key(1, x0, y2, rowHeight));
        newKeys.add(new Key(2, x1, y2, rowHeight));
        newKeys.add(new Key(3, x2, y2, rowHeight));
        newKeys.add(new Key(4, x0, y1, rowHeight));
        newKeys.add(new Key(5, x1, y1, rowHeight));
        newKeys.add(new Key(6, x2, y1, rowHeight));
        newKeys.add(new Key(7, x0, y0, rowHeight));
        newKeys.add(new Key(8, x1, y0, rowHeight));
        newKeys.add(new Key(9, x2, y0, rowHeight));

        keys = newKeys;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.BLACK);

        for (Key key: keys) {
            key.drawOn(canvas, paint);
        }
    }
}
