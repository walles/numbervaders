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
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.gmail.walles.johan.numbershooter.GameType;
import com.gmail.walles.johan.numbershooter.Medal;
import com.gmail.walles.johan.numbershooter.Medals;
import com.gmail.walles.johan.numbershooter.MedalsAdapter;
import com.gmail.walles.johan.numbershooter.R;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;

import java.io.IOException;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

public class LevelClearedActivity extends MusicActivity {
    private GameType gameType;
    private int clearedLevel;

    private static final String GAME_TYPE_EXTRA = "gameType";
    private static final String LEVEL_EXTRA = "clearedLevel";
    public static void start(Context context, GameType gameType, int level) {
        Intent intent = new Intent(context, LevelClearedActivity.class);
        intent.putExtra(GAME_TYPE_EXTRA, gameType.toString());
        intent.putExtra(LEVEL_EXTRA, level);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameType = GameType.valueOf(getIntent().getStringExtra(GAME_TYPE_EXTRA));
        clearedLevel = getIntent().getIntExtra(LEVEL_EXTRA, 0);
        if (clearedLevel <= 0) {
            throw new RuntimeException("Level not found: " + getIntent());
        }

        setContentView(R.layout.activity_level_cleared);

        TextView textView = findViewById(R.id.level_cleared_text);
        textView.setText(String.format(Locale.getDefault(), "Level %d cleared",
                clearedLevel));

        Button button = findViewById(R.id.next_level_button);
        button.setText(String.format(Locale.getDefault(), "Level %d", clearedLevel + 1));
        button.setOnClickListener(v -> {
            GameActivity.start(this, gameType, clearedLevel + 1);
            finish();
        });

        listMedals();
    }

    private void listMedals() {
        PlayerStateV2 playerState;
        try {
            playerState = PlayerStateV2.fromContext(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get player state", e);
        }

        List<Medal> medalsEarned = Medals.getLatest(playerState, gameType);

        // FIXME: Test code, remove or comment out
        medalsEarned.add(new Medal(Medal.Flavor.GOLD, "Test medal 1"));
        medalsEarned.add(new Medal(Medal.Flavor.SILVER, "Test medal 2"));
        medalsEarned.add(new Medal(Medal.Flavor.BRONZE, "Test medal 3"));

        if (medalsEarned.isEmpty()) {
            return;
        }

        RecyclerView medalsList = findViewById(R.id.medalsList);
        medalsList.setVisibility(View.VISIBLE);

        int medalSize = 2 * getResources().getDimensionPixelSize(R.dimen.big_text_size);
        medalsList.setLayoutManager(new LinearLayoutManager(this));
        medalsList.setAdapter(new MedalsAdapter(medalSize, medalsEarned));

        // Iterating backwards will get Android to show the dialogs in the same order as the medals
        // list.
        ListIterator<Medal> backwards = medalsEarned.listIterator(medalsEarned.size());
        while (backwards.hasPrevious()) {
            Medal medal = backwards.previous();
            showEarnedMedalDialog(medal);
        }
    }

    private void showEarnedMedalDialog(Medal medal) {
        // FIXME: Fanfare when the dialog gets on-screen

        // FIXME: Add medal image as dialog icon

        new AlertDialog.Builder(this)
                .setMessage(medal.getDescription())
                .setNeutralButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }
}
