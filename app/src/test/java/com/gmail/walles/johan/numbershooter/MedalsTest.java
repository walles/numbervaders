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

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import org.hamcrest.Matchers;
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

        Medal timesOneTableMedal = new Medal(Medal.Flavor.BRONZE, "2131558460: [2131558441, ×, 1]");

        Collection<Medal> medalsEarned =
                Medals.getLatest(resources, playerState, GameType.MULTIPLICATION);

        Assert.assertThat(medalsEarned, Matchers.contains(timesOneTableMedal));
    }

    @Test
    public void multiplicationMedalsOftenEnough() {
        Assert.fail(
                "Should verify that we get a multiplication medal at least every 4 completed levels");
    }

    @Test
    public void multiplicationFewEnoughMedals() {
        Assert.fail(
                "Should verify that we get a multiplication no more often than every 2 completed levels on average");
    }

    // FIXME: Add medals frequency tests for addition
    // FIXME: Add medals frequency tests for subtraction
    // FIXME: Add medals frequency tests for division
}
