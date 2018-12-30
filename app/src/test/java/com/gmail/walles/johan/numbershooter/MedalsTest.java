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
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;
import java.util.Collection;
import java.util.LinkedList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

public class MedalsTest {
    /** Validate that get() and getLatest() don't contradict each other. */
    @Test
    public void testGetVsGetLatest() {
        // Mock both varargs and non-varargs flavors of the getString() method
        Resources resources = Mockito.mock(Resources.class);
        Mockito.when(resources.getString(Mockito.anyInt())).thenReturn("mock description");
        Mockito.when(resources.getString(Mockito.anyInt(), ArgumentMatchers.any()))
                .thenReturn("mock description");

        // Validate our mocking
        Assert.assertThat(resources.getString(12345), is("mock description"));
        Assert.assertThat(resources.getString(12345, "any", "varargs"), is("mock description"));

        for (GameType gameType : GameType.values()) {
            for (int level = 1; level <= 45; level++) {
                PlayerStateV2 playerStateNow = Mockito.mock(PlayerStateV2.class);
                Mockito.when(playerStateNow.getLevel(gameType)).thenReturn(level);
                Collection<Medal> after = Medals.get(resources, playerStateNow);

                PlayerStateV2 playerStateBefore = Mockito.mock(PlayerStateV2.class);
                Mockito.when(playerStateBefore.getLevel(gameType)).thenReturn(level - 1);
                Collection<Medal> before = Medals.get(resources, playerStateBefore);

                Collection<Medal> earnedAccordingToMedalsClass =
                        Medals.getLatest(resources, playerStateNow, gameType);

                Collection<Medal> actuallyGained = new LinkedList<>(after);
                actuallyGained.removeAll(before);

                Assert.assertThat(earnedAccordingToMedalsClass, is(actuallyGained));
            }
        }
    }
}
