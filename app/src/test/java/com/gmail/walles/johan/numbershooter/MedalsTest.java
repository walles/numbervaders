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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.*;

import android.content.res.Resources;
import androidx.annotation.NonNull;
import com.gmail.walles.johan.numbershooter.model.MathsFactory;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NonNls;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class MedalsTest {
    private static class TestableResources extends Resources {
        public TestableResources() {
            super(null, null, null);
        }

        @NonNull
        @Override
        public String getString(int id) throws NotFoundException {
            return Integer.toString(id);
        }

        @NonNls
        @NonNull
        @Override
        public String getString(int id, Object... formatArgs) throws NotFoundException {
            return Integer.toString(id) + ": " + Arrays.toString(formatArgs);
        }
    }

    /** Validate that get() and getLatest() don't contradict each other. */
    @Test
    public void testGetVsGetLatest() {
        Resources resources = new TestableResources();

        for (GameType gameType : GameType.values()) {
            for (int level = 1; level <= 45; level++) {
                PlayerStateV2 playerStateNow = Mockito.mock(PlayerStateV2.class);
                Mockito.when(playerStateNow.getNextLevel(gameType)).thenReturn(level);
                Collection<Medal> after = Medals.get(resources, playerStateNow);

                PlayerStateV2 playerStateBefore = Mockito.mock(PlayerStateV2.class);
                Mockito.when(playerStateBefore.getNextLevel(gameType)).thenReturn(level - 1);
                Collection<Medal> before = Medals.get(resources, playerStateBefore);

                Collection<Medal> earnedAccordingToMedalsClass =
                        Medals.getLatest(resources, playerStateNow, gameType);

                Collection<Medal> actuallyGained = new LinkedList<>(after);
                actuallyGained.removeAll(before);

                Assert.assertThat(earnedAccordingToMedalsClass, is(actuallyGained));
            }
        }
    }

    @Test
    public void testOneTimesTableMedal() {
        // We've just done levels 1 to 4, covering 1*1 - 1*10 / 10*1
        final int LOWEST_NON_COMPLETED_LEVEL = 5;

        Resources resources = new TestableResources();

        PlayerStateV2 playerState = Mockito.mock(PlayerStateV2.class);
        Mockito.when(playerState.getNextLevel(GameType.MULTIPLICATION))
                .thenReturn(LOWEST_NON_COMPLETED_LEVEL);

        Medal timesOneTableMedal =
                new Medal(
                        Medal.Flavor.BRONZE,
                        R.string.way_of_counting_colon_sign_number_done + ": [2131623980, ×, 1]");

        Collection<Medal> medalsEarned =
                Medals.getLatest(resources, playerState, GameType.MULTIPLICATION);

        Assert.assertThat(medalsEarned, contains(timesOneTableMedal));
    }

    private Map<Integer, List<Medal>> getMedalsPerLevel(GameType gameType) {
        Map<Integer, List<Medal>> returnMe = new HashMap<>();

        for (int level = 1; level <= MathsFactory.getTopLevel(gameType); level++) {
            PlayerStateV2 playerState = Mockito.mock(PlayerStateV2.class);
            Mockito.when(playerState.getNextLevel(gameType)).thenReturn(level + 1);

            List<Medal> medals = Medals.getLatest(new TestableResources(), playerState, gameType);
            if (medals.isEmpty()) {
                continue;
            }

            returnMe.put(level, medals);
        }

        return returnMe;
    }

    private String toString(Map<Integer, List<Medal>> medalsPerLevel) {
        int highestLevel = 0;
        for (int level : medalsPerLevel.keySet()) {
            highestLevel = Math.max(level, highestLevel);
        }

        StringBuilder builder = new StringBuilder();
        for (int level = 1; level <= highestLevel; level++) {
            if (builder.length() > 0) {
                builder.append("\n");
            }

            builder.append(level).append(": ");
            List<Medal> medals = medalsPerLevel.get(level);
            if (medals == null) {
                builder.append("-");
            } else {
                builder.append(Arrays.toString(medals.toArray()));
            }
        }

        return builder.toString();
    }

    private void assertFewEnoughMedals(GameType gameType) {
        Map<Integer, List<Medal>> medalsPerLevel = getMedalsPerLevel(gameType);
        int levelsWithMedals = medalsPerLevel.keySet().size();
        int levelsCount = MathsFactory.getTopLevel(gameType);
        double percentWithMedals = 100.0 * levelsWithMedals / (double) levelsCount;

        Assert.assertThat(
                gameType
                        + ": Should get medals for at most 66% of all levels:\n"
                        + toString(medalsPerLevel),
                percentWithMedals,
                lessThanOrEqualTo(66.0));
    }

    @Test
    public void additionFewEnoughMedals() {
        assertFewEnoughMedals(GameType.ADDITION);
    }

    @Test
    public void subtractionFewEnoughMedals() {
        assertFewEnoughMedals(GameType.SUBTRACTION);
    }

    @Test
    public void multiplicationFewEnoughMedals() {
        assertFewEnoughMedals(GameType.MULTIPLICATION);
    }

    @Test
    public void divisionFewEnoughMedals() {
        assertFewEnoughMedals(GameType.DIVISION);
    }

    private void assertFrequentEnoughMedals(GameType gameType) {
        Map<Integer, List<Medal>> medalsPerLevel = getMedalsPerLevel(gameType);

        int lastLevelWithMedal = 0;
        for (int level = 1; level <= MathsFactory.getTopLevel(gameType); level++) {
            if (!medalsPerLevel.containsKey(level)) {
                continue;
            }

            int levelsBetweenMedals = level - lastLevelWithMedal;
            Assert.assertThat(
                    gameType
                            + ": Should get a medal at least every 4 levels\n"
                            + toString(medalsPerLevel),
                    levelsBetweenMedals,
                    lessThanOrEqualTo(4));

            lastLevelWithMedal = level;
        }

        Assert.assertThat(
                gameType
                        + ": Should have gotten a medal for completing the top level\n"
                        + toString(medalsPerLevel),
                medalsPerLevel,
                hasKey(MathsFactory.getTopLevel(gameType)));
    }

    @Test
    public void additionMedalsOftenEnough() {
        assertFrequentEnoughMedals(GameType.ADDITION);
    }

    @Test
    public void subtractionMedalsOftenEnough() {
        assertFrequentEnoughMedals(GameType.SUBTRACTION);
    }

    @Test
    public void multiplicationMedalsOftenEnough() {
        assertFrequentEnoughMedals(GameType.MULTIPLICATION);
    }

    @Test
    public void divisionMedalsOftenEnough() {
        assertFrequentEnoughMedals(GameType.DIVISION);
    }
}
