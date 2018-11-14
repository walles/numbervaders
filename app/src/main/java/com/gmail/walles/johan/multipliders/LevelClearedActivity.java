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

package com.gmail.walles.johan.multipliders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Locale;

public class LevelClearedActivity extends MusicActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, LevelClearedActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_cleared);

        PlayerState playerState;
        try {
            playerState = PlayerState.fromContext(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to figure out which level was just completed", e);
        }

        // FIXME: Have the one launching us pass us this number; otherwise we'll get it wrong if
        // users replay older levels
        int clearedLevel = playerState.getLevel() - 1;

        TextView textView = findViewById(R.id.level_cleared_text);
        textView.setText(String.format(Locale.getDefault(), "Level %d cleared",
                clearedLevel));

        Button button = findViewById(R.id.next_level_button);
        button.setText(String.format(Locale.getDefault(), "Level %d", playerState.getLevel()));
        button.setOnClickListener(v -> {
            Intent intent = new Intent(LevelClearedActivity.this, GameActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
