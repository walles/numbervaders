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
import com.gmail.walles.johan.numbershooter.model.MathsFactory;
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
        Map<GameType, Integer> gameTypeToNextLevel = new HashMap<>();
        for (GameType gameType : GameType.values()) {
            gameTypeToNextLevel.put(gameType, playerState.getNextLevel(gameType));
        }

        return get(resources, gameTypeToNextLevel);
    }

    /**
     * List medals awarded for the most recent round of a given game type.
     *
     * @see #get(Resources, Map)
     */
    public static List<Medal> getLatest(
            Resources resources, PlayerStateV2 playerState, GameType gameType) {
        if (playerState.getNextLevel(gameType) == 1) {
            Timber.w("Game type not started, why was this requested?");
            return Collections.emptyList();
        }

        Map<GameType, Integer> gameTypeToNextLevel = new HashMap<>();
        for (GameType gameTypeIter : GameType.values()) {
            gameTypeToNextLevel.put(gameTypeIter, playerState.getNextLevel(gameTypeIter));
        }
        List<Medal> after = get(resources, gameTypeToNextLevel);

        int nextLevel = gameTypeToNextLevel.get(gameType);
        gameTypeToNextLevel.put(gameType, nextLevel - 1);
        List<Medal> before = get(resources, gameTypeToNextLevel);

        after.removeAll(before);
        return after;
    }

    private static List<Medal> get(
            Resources resources, Map<GameType, Integer> gameTypeToNextLevel) {
        List<Medal> medals = new ArrayList<>();

        medals.addAll(getWaysOfCountingMedals(resources, gameTypeToNextLevel));

        medals.addAll(getPercentCompleteMedals(resources, gameTypeToNextLevel));

        medals.addAll(getCommutativeMedals(resources, gameTypeToNextLevel));

        medals.addAll(getNonCommutativeMedals(resources, gameTypeToNextLevel));

        return medals;
    }

    private static Collection<Medal> getPercentCompleteMedals(
            Resources resources, Map<GameType, Integer> gameTypeToNextLevel) {
        List<Medal> medals = new LinkedList<>();

        for (GameType gameType : GameType.values()) {
            int topLevel = MathsFactory.getTopLevel(gameType);
            int nextLevel = gameTypeToNextLevel.get(gameType);
            int highestCompletedLevel = nextLevel - 1;
            int percentDone = (int) Math.floor(100 * highestCompletedLevel / (double) topLevel);

            String operationName = gameType.getLocalizedName(resources);
            if (percentDone >= 25) {
                String progress = resources.getString(R.string.one_quarter_done);
                medals.add(
                        new Medal(
                                Medal.Flavor.BRONZE,
                                resources.getString(
                                        R.string.operation_colon_partly_done,
                                        operationName,
                                        progress)));
            }
            if (percentDone >= 50) {
                String progress = resources.getString(R.string.half_done);
                medals.add(
                        new Medal(
                                Medal.Flavor.BRONZE,
                                resources.getString(
                                        R.string.operation_colon_partly_done,
                                        operationName,
                                        progress)));
            }
            if (percentDone >= 75) {
                String progress = resources.getString(R.string.three_quarters_done);
                medals.add(
                        new Medal(
                                Medal.Flavor.SILVER,
                                resources.getString(
                                        R.string.operation_colon_partly_done,
                                        operationName,
                                        progress)));
            }
            if (percentDone >= 100) {
                String progress = resources.getString(R.string.all_done);
                medals.add(
                        new Medal(
                                Medal.Flavor.GOLD,
                                resources.getString(
                                        R.string.operation_colon_partly_done,
                                        operationName,
                                        progress)));
            }
        }

        return medals;
    }

    private static Collection<Medal> getCommutativeMedals(
            Resources resources, Map<GameType, Integer> gameTypeToNextLevel) {

        List<Medal> medals = new LinkedList<>();

        for (GameType gameType : GameType.values()) {
            if (!gameType.isCommutative) {
                continue;
            }

            Map<Integer, Integer> doneCountsPerNumber = new HashMap<>();

            List<MathsFactory.Maths> completedMaths =
                    MathsFactory.create(gameType)
                            .getMathsUpToLevelInclusive(gameTypeToNextLevel.get(gameType) - 1);
            for (MathsFactory.Maths maths : completedMaths) {
                Integer count = doneCountsPerNumber.get(maths.a);
                if (count == null) {
                    count = 0;
                }
                doneCountsPerNumber.put(maths.a, count + 1);

                if (maths.a == maths.b) {
                    // Don't count 5*5 twice
                    continue;
                }

                count = doneCountsPerNumber.get(maths.b);
                if (count == null) {
                    count = 0;
                }
                doneCountsPerNumber.put(maths.b, count + 1);
            }

            int maxDoneNumber = 0;
            for (int number = 1; number <= gameType.topNumber; number++) {
                Integer doneCount = doneCountsPerNumber.get(number);
                if (doneCount == null) {
                    // Table not started
                    continue;
                }

                // 19 here is:
                // x * [1-10]: There are 10 of these
                // [1-10] * x: There are 10 of these
                // So it's 20, but one of them is x * x and we count that only once.
                if (doneCount < 2 * gameType.topNumber - 1) {
                    // Number not done
                    continue;
                }

                maxDoneNumber = number;
            }

            for (int number = 1; number <= maxDoneNumber; number++) {
                Medal.Flavor flavor = Medal.Flavor.BRONZE;
                if (number >= (gameType.topNumber * 6) / 10) {
                    flavor = Medal.Flavor.SILVER;
                }
                if (number >= gameType.topNumber) {
                    flavor = Medal.Flavor.GOLD;
                }
                medals.add(
                        new Medal(
                                flavor,
                                resources.getString(
                                        R.string.way_of_counting_colon_sign_number_done,
                                        gameType.getLocalizedName(resources),
                                        gameType.prettyOperator,
                                        number)));
            }
        }

        return medals;
    }

    private static Collection<Medal> getNonCommutativeMedals(
            Resources resources, Map<GameType, Integer> gameTypeToNextLevel) {

        List<Medal> medals = new LinkedList<>();

        for (GameType gameType : GameType.values()) {
            if (gameType.isCommutative) {
                continue;
            }

            Map<Integer, Integer> doneCountsPerNumber = new HashMap<>();

            List<MathsFactory.Maths> completedMaths =
                    MathsFactory.create(gameType)
                            .getMathsUpToLevelInclusive(gameTypeToNextLevel.get(gameType) - 1);
            for (MathsFactory.Maths maths : completedMaths) {
                Integer count = doneCountsPerNumber.get(maths.b);
                if (count == null) {
                    count = 0;
                }
                doneCountsPerNumber.put(maths.b, count + 1);
            }

            int maxDoneNumber = 0;
            for (int number = 1; number <= gameType.topNumber; number++) {
                Integer doneCount = doneCountsPerNumber.get(number);
                if (doneCount == null) {
                    // Number not started
                    continue;
                }

                if (doneCount < gameType.topNumber) {
                    // Number not done
                    continue;
                }

                maxDoneNumber = number;
            }

            for (int number = 1; number <= maxDoneNumber; number++) {
                Medal.Flavor flavor = Medal.Flavor.BRONZE;
                if (number >= (gameType.topNumber * 6) / 10) {
                    flavor = Medal.Flavor.SILVER;
                }
                if (number >= gameType.topNumber) {
                    flavor = Medal.Flavor.GOLD;
                }
                medals.add(
                        new Medal(
                                flavor,
                                resources.getString(
                                        R.string.way_of_counting_colon_sign_number_done,
                                        gameType.getLocalizedName(resources),
                                        gameType.prettyOperator,
                                        number)));
            }
        }

        return medals;
    }

    /** Figure out medals for how many ways of counting the user has tried out. */
    private static Collection<Medal> getWaysOfCountingMedals(
            Resources resources, Map<GameType, Integer> gameTypeToNextLevel) {
        List<Medal> medals = new LinkedList<>();

        int startedWaysOfCounting = 0;
        for (int nextLevel : gameTypeToNextLevel.values()) {
            if (nextLevel > 1) {
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
