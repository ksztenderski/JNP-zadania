package com.example.notes.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;
import androidx.room.TypeConverters;

import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "note")
public class Note {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @TypeConverters({DateConverter.class})
    public Date dateCreated;

    @NonNull
    public String title;

    @NonNull
    public String description;

    @NonNull
    public String content;

    public Note(@NonNull String title, @NonNull String description, @NonNull String content) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.dateCreated = Calendar.getInstance().getTime();
    }
}
