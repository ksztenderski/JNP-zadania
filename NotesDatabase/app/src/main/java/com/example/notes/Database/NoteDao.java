package com.example.notes.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.TypeConverters;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Query("SELECT * FROM note")
    LiveData<List<Note>> getNotes();

    @TypeConverters({DateConverter.class})
    @Query("SELECT * FROM note WHERE dateCreated= :date")
    LiveData<Note> getNote(Date date);
}
