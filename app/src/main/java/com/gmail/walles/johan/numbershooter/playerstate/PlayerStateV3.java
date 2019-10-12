/*
 * Copyright 2019, Johan Walles <johan.walles@gmail.com>
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
import androidx.annotation.VisibleForTesting;
import com.gmail.walles.johan.numbershooter.GameType;
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
import org.jetbrains.annotations.NonNls;

/**
 * Note that this class needs to stay in the {@link
 * com.gmail.walles.johan.numbershooter.playerstate} package for deserialization of old player
 * states to work.
 */
public class PlayerStateV3 implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNls private static final String PLAYER_STATE_FILE_NAME = "player-state";

    /**
     * The lowest not-completed level for each game type.
     *
     * <p>Note that we store the enum {@link GameType} as a {@link String} to be able to support
     * more types in the future without more data migrations.
     */
    private HashMap<String, Integer> hardestLevels = new HashMap<>();

    /**
     * The next level that the user will get to play for each game type.
     *
     * <p>This can be sometimes be lower than hardestLevels, but not too low.
     *
     * <p>Note that we store the enum {@link GameType} as a {@link String} to be able to support
     * more types in the future without more data migrations.
     */
    private HashMap<String, Integer> nextToPlayLevels = new HashMap<>();

    /**
     * Maps game type name to the highest level + 1 for which medals have been awarded.
     */
    private HashMap<String, Integer> highestMedalsAwardedLevels = new HashMap<>();

    /** This is our on-disk backing store. */
    private final File file;

    private PlayerStateV3(File file) {
        this.file = file;
    }

    @VisibleForTesting
    private static PlayerStateV3 fromFile(@NonNls File file) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            // FIXME: Look at the file timestamp here to determine if we should drop one or more
            // levels?
            return (PlayerStateV3) in.readObject();
        } catch (ClassCastException | ClassNotFoundException | InvalidClassException e) {
            return migrate(PlayerStateV2.fromFile(file));
        } catch (FileNotFoundException e) {
            return new PlayerStateV3(file);
        }
    }

    private static PlayerStateV3 migrate(PlayerStateV2 playerState) {
        PlayerStateV3 returnMe = new PlayerStateV3(playerState.file);
        returnMe.hardestLevels = new HashMap<>(playerState.levels);
        returnMe.nextToPlayLevels = new HashMap<>(playerState.levels);

        // FIXME: Look at the file timestamp here to determine if we should drop one or more levels?

        return returnMe;
    }

    public static PlayerStateV3 fromContext(Context context) throws IOException {
        return fromFile(new File(context.getFilesDir(), PLAYER_STATE_FILE_NAME));
    }

    /** Atomically persist to disk via a tempfile */
    private void persist() throws IOException {
        @NonNls String pathname = file.getPath() + ".tmp";
        File tempfile = new File(pathname);
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tempfile))) {
            out.writeObject(this);
        }

        if (!tempfile.renameTo(file)) {
            @NonNls
            String message =
                    "Rename failed: " + tempfile.getAbsolutePath() + "->" + file.getAbsolutePath();
            throw new IOException(message);
        }
    }

    /** This method is expected to be called from GameActivity when a level is completed */
    public void reportSuccess(GameType gameType) throws IOException {
        Integer next = nextToPlayLevels.get(gameType.toString());
        if (next == null) {
            next = 1;
        }
        nextToPlayLevels.put(gameType.toString(), next);

        Integer hardest = hardestLevels.get(gameType.toString());
        if (hardest != null && hardest < next) {
            hardest = next;
            hardestLevels.put(gameType.toString(), hardest);
        }

        persist();
    }

    /** This method is expected to be called from GameActivity when the player failed a level */
    public void reportFailure(GameType gameType) throws IOException {
        Integer hardest = hardestLevels.get(gameType.toString());
        if (hardest == null) {
            hardest = 1;
        }

        // How low can you go?
        int atLeast = hardest - 3;
        if (atLeast < 1) {
            atLeast = 1;
        }

        Integer next = nextToPlayLevels.get(gameType.toString());
        if (next == null) {
            next = hardest;
        }

        // This is to simplify for people so that they get to succeed at at least 2/3 levels
        next -= 2;

        if (next < atLeast) {
            next = atLeast;
        }
        nextToPlayLevels.put(gameType.toString(), next);

        persist();
    }

    /**
     * Returns the next level this user will be presented with.
     */
    public int getNextLevel(GameType gameType) {
        Integer returnMe = nextToPlayLevels.get(gameType.toString());
        if (returnMe == null) {
            return 1;
        }
        return returnMe;
    }

    public boolean medalsAlreadyAwarded(GameType gameType) {
        Integer highestAwardLevel = highestMedalsAwardedLevels.get(gameType.toString());
        if (highestAwardLevel == null) {
            return false;
        }

        int nextLevel = getNextLevel(gameType);

        return highestAwardLevel >= nextLevel;
    }

    public void setMedalsAwarded(GameType gameType) {
        Integer highestAwardLevel = highestMedalsAwardedLevels.get(gameType.toString());
        if (highestAwardLevel == null) {
            highestAwardLevel = 0;
        }

        int nextLevel = getNextLevel(gameType);
        if (nextLevel > highestAwardLevel) {
            highestMedalsAwardedLevels.put(gameType.toString(), nextLevel);
        }
    }
}
