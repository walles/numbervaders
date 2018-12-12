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
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

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

        // FIXME: List medals earned on this level
    }
}
