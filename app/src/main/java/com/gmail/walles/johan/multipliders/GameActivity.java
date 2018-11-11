package com.gmail.walles.johan.multipliders;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.gmail.walles.johan.multipliders.model.FallingMaths;

import java.io.IOException;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameActivity extends AppCompatActivity {
    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler handler = new Handler();
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

        mVisible = true;
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        gameView = findViewById(R.id.game);

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
                    PlayerState.fromContext(GameActivity.this).increaseLevel();
                } catch (IOException e) {
                    throw new RuntimeException("Increasing player level failed", e);
                }

                // ... wait a bit before telling the player that they succeeded
                handler.postDelayed(() -> levelCleared(), 2000);
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

                            Intent intent = new Intent(GameActivity.this, LaunchActivity.class);
                            startActivity(intent);
                            finish();
                        })
                        .setCancelable(false)
                        .show();
        TextView textView = alertDialog.findViewById(android.R.id.message);
        assert textView != null;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
    }

    private void levelCleared() {
        PlayerState playerState;
        try {
            playerState = PlayerState.fromContext(this);
        } catch (IOException e) {
            // FIXME: Will this make the whole app crash? That's what we want, otherwise just log it
            // as an Error.
            throw new RuntimeException("Failed to read level state", e);
        }

        AlertDialog alertDialog =
                new AlertDialog.Builder(this, android.R.style.Theme_DeviceDefault_Dialog_Alert)
                        .setMessage("Level cleared, good work!")
                        .setNeutralButton("Launch Level " + playerState.getLevel(), (dialog, which) -> {
                            dialog.dismiss();
                            gameView.resetGame(this);
                        })
                        .setCancelable(false)
                        .show();

        TextView textView = alertDialog.findViewById(android.R.id.message);
        assert textView != null;
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 50);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
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
