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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.gmail.walles.johan.numbershooter.model.FallingMaths;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends MusicActivity {
    private static final String GAME_TYPE_EXTRA = "gameType";
    private static final String LEVEL_EXTRA = "level";
    public static void start(Context context, GameType gameType, int level) {
        Intent intent = new Intent(context, GameActivity.class);
        intent.putExtra(GAME_TYPE_EXTRA, gameType.toString());
        intent.putExtra(LEVEL_EXTRA, level);
        context.startActivity(intent);
    }

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler handler = new Handler();
    private GameType gameType;
    private GameView gameView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            gameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = () -> {
        // FIXME: Before releasing on Google Play, re-enable this hide() call to make the app
        // full screen.
        // FIXME: hide();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game);

        gameType = GameType.valueOf(getIntent().getStringExtra(GAME_TYPE_EXTRA));
        int level = getIntent().getIntExtra(LEVEL_EXTRA, 0);
        if (level <= 0) {
            throw new RuntimeException("Level not found: " + getIntent());
        }

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        gameView = findViewById(R.id.game);
        gameView.restart(gameType, level);

        // Set up the user interaction to manually show or hide the system UI.
        gameView.setOnClickListener(view -> toggle());

        gameView.setOnGameOverListener(new GameView.OnGameOverListener() {
            @Override
            public void onPlayerDied(Iterable<FallingMaths> failedMaths) {
                handler.postDelayed(() -> tellPlayerItDied(failedMaths), 2000);
            }

            @Override
            public void onLevelCleared() {
                // Update the stored level now, but...
                try {
                    PlayerStateV2.fromContext(GameActivity.this).increaseLevel(gameType);
                } catch (IOException e) {
                    throw new RuntimeException("Increasing player level failed", e);
                }

                // ... wait a bit before telling the player that they succeeded
                handler.postDelayed(() -> {
                    LevelClearedActivity.start(GameActivity.this, gameType, level);
                    finish();
                }, 2000);
            }
        });

        KeyboardView keyboard = findViewById(R.id.keyboard);
        keyboard.setOnKeypress(gameView::insertDigit);
    }

    private void tellPlayerItDied(Iterable<FallingMaths> failedMaths) {
        FallingMaths lowestAnswer = failedMaths.iterator().next();
        for (FallingMaths failed: failedMaths) {
            if (failed.getY() > lowestAnswer.getY()) {
                lowestAnswer = failed;
            }
        }

        AlertDialog alertDialog =
                new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                        .setMessage(lowestAnswer.question + "=" + lowestAnswer.answer)
                        .setNeutralButton("OK", (dialog, which) -> {
                            dialog.dismiss();

                            LaunchActivity.start(this);
                            finish();
                        })
                        .setOnCancelListener(dialog -> finish())
                        .show();
        TextView textView = alertDialog.findViewById(android.R.id.message);
        assert textView != null;
        textView.setTextSize(pixelsToSp(getResources().getDimension(R.dimen.big_text_size)));
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    private float pixelsToSp(float px) {
        // From: https://stackoverflow.com/a/9219417/473672
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        handler.removeCallbacks(mHideRunnable);
        handler.postDelayed(mHideRunnable, 100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ((GameView)findViewById(R.id.game)).close();
        ((KeyboardView)findViewById(R.id.keyboard)).close();
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        handler.removeCallbacks(mShowPart2Runnable);
        handler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        gameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        handler.removeCallbacks(mHidePart2Runnable);
        handler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

}
