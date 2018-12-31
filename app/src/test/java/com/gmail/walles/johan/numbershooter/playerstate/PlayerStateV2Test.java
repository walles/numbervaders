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

package com.gmail.walles.johan.numbershooter.playerstate;

import static org.hamcrest.CoreMatchers.is;

import com.gmail.walles.johan.numbershooter.GameType;
import com.gmail.walles.johan.numbershooter.PlayerState;
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PlayerStateV2Test {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldMigrateOldPlayerState() throws IOException {
        File file = folder.newFile();
        Assert.assertThat(file.delete(), is(true));

        PlayerState playerState = PlayerState.fromFile(file);
        playerState.increaseLevel();
        Assert.assertThat(playerState.getLevel(), is(2));

        PlayerStateV2 testMe = PlayerStateV2.fromFile(file);
        Assert.assertThat(testMe.getNextLevel(GameType.MULTIPLICATION), is(2));
        Assert.assertThat(testMe.getNextLevel(GameType.ADDITION), is(1));
    }

    @Test
    public void shouldPersistState() throws IOException {
        File file = folder.newFile();
        Assert.assertThat(file.delete(), is(true));

        PlayerStateV2 toPersist = PlayerStateV2.fromFile(file);
        toPersist.increaseLevel(GameType.MULTIPLICATION);
        toPersist.increaseLevel(GameType.ADDITION);
        toPersist.increaseLevel(GameType.ADDITION);

        PlayerStateV2 fromPersistence = PlayerStateV2.fromFile(file);
        Assert.assertThat(
                fromPersistence.getNextLevel(GameType.MULTIPLICATION),
                is(toPersist.getNextLevel(GameType.MULTIPLICATION)));
        Assert.assertThat(
                fromPersistence.getNextLevel(GameType.ADDITION),
                is(toPersist.getNextLevel(GameType.ADDITION)));
    }
}
