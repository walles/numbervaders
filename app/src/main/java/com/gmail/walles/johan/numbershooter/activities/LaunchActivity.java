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

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import androidx.annotation.IdRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import com.gmail.walles.johan.numbershooter.GameType;
import com.gmail.walles.johan.numbershooter.R;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV3;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.jetbrains.annotations.NonNls;
import timber.log.Timber;

public class LaunchActivity extends MusicActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, LaunchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        PlayerStateV3 playerState;
        try {
            playerState = PlayerStateV3.fromContext(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get player state", e);
        }

        configureButton(R.id.startButton1, playerState, GameType.ADDITION);
        configureButton(R.id.startButton2, playerState, GameType.SUBTRACTION);
        configureButton(R.id.startButton3, playerState, GameType.MULTIPLICATION);
        configureButton(R.id.startButton4, playerState, GameType.DIVISION);

        Button medalsButton = findViewById(R.id.medalsButton);
        medalsButton.setOnClickListener(v -> MedalsActivity.start(LaunchActivity.this));
    }

    private void configureButton(
            @IdRes int buttonId, PlayerStateV3 playerState, GameType gameType) {
        Button button = findViewById(buttonId);

        SpannableString labelText =
                new SpannableString(
                        getString(
                                R.string.way_of_counting_level_n,
                                gameType.prettyOperator,
                                playerState.getNextLevel(gameType)));
        // Make operator bigger
        labelText.setSpan(new RelativeSizeSpan(2f), 0, 1, 0);
        button.setText(labelText);

        int startLevel = playerState.getNextLevel(gameType);
        button.setOnClickListener(v -> GameActivity.start(this, gameType, startLevel));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getDelegate().getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.credits) {
            LaunchActivity.this.showAboutDialog();
            return true;
        }

        if (item.getItemId() == R.id.view_source_code) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            @NonNls String uri = "https://github.com/walles/numbervaders?files=1";
            intent.setData(Uri.parse(uri));
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings({"resource", "IOResourceOpenedButNotSafelyClosed"})
    private void showAboutDialog() {
        String credits;
        try (InputStream inputStream = getResources().openRawResource(R.raw.credits)) {
            @NonNls final String BEGINNING_OF_INPUT = "\\A";
            credits =
                    new Scanner(inputStream, StandardCharsets.UTF_8.name())
                            .useDelimiter(BEGINNING_OF_INPUT)
                            .next();
        } catch (IOException e) {
            Timber.e(e, "Unable to load credits resource");
            return;
        }

        new AlertDialog.Builder(this)
                .setMessage(credits)
                .setCancelable(true)
                .setTitle(R.string.credits)
                .setNeutralButton(android.R.string.ok, (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
}
