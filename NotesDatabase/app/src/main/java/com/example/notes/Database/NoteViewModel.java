package com.example.notes.Database;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.Date;
import java.util.List;

public class NoteViewModel extends AndroidViewModel {
    private NoteRepository repository;
    private LiveData<List<Note>> allNotes;
    private LiveData<List<NoteInfo>> allNoteInfo;

    public NoteViewModel(Application application) {
        super(application);
        repository = new NoteRepository(application);
        allNotes = repository.getAllNotes();
        allNoteInfo = repository.getAllNoteInfo();
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }

    public void insertNote(Note note) {
        repository.insertNote(note);
    }

    public void updateNote(Note note) {
        repository.updateNote(note);
    }

    public LiveData<Note> getNote(Date date) {
        return repository.getNote(date);
    }

    public LiveData<List<NoteInfo>> getAllNoteInfo() {
        return allNoteInfo;
    }

    public void inertNoteInfo(NoteInfo noteInfo) {
        repository.insertNoteInfo(noteInfo);
    }

    public void updateNoteInfo(NoteInfo noteInfo) {
        repository.updateNoteInfo(noteInfo);
    }
}
