package com.example.notes.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteInfoDao {
    @Insert
    void insert(NoteInfo noteInfo);

    @Update
    void update(NoteInfo noteInfo);

    @Query("SELECT * FROM note_info")
    LiveData<List<NoteInfo>> getNoteInfo();
}
