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

import android.content.res.Resources;

public enum GameType {
    MULTIPLICATION("×", R.string.multiplication, true, 10),
    ADDITION("+", R.string.addition, true, 15),
    DIVISION("÷", R.string.division, false, 10),
    SUBTRACTION("-", R.string.subtraction, false, 15);

    public final String prettyOperator;
    private final int nameResourceId;
    public final boolean isCommutative;
    public final int topNumber;

    GameType(String prettyOperator, int nameResourceId, boolean isCommutative, int topNumber) {
        this.prettyOperator = prettyOperator;
        this.nameResourceId = nameResourceId;
        this.isCommutative = isCommutative;
        this.topNumber = topNumber;
    }

    public String getLocalizedName(Resources resources) {
        return resources.getString(nameResourceId);
    }
}
