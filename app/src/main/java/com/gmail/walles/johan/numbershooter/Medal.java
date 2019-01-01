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

import android.graphics.Color;
import org.jetbrains.annotations.NonNls;

public class Medal {
    private final String description;
    public final Flavor flavor;

    @Override
    @NonNls
    public String toString() {
        return flavor + ": " + description;
    }

    public Medal(Flavor flavor, String description) {
        if (description == null) {
            throw new NullPointerException("Description must not be null");
        }

        this.description = description;
        this.flavor = flavor;
    }

    public enum Flavor {
        BRONZE(0x66, 0xcd, 0x7f, 0x32),
        SILVER(0x00, 0xff, 0xff, 0xff),
        GOLD(0x33, 0xff, 0xd7, 0x00);

        private final int a;
        private final int r;
        private final int g;
        private final int b;

        Flavor(int a, int r, int g, int b) {
            this.a = a;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public int getColor() {
            return Color.argb(a, r, g, b);
        }
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Medal)) {
            return false;
        }

        Medal that = (Medal) obj;
        if (this.flavor != that.flavor) {
            return false;
        }

        return this.description.equals(that.description);
    }
}
