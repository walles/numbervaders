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
import android.content.res.Resources;
import com.gmail.walles.johan.numbershooter.model.MathsFactory;
import com.gmail.walles.johan.numbershooter.model.MultiplicationFactory;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

public final class Medals {
    private Medals() {
        // Prevent us from being instantiated
    }

    /** @see #get(Resources, Map) */
    public static List<Medal> get(Resources resources, PlayerStateV2 playerState) {
        Map<GameType, Integer> gameTypeToLevel = new HashMap<>();
        for (GameType gameType : GameType.values()) {
            gameTypeToLevel.put(gameType, playerState.getLevel(gameType));
        }

        return get(resources, gameTypeToLevel);
    }

    /**
     * List medals awarded for the most recent round of a given game type.
     *
     * @see #get(Resources, Map)
     */
    public static List<Medal> getLatest(
            Resources resources, PlayerStateV2 playerState, GameType gameType) {
        if (playerState.getLevel(gameType) == 1) {
            Timber.w("Game type not started, why was this requested?");
            return Collections.emptyList();
        }

        Map<GameType, Integer> gameTypeToLevel = new HashMap<>();
        for (GameType gameTypeIter : GameType.values()) {
            gameTypeToLevel.put(gameTypeIter, playerState.getLevel(gameTypeIter));
        }
        List<Medal> after = get(resources, gameTypeToLevel);

        int level = gameTypeToLevel.get(gameType);
        gameTypeToLevel.put(gameType, level - 1);
        List<Medal> before = get(resources, gameTypeToLevel);

        after.removeAll(before);
        return after;
    }

    private static List<Medal> get(Resources resources, Map<GameType, Integer> gameTypeToLevel) {
        List<Medal> medals = new ArrayList<>();

        medals.addAll(getWaysOfCountingMedals(resources, gameTypeToLevel));
        medals.addAll(getTimesTableMedals(resources, gameTypeToLevel));

        return medals;
    }

    private static Collection<Medal> getTimesTableMedals(
            Resources resources, Map<GameType, Integer> gameTypeToLevel) {
        @SuppressLint("UseSparseArrays")
        Map<Integer, Integer> doneCountsPerTable = new HashMap<>();

        List<MathsFactory.Maths> mathsUpToLevelInclusive =
                MultiplicationFactory.getMathsUpToLevelInclusive(
                        gameTypeToLevel.get(GameType.MULTIPLICATION));
        for (MathsFactory.Maths maths : mathsUpToLevelInclusive) {
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
            medals.add(
                    new Medal(
                            flavor, resources.getString(R.string.n_times_table_done, tableNumber)));
        }

        return medals;
    }

    /** Figure out medals for how many ways of counting the user has tried out. */
    private static Collection<Medal> getWaysOfCountingMedals(
            Resources resources, Map<GameType, Integer> gameTypeToLevel) {
        List<Medal> medals = new LinkedList<>();

        int startedWaysOfCounting = 0;
        for (int level : gameTypeToLevel.values()) {
            if (level > 1) {
                startedWaysOfCounting++;
            }
        }

        if (startedWaysOfCounting >= 1) {
            medals.add(
                    new Medal(
                            Medal.Flavor.BRONZE,
                            resources.getString(R.string.started_first_way_of_counting)));
        }

        if (startedWaysOfCounting >= 2) {
            medals.add(
                    new Medal(
                            Medal.Flavor.BRONZE,
                            resources.getString(R.string.started_second_way_of_counting)));
        }

        if (startedWaysOfCounting >= 3) {
            medals.add(
                    new Medal(
                            Medal.Flavor.SILVER,
                            resources.getString(R.string.started_third_way_of_counting)));
        }

        if (startedWaysOfCounting >= 4) {
            medals.add(
                    new Medal(
                            Medal.Flavor.GOLD,
                            resources.getString(R.string.started_final_way_of_counting)));
        }

        return medals;
    }
}
