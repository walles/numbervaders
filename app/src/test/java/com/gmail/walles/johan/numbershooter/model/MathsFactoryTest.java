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

package com.gmail.walles.johan.numbershooter.model;

import com.gmail.walles.johan.numbershooter.GameType;
import java.util.Collections;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class MathsFactoryTest {
    @Test
    public void getMathsUpToLevelInclusive() {
        Assert.assertThat(
                MathsFactory.create(GameType.MULTIPLICATION).getMathsUpToLevelInclusive(-1),
                CoreMatchers.is(Collections.emptyList()));

        Assert.assertThat(
                MathsFactory.create(GameType.MULTIPLICATION).getMathsUpToLevelInclusive(0),
                CoreMatchers.is(Collections.emptyList()));

        Assert.assertThat(
                MathsFactory.create(GameType.MULTIPLICATION).getMathsUpToLevelInclusive(1).size(),
                CoreMatchers.is(MathsFactory.NEW_MATHS_PER_LEVEL));
    }
}
