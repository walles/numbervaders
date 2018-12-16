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

import com.gmail.walles.johan.numbershooter.model.MathsFactory;
import com.gmail.walles.johan.numbershooter.model.MultiplicationFactory;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public final class Medals {
    private Medals() {
        // Prevent us from being instantiated
    }

    public static List<Medal> get(PlayerStateV2 playerState) {
        List<Medal> medals = new ArrayList<>();

        medals.addAll(getWaysOfCountingMedals(playerState));
        medals.addAll(getTimesTableMedals(playerState));

        return medals;
    }

    private static Collection<Medal> getTimesTableMedals(PlayerStateV2 playerState) {
        @SuppressLint("UseSparseArrays")
        Map<Integer, Integer> doneCountsPerTable = new HashMap<>();

        List<MathsFactory.Maths> mathsUpToLevelInclusive =
                MultiplicationFactory.getMathsUpToLevelInclusive(
                        playerState.getLevel(GameType.MULTIPLICATION));
        for (MathsFactory.Maths maths: mathsUpToLevelInclusive) {
            Integer count = doneCountsPerTable.get(maths.a);
            if (count == null) {
                count = 0;
            }
            doneCountsPerTable.put(maths.a, count + 1);

            if (maths.a == maths.b) {
                // Don't count 5*5 twice
                continue;
            }

            count = doneCountsPerTable.get(maths.b);
            if (count == null) {
                count = 0;
            }
            doneCountsPerTable.put(maths.b, count + 1);
        }

        int maxDoneTable = 0;
        for (int table = 1; table <= 10; table++) {
            // 19 here is:
            // x * [1-10]: There are 10 of these
            // [1-10] * x: There are 10 of these
            // So it's 20, but one of them is x * x and we count that only once.
            if (doneCountsPerTable.get(table) < 19) {
                // Table not done
                continue;
            }

            maxDoneTable = table;
        }

        List<Medal> medals = new LinkedList<>();
        for (int tableNumber = 1; tableNumber <= maxDoneTable; tableNumber++) {
            Medal.Flavor flavor = Medal.Flavor.BRONZE;
            if (tableNumber >= 6) {
                flavor = Medal.Flavor.SILVER;
            }
            if (tableNumber >= 10) {
                flavor = Medal.Flavor.GOLD;
            }
            medals.add(new Medal(flavor, tableNumber + " times table done"));
        }

        return medals;
    }

    /**
     * Figure out medals for how many ways of counting the user has tried out.
     */
    private static Collection<Medal> getWaysOfCountingMedals(PlayerStateV2 playerState) {
        List<Medal> medals = new LinkedList<>();

        int startedWaysOfCounting = 0;
        for (GameType gameType: GameType.values()) {
            if (playerState.getLevel(gameType) > 1) {
                startedWaysOfCounting += 1;
            }
        }

        if (startedWaysOfCounting >= 1) {
            medals.add(new Medal(Medal.Flavor.BRONZE, "Started first way of counting"));
        }

        if (startedWaysOfCounting >= 2) {
            medals.add(new Medal(Medal.Flavor.BRONZE, "Started second way of counting"));
        }

        if (startedWaysOfCounting >= 3) {
            medals.add(new Medal(Medal.Flavor.SILVER, "Started third way of counting"));
        }

        if (startedWaysOfCounting >= 4) {
            medals.add(new Medal(Medal.Flavor.GOLD, "Started final way of counting"));
        }

        return medals;
    }

    /**
     * List medals awarded for the most recent round of a given game type.
     */
    public static List<Medal> getLatest(PlayerStateV2 playerState, GameType gameType) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
