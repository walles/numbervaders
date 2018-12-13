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
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.gmail.walles.johan.numbershooter.R;
import com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2;

import java.io.IOException;

// FIXME: Verify that the table scrolls if there are lots of medals

public class MedalsActivity extends MusicActivity {
    public static void start(Context context) {
        Intent intent = new Intent(context, MedalsActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medals);

        PlayerStateV2 playerState;
        try {
            playerState = PlayerStateV2.fromContext(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get player state", e);
        }

        /* FIXME: Re-enable this code
        List<Medal> medals = Medals.get(playerState);
        if (medals.isEmpty()) {
            TextView textView = findViewById(R.id.textView);
            textView.setText(R.string.no_medals_yet);
        }
        */

        // FIXME: List medals
        TableLayout table = findViewById(R.id.medalsTable);
        for (int i = 0; i < 35; i++) {
            TextView medalDescription = new TextView(this);
            medalDescription.setTextColor(Color.WHITE);
            medalDescription.setText("Medal number " + (i + 1));
            medalDescription.setTextSize(pixelsToSp(getResources().getDimension(R.dimen.text_size)));

            TableRow tableRow = new TableRow(this);
            tableRow.addView(medalDescription);

            table.addView(tableRow);
        }

        // Provide user with a way to go back to the launch screen
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private float pixelsToSp(float px) {
        // From: https://stackoverflow.com/a/9219417/473672
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        return px/scaledDensity;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                LaunchActivity.start(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
