package com.example.notes.Database;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "note_info")
public class NoteInfo {
    @PrimaryKey
    @NonNull
    @TypeConverters({DateConverter.class})
    public Date dateCreated;

    @NonNull
    @TypeConverters({DateConverter.class})
    public Date dateModified;

    @NonNull
    public String title;

    public NoteInfo(@NonNull Date dateCreated, @NonNull String title) {
        this.dateCreated = dateCreated;
        this.dateModified = dateCreated;
        this.title = title;
    }
}
