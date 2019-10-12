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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jetbrains.annotations.NonNls;
import timber.log.Timber;

// Consider replacing Serializable with SQLite and Flyway to support database migrations

/**
 * Deprecated Player State, see {@link
 * com.gmail.walles.johan.numbershooter.playerstate.PlayerStateV2}
 */
public class PlayerState implements Serializable {
    private static final long serialVersionUID = 1L;

    @NonNls private static final String PLAYER_STATE_FILE_NAME = "player-state";

    /**
     * The lowest not-completed level.
     *
     * <p>When the user starts a new level, this is the level they will end up on.
     */
    public int level = 1;

    /** This is our on-disk backing store. */
    public final File file;

    private PlayerState(File file) {
        this.file = file;
    }

    /** Has default protection so that it can be called from PlayerStateV2. */
    public static PlayerState fromFile(@NonNls File file) throws IOException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            return (PlayerState) in.readObject();
        } catch (ClassNotFoundException e) {
            throw new IOException("PlayerState not found in " + file, e);
        } catch (FileNotFoundException e) {
            return new PlayerState(file);
        } catch (InvalidClassException e) {
            Timber.w(e, "Player state loading failed, starting over");
            return new PlayerState(file);
        }
    }

    @SuppressWarnings("unused")
    public static PlayerState fromContext(Context context) throws IOException {
        return fromFile(new File(context.getFilesDir(), PLAYER_STATE_FILE_NAME));
    }

    /** Atomically persist to disk via a tempfile */
    private void persist() throws IOException {
        File tempfile = new File(file.getPath());
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

    /** This method is expected to be called from GameActivity when the level is completed */
    public void increaseLevel() throws IOException {
        level++;

        persist();
    }

    public int getLevel() {
        return level;
    }
}
