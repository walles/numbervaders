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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Medal extends Drawable {
    private final String description;
    private final Flavor flavor;
    private final Paint paint;

    public Medal(Flavor flavor, String description) {
        this.description = description;
        this.flavor = flavor;

        paint = new Paint();
        switch (flavor) {
            case GOLD:
                paint.setColor(Color.YELLOW);
                break;

            case SILVER:
                paint.setColor(Color.LTGRAY);
                break;

            case BRONZE:
                paint.setColor(0xff400000);
                break;

            default:
                throw new UnsupportedOperationException("Unsupported flavor: " + flavor);
        }
    }

    public enum Flavor {
        BRONZE,
        SILVER,
        GOLD
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Medal)) {
            return false;
        }

        Medal that = (Medal)obj;
        if (this.flavor != that.flavor) {
            return false;
        }

        return this.description.equals(that.description);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();
        float radius = Math.min(width, height) / 2;

        canvas.drawCircle(width/2, height/2, radius, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        // This method is required
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        // This method is required
    }

    @Override
    public int getOpacity() {
        // Transparent = at least one bit of alpha
        return PixelFormat.TRANSPARENT;
    }
}
