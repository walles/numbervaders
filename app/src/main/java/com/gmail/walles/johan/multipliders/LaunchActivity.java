package com.gmail.walles.johan.multipliders;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.Button;

import java.io.IOException;

public class LaunchActivity extends MusicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button startButton = findViewById(R.id.startButton);
        assert startButton != null;
        startButton.setOnClickListener(v -> {
            Intent intent = new Intent(LaunchActivity.this, GameActivity.class);
            startActivity(intent);
        });

        PlayerState playerState;
        try {
            playerState = PlayerState.fromContext(this);
        } catch (IOException e) {
            throw new RuntimeException("Failed to get player state", e);
        }
        startButton.setText("Level " + playerState.getLevel());
    }
}
