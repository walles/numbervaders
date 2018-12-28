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

package com.gmail.walles.johan.numbershooter.activities;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.gmail.walles.johan.numbershooter.R;

/**
 * An activity with background music.
 */
@SuppressLint("Registered")
public class MusicActivity extends AppCompatActivity {
    private MediaPlayer music;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        music = MediaPlayer.create(this, R.raw.bensound_scifi);
        setVolumePercent(100);
        music.setLooping(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        music.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        music.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        music.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        music.release();
    }

    /**
     * From: https://stackoverflow.com/a/12075910/473672
     * @param percent 0-100
     */
    private void setVolumePercent(int percent) {
        final int maxVolume = 100;

        float log1=(float)(Math.log(maxVolume - percent)/Math.log(maxVolume));
        music.setVolume(1 - log1, 1 - log1);
    }

}
