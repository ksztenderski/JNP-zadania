package com.example.notes.Database;

import android.content.Context;

import androidx.room.Dao;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Note.class, NoteInfo.class}, version = 1, exportSchema = false)
public abstract class NoteRoomDatabase extends RoomDatabase {
    public abstract NoteDao noteDao();

    public abstract NoteInfoDao noteInfoDao();

    private static volatile NoteRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static NoteRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (NoteRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            NoteRoomDatabase.class, "category_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}