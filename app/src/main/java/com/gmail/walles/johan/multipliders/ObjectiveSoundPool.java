package com.gmail.walles.johan.multipliders;

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

        private SoundEffect(String name, int sampleId) {
            this.name = name;
            this.sampleId = sampleId;
        }

        public void play() {
            if (soundPool == null) {
                throw new IllegalStateException("Sound pool closed");
            }

            int result = soundPool.play(sampleId, 1, 1, 0, 0, 1);
            if (result == 0) {
                Timber.w("Playing <%s> sound failed", name);
                playRequestedWhileLoading = true;
            }
        }
    }

    @Nullable
    private SoundPool soundPool;

    @Nullable
    private List<SoundEffect> soundEffects = new ArrayList<>();

    public ObjectiveSoundPool() {
        soundPool = new SoundPool.Builder().setMaxStreams(2).build();
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
