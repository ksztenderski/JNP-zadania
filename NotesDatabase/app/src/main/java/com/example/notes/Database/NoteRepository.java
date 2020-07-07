package com.example.notes.Database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class NoteRepository {
    private NoteDao noteDao;
    private NoteInfoDao noteInfoDao;
    private LiveData<List<Note>> allNotes;
    private LiveData<List<NoteInfo>> allNoteInfo;

    NoteRepository(Application application) {
        NoteRoomDatabase db = NoteRoomDatabase.getDatabase(application);
        noteDao = db.noteDao();
        noteInfoDao = db.noteInfoDao();
        allNotes = noteDao.getNotes();
        allNoteInfo = noteInfoDao.getNoteInfo();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    void insertNote(Note note) {
        NoteRoomDatabase.databaseWriteExecutor.execute(() -> noteDao.insert(note));
    }

    void updateNote(Note note) {
        NoteRoomDatabase.databaseWriteExecutor.execute(() -> noteDao.update(note));
    }

    public LiveData<Note> getNote(Date date) {
        return noteDao.getNote(date);
    }

    public LiveData<List<NoteInfo>> getAllNoteInfo() {
        return allNoteInfo;
    }

    void insertNoteInfo(NoteInfo noteInfo) {
        NoteRoomDatabase.databaseWriteExecutor.execute(() -> noteInfoDao.insert(noteInfo));
    }

    void updateNoteInfo(NoteInfo noteInfo) {
        NoteRoomDatabase.databaseWriteExecutor.execute(() -> noteInfoDao.update(noteInfo));
    }
}
