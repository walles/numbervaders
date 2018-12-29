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

public class Medal {
    private final String description;
    public final Flavor flavor;

    public Medal(Flavor flavor, String description) {
        this.description = description;
        this.flavor = flavor;
    }

    public enum Flavor {
        BRONZE(0x99, 0x80, 0x40, 0x00),
        SILVER(0x00, 0xff, 0xff, 0xff),
        GOLD(0x66, 0xff, 0xff, 0x00);

        public final int color;

        Flavor(int a, int r, int g, int b) {
            this.color = Color.argb(a, r, g, b);
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
