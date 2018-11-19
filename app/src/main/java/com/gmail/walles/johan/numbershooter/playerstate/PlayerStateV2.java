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

package com.gmail.walles.johan.numbershooter.playerstate;

import android.content.Context;

import com.gmail.walles.johan.numbershooter.GameType;

import org.jetbrains.annotations.NonNls;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;

// Consider replacing Serializable with SQLite and Flyway to support database migrations
public class PlayerStateV2 implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNls
    private static final String PLAYER_STATE_FILE_NAME = "player-state";

    /**
     * The lowest not-completed level for each game type.
     *
     * When the user starts a new level, this is the level they will end up on.
     */
    private HashMap<GameType, Integer> levels = new HashMap<>();

    /**
     * This is our on-disk backing store.
     */
    private final File file;

    private PlayerStateV2(File file) {
        this.file = file;
        levels.put(GameType.ADDITION, 1);
        levels.put(GameType.MULTIPLICATION, 1);
    }

    private static PlayerStateV2 fromFile(@NonNls File file) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (PlayerStateV2)in.readObject();
        } catch (ClassNotFoundException | InvalidClassException e) {
            return migrate(PlayerState.fromFile(file));
        } catch (FileNotFoundException e) {
            return new PlayerStateV2(file);
        }
    }

    private static PlayerStateV2 migrate(PlayerState playerState) {
        PlayerStateV2 returnMe = new PlayerStateV2(playerState.file);
        returnMe.levels.put(GameType.MULTIPLICATION, playerState.level);
        return returnMe;
    }

    public static PlayerStateV2 fromContext(Context context) throws IOException {
        return fromFile(new File(context.getFilesDir(), PLAYER_STATE_FILE_NAME));
    }

    /**
     * Atomically persist to disk via a tempfile
     */
    private void persist() throws IOException {
        File tempfile = new File(file.getPath());
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempfile))) {
            out.writeObject(this);
        }

        if (!tempfile.renameTo(file)) {
            @NonNls String message =
                    "Rename failed: " + tempfile.getAbsolutePath() + "->" + file.getAbsolutePath();
            throw new IOException(message);
        }
    }

    /**
     * This method is expected to be called from GameActivity when the level is completed
     */
    public void increaseLevel(GameType gameType) throws IOException {
        int level = levels.get(gameType);
        levels.put(gameType, level + 1);

        persist();
    }

    public int getLevel(GameType gameType) {
        return levels.get(gameType);
    }
}
