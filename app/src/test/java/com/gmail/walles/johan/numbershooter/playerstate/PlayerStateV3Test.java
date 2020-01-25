/*
 * Copyright 2019, Johan Walles <johan.walles@gmail.com>
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
import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class PlayerStateV3Test {
    @Rule public TemporaryFolder folder = new TemporaryFolder();

    @Test
    public void shouldMigrateOldPlayerState() throws IOException {
        File file = folder.newFile();
        Assert.assertThat(file.delete(), is(true));

        PlayerStateV2 old = PlayerStateV2.fromFile(file);
        old.increaseLevel(GameType.DIVISION);
        old.increaseLevel(GameType.DIVISION);
        old.increaseLevel(GameType.ADDITION);
        Assert.assertThat(old.getNextLevel(GameType.DIVISION), is(3));
        Assert.assertThat(old.getNextLevel(GameType.ADDITION), is(2));
        Assert.assertThat(old.getNextLevel(GameType.SUBTRACTION), is(1));

        PlayerStateV3 testMe = PlayerStateV3.fromFile(file);
        Assert.assertThat(testMe.getNextLevel(GameType.DIVISION), is(3));
        Assert.assertThat(testMe.getNextLevel(GameType.ADDITION), is(2));
        Assert.assertThat(testMe.getNextLevel(GameType.SUBTRACTION), is(1));

        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.DIVISION), is(true));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.ADDITION), is(true));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.SUBTRACTION), is(true));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.MULTIPLICATION), is(true));
    }

    @Test
    public void shouldPersistState() throws IOException {
        File file = folder.newFile();
        Assert.assertThat(file.delete(), is(true));

        PlayerStateV3 toPersist = PlayerStateV3.fromFile(file);
        toPersist.reportSuccess(GameType.MULTIPLICATION);
        toPersist.reportSuccess(GameType.ADDITION);
        toPersist.reportSuccess(GameType.ADDITION);

        toPersist.setMedalsAwarded(GameType.DIVISION);
        toPersist.setMedalsAwarded(GameType.MULTIPLICATION);
        Assert.assertThat(toPersist.medalsAlreadyAwarded(GameType.DIVISION), is(true));
        Assert.assertThat(toPersist.medalsAlreadyAwarded(GameType.MULTIPLICATION), is(true));

        PlayerStateV3 fromPersistence = PlayerStateV3.fromFile(file);
        Assert.assertThat(
                fromPersistence.getNextLevel(GameType.MULTIPLICATION),
                is(toPersist.getNextLevel(GameType.MULTIPLICATION)));
        Assert.assertThat(
                fromPersistence.getNextLevel(GameType.ADDITION),
                is(toPersist.getNextLevel(GameType.ADDITION)));
        Assert.assertThat(fromPersistence.medalsAlreadyAwarded(GameType.DIVISION), is(true));
        Assert.assertThat(fromPersistence.medalsAlreadyAwarded(GameType.MULTIPLICATION), is(true));
    }

    @Test
    public void shouldBackOff() throws IOException {
        File file = folder.newFile();
        Assert.assertThat(file.delete(), is(true));

        PlayerStateV3 testMe = PlayerStateV3.fromFile(file);

        testMe.reportSuccess(GameType.DIVISION);
        testMe.setMedalsAwarded(GameType.DIVISION);
        testMe.reportSuccess(GameType.DIVISION);
        testMe.setMedalsAwarded(GameType.DIVISION);
        testMe.reportSuccess(GameType.DIVISION);
        testMe.setMedalsAwarded(GameType.DIVISION);
        testMe.reportSuccess(GameType.DIVISION);
        testMe.setMedalsAwarded(GameType.DIVISION);
        Assert.assertThat(testMe.getNextLevel(GameType.DIVISION), is(5));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.DIVISION), is(true));

        testMe.reportFailure(GameType.DIVISION);
        Assert.assertThat(testMe.getNextLevel(GameType.DIVISION), is(4));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.DIVISION), is(true));

        testMe.reportFailure(GameType.DIVISION);
        Assert.assertThat(testMe.getNextLevel(GameType.DIVISION), is(3));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.DIVISION), is(true));

        testMe.reportFailure(GameType.DIVISION);
        Assert.assertThat(testMe.getNextLevel(GameType.DIVISION), is(3));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.DIVISION), is(true));

        testMe.reportSuccess(GameType.DIVISION);
        Assert.assertThat(testMe.getNextLevel(GameType.DIVISION), is(4));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.DIVISION), is(true));

        testMe.reportSuccess(GameType.DIVISION);
        Assert.assertThat(testMe.getNextLevel(GameType.DIVISION), is(5));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.DIVISION), is(true));

        // First time we beat this level, medals should not have been awarded
        testMe.reportSuccess(GameType.DIVISION);
        Assert.assertThat(testMe.getNextLevel(GameType.DIVISION), is(6));
        Assert.assertThat(testMe.medalsAlreadyAwarded(GameType.DIVISION), is(false));
    }
}
