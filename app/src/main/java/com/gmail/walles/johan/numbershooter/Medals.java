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

import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;

import java.util.ArrayList;
import java.util.List;

public final class Medals {
    private Medals() {
        // Prevent us from being instantiated
    }

    public static List<Medal> get(PlayerStateV2 playerState) {
        // FIXME: Support "Multiply by Three" type medals

        // FIXME: Support "Started with First Arithmetic Operation" + "Most" + "All"

        return new ArrayList<>();
    }

    /**
     * List medals awarded for the most recent round of a given game type.
     */
    public static List<Medal> getLatest(PlayerStateV2 playerState, GameType gameType) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
