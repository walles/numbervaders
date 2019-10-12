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
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV3;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.hamcrest.number.OrderingComparison;
import org.jetbrains.annotations.NonNls;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class MedalsTest {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

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
            return id + ": " + Arrays.toString(formatArgs);
        }
    }

    private PlayerStateV3 getPlayerStateAtLevel(GameType gameType, int level) throws IOException {
        Assert.assertThat(level, OrderingComparison.greaterThanOrEqualTo(1));

        File file = folder.newFile();
        Assert.assertThat(file.delete(), is(true));

        PlayerStateV3 returnMe = PlayerStateV3.fromFile(file);
        for (int l = 1; l < level; l++) {
            returnMe.reportSuccess(gameType);
            // FIXME: Do we want to returnMe.setMedalsAwarded() here
        }

        return returnMe;
    }

    /** Validate that get() and getLatest() don't contradict each other. */
    @Test
    public void testGetVsGetLatest() throws IOException {
        Resources resources = new TestableResources();

        for (GameType gameType : GameType.values()) {
            for (int level = 2; level <= 45; level++) {
                PlayerStateV3 playerStateNow = getPlayerStateAtLevel(gameType, level);
                Collection<Medal> after = Medals.get(resources, playerStateNow);

                PlayerStateV3 playerStateBefore = getPlayerStateAtLevel(gameType, level - 1);
                Collection<Medal> before = Medals.get(resources, playerStateBefore);

                Collection<Medal> earnedAccordingToMedalsClass =
                        Medals.getLatest(resources, playerStateNow, gameType);

                Collection<Medal> actuallyGained = new LinkedList<>(after);
                actuallyGained.removeAll(before);

                Assert.assertThat(
                        gameType + " level " + level,
                        earnedAccordingToMedalsClass,
                        is(actuallyGained));
            }
        }
    }

    @Test
    public void testOneTimesTableMedal() throws IOException {
        // We've just done levels 1 to 4, covering 1*1 - 1*10 / 10*1
        final int LOWEST_NON_COMPLETED_LEVEL = 5;

        Resources resources = new TestableResources();

        PlayerStateV3 playerState =
                getPlayerStateAtLevel(GameType.MULTIPLICATION, LOWEST_NON_COMPLETED_LEVEL);

        Medal timesOneTableMedal =
                new Medal(
                        Medal.Flavor.BRONZE,
                        R.string.way_of_counting_colon_sign_number_done + ": [2131623980, ×, 1]");

        Collection<Medal> medalsEarned =
                Medals.getLatest(resources, playerState, GameType.MULTIPLICATION);

        Assert.assertThat(medalsEarned, contains(timesOneTableMedal));
    }

    private Map<Integer, List<Medal>> getMedalsPerLevel(GameType gameType) throws IOException {
        Map<Integer, List<Medal>> returnMe = new HashMap<>();

        for (int level = 1; level <= MathsFactory.getTopLevel(gameType); level++) {
            PlayerStateV3 playerState = getPlayerStateAtLevel(gameType, level + 1);

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

    private void assertFewEnoughMedals(GameType gameType) throws IOException {
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
    public void additionFewEnoughMedals() throws IOException {
        assertFewEnoughMedals(GameType.ADDITION);
    }

    @Test
    public void subtractionFewEnoughMedals() throws IOException {
        assertFewEnoughMedals(GameType.SUBTRACTION);
    }

    @Test
    public void multiplicationFewEnoughMedals() throws IOException {
        assertFewEnoughMedals(GameType.MULTIPLICATION);
    }

    @Test
    public void divisionFewEnoughMedals() throws IOException {
        assertFewEnoughMedals(GameType.DIVISION);
    }

    private void assertFrequentEnoughMedals(GameType gameType) throws IOException {
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
    public void additionMedalsOftenEnough() throws IOException {
        assertFrequentEnoughMedals(GameType.ADDITION);
    }

    @Test
    public void subtractionMedalsOftenEnough() throws IOException {
        assertFrequentEnoughMedals(GameType.SUBTRACTION);
    }

    @Test
    public void multiplicationMedalsOftenEnough() throws IOException {
        assertFrequentEnoughMedals(GameType.MULTIPLICATION);
    }

    @Test
    public void divisionMedalsOftenEnough() throws IOException {
        assertFrequentEnoughMedals(GameType.DIVISION);
    }
}
