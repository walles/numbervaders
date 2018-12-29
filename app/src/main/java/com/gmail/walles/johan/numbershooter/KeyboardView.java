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

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

public class KeyboardView extends View {
    /** This is how high we want the keyboard. */
    private static final double HEIGHT_MILLIMETERS = 40;

    /** We never give the keyboard more height than this. */
    private static final double MAX_HEIGHT_PERCENT = 30;

    private final Paint paint;
    private KeypressListener keypressListener;
    private final ObjectiveSoundPool soundPool;
    private final ObjectiveSoundPool.SoundEffect keyUp;
    private final ObjectiveSoundPool.SoundEffect keyDown;
    private int backgroundColor;

    public interface KeypressListener {
        void handleDigit(int digit);
    }

    private class Key {
        private final int digit;
        private final float xCenter;
        private final float yBase;

        private final float yCenter;

        private Key(int digit, float xCenter, float yBase) {
            this.digit = digit;
            this.xCenter = xCenter;
            this.yBase = yBase;

            Rect textBounds = new Rect();
            paint.getTextBounds(Integer.toString(digit), 0, 1, textBounds);

            yCenter = yBase - textBounds.height() / 2f;
        }

        private float distanceSquaredTo(float x, float y) {
            float dx = x - xCenter;
            float dy = y - yCenter;
            return dx * dx + dy * dy;
        }

        private void drawOn(Canvas canvas, Paint paint) {
            canvas.drawText(Integer.toString(digit), xCenter, yBase, paint);

            // NOTE: Enable these for debugging key hit areas
            // canvas.drawCircle(xCenter, yCenter, 20, paint);
            // canvas.drawRect(xCenter - 50, yBase - 5, xCenter + 50, yBase + 5, paint);
        }
    }

    private List<Key> keys;

    /** The actual initialization is done in {@link #KeyboardView(Context, AttributeSet, int)}. */
    public KeyboardView(Context context) {
        this(context, null);
    }

    /** The actual initialization is done in {@link #KeyboardView(Context, AttributeSet, int)}. */
    public KeyboardView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /** This is where all initialization is done. */
    @SuppressLint("ClickableViewAccessibility")
    public KeyboardView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        soundPool = new ObjectiveSoundPool();
        keyDown = soundPool.load(context, R.raw.keydown, "Key down").setVolume(0.3);
        keyUp = soundPool.load(context, R.raw.keyup, "Key up").setVolume(0.6);

        setOnTouchListener((v, event) -> handleTouch(event));

        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);

        backgroundColor =
                ResourcesCompat.getColor(getResources(), R.color.keyboard_background, null);

        setFocusableInTouchMode(true);
    }

    public void close() {
        soundPool.close();
    }

    /** @see android.view.View.OnTouchListener#onTouch(View, MotionEvent) */
    private boolean handleTouch(MotionEvent event) {
        float x;
        float y;

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                keyDown.play();
                // Required to get the up events:
                // https://stackoverflow.com/a/16495363/473672
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                keyUp.play();
                x = event.getX(event.getActionIndex());
                y = event.getY(event.getActionIndex());
                break;

            case MotionEvent.ACTION_UP:
                keyUp.play();
                x = event.getX();
                y = event.getY();
                break;

            default:
                // We only handle up events
                return false;
        }

        Key closestKey = keys.get(0);
        float closestDistance = closestKey.distanceSquaredTo(x, y);
        for (Key key : keys) {
            float distance = key.distanceSquaredTo(x, y);
            if (distance < closestDistance) {
                closestKey = key;
                closestDistance = distance;
            }
        }

        if (keypressListener != null) {
            keypressListener.handleDigit(closestKey.digit);
        }
        return true;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode < KeyEvent.KEYCODE_0) {
            return super.onKeyUp(keyCode, event);
        }
        if (keyCode > KeyEvent.KEYCODE_9) {
            return super.onKeyUp(keyCode, event);
        }

        keypressListener.handleDigit(keyCode - KeyEvent.KEYCODE_0);
        return true;
    }

    public void setOnKeypress(KeypressListener keypressListener) {
        this.keypressListener = keypressListener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);

        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        double millimetersToPixelsFactor = displayMetrics.ydpi / 25.4;
        int height = (int) (HEIGHT_MILLIMETERS * millimetersToPixelsFactor);

        // Limit height to a percentage of the allowed height
        int maxHeight =
                (int) ((MAX_HEIGHT_PERCENT / 100.0) * MeasureSpec.getSize(heightMeasureSpec));
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
        //noinspection UnnecessaryLocalVariable
        float y0 = 1 * rowHeight;
        float y1 = 2 * rowHeight;
        float y2 = 3 * rowHeight;
        float y3 = 4 * rowHeight;

        List<Key> newKeys = new ArrayList<>();
        newKeys.add(new Key(0, x1, y3));
        newKeys.add(new Key(1, x0, y2));
        newKeys.add(new Key(2, x1, y2));
        newKeys.add(new Key(3, x2, y2));
        newKeys.add(new Key(4, x0, y1));
        newKeys.add(new Key(5, x1, y1));
        newKeys.add(new Key(6, x2, y1));
        newKeys.add(new Key(7, x0, y0));
        newKeys.add(new Key(8, x1, y0));
        newKeys.add(new Key(9, x2, y0));

        keys = newKeys;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(backgroundColor);

        for (Key key : keys) {
            key.drawOn(canvas, paint);
        }
    }
}
