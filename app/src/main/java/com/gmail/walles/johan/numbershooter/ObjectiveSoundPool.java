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

import android.content.Context;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import timber.log.Timber;

public class ObjectiveSoundPool {
    public class SoundEffect {
        private final String name;
        private final int sampleId;
        private boolean playRequestedWhileLoading = false;
        private float volume = 1.0f;

        private SoundEffect(String name, int sampleId) {
            this.name = name;
            this.sampleId = sampleId;
        }

        public void play() {
            if (soundPool == null) {
                throw new IllegalStateException("Sound pool closed");
            }

            int result = soundPool.play(sampleId, volume, volume, 0, 0, 1);
            if (result == 0) {
                Timber.w("Playing <%s> sound failed", name);
                playRequestedWhileLoading = true;
            }
        }

        public SoundEffect setVolume(double zeroToOne) {
            if (zeroToOne < 0 || zeroToOne > 1) {
                throw new IllegalArgumentException("Volume out of 0.0-1.0 bounds: " + zeroToOne);
            }

            volume = (float)zeroToOne;

            return this;
        }
    }

    @Nullable
    private SoundPool soundPool;

    @Nullable
    private List<SoundEffect> soundEffects = new ArrayList<>();

    public ObjectiveSoundPool() {
        soundPool = new SoundPool.Builder().setMaxStreams(3).build();
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            SoundEffect soundEffect = getSoundEffectById(sampleId);

            if (status != 0) {
                Timber.w("Loading <%s> sound failed: %d", soundEffect.name, status);
                return;
            }

            Timber.i("Sound effect loaded: <%s>", soundEffect.name);

            if (soundEffect.playRequestedWhileLoading) {
                Timber.i("Playing now-loaded sound <%s>", soundEffect.name);
                soundEffect.play();
            }
        });
    }

    public void close() {
        if (soundPool != null) {
            soundPool.release();

            // SoundEffect.play() will actually fail after this
            soundPool = null;
        }
    }

    private SoundEffect getSoundEffectById(int sampleId) {
        if (soundEffects == null) {
            throw new IllegalStateException("Sound pool closed, sound effects shut down");
        }

        for (SoundEffect soundEffect: soundEffects) {
            if (soundEffect.sampleId == sampleId) {
                return soundEffect;
            }
        }

        throw new NoSuchElementException("Sample id " + sampleId + " not found");
    }

    /**
     * @param name A free-text name used for logging purposes
     */
    public SoundEffect load(Context context, @RawRes int resId, String name) {
        if (soundPool == null || soundEffects == null) {
            throw new IllegalStateException("Sound pool closed");
        }

        int sampleId = soundPool.load(context, resId, 1);

        SoundEffect soundEffect = new SoundEffect(name, sampleId);
        soundEffects.add(soundEffect);
        return soundEffect;
    }
}
