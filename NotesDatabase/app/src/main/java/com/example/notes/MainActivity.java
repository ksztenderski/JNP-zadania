package com.example.notes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.notes.Database.Note;
import com.example.notes.Database.NoteInfo;
import com.example.notes.Database.NoteViewModel;

import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<NoteInfo> notes;
    private TextView title;
    private TextView description;
    private TextView content;
    private int notesPosition = 0;

    private NoteViewModel noteViewModel;

    public static final int REQUEST = 1;
    public static final String EDIT_ADD_TITLE = "com.example.notes.edit_add_title";
    public static final String EDIT_ADD_DESCRIPTION = "com.example.notes.edit_add_description";
    public static final String EDIT_ADD_CONTENT = "com.example.notes.edit_add_content";
    public static final String EDIT_ADD_EDITING = "com.example.notes.edit_add_editing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        content = findViewById(R.id.content);

        noteViewModel = new ViewModelProvider(this).get(NoteViewModel.class);

        noteViewModel.getAllNoteInfo().observe(this, (res) -> {
            notes = res;
            if (!notes.isEmpty()) {
                updateNote(notes.get(notesPosition));
            }
        });
    }

    private void updateNote(NoteInfo noteInfo) {
        noteViewModel.getNote(noteInfo.dateCreated).observe(this, (res) -> {
            title.setText(res.title);
            description.setText(res.description);
            content.setText(res.content);
        });
    }

    public void previousListener(View view) {
        if (notesPosition > 0) {
            updateNote(notes.get(--notesPosition));
        }
    }

    public void nextListener(View view) {
        if (notesPosition < notes.size() - 1) {
            updateNote(notes.get(++notesPosition));
        }
    }

    private void startEditNodeActivity(String title, String description, String content, boolean editing) {
        Intent intent = new Intent(this, EditNoteActivity.class);
        intent.putExtra(EDIT_ADD_TITLE, title);
        intent.putExtra(EDIT_ADD_DESCRIPTION, description);
        intent.putExtra(EDIT_ADD_CONTENT, content);
        intent.putExtra(EDIT_ADD_EDITING, editing);

        startActivityForResult(intent, REQUEST);
    }

    public void addNodeListener(View view) {
        startEditNodeActivity("", "", "", false);
    }

    public void editNoteListener(View view) {
        if (!notes.isEmpty()) {
            String titleText = title.getText().toString();
            String descriptionText = description.getText().toString();
            String contentText = content.getText().toString();

            startEditNodeActivity(titleText, descriptionText, contentText, true);
        }
    }

    private void saveAddNote(String titleText, String descriptionText, String contentText) {
        Note newNote = new Note(titleText, descriptionText, contentText);
        NoteInfo newNoteInfo = new NoteInfo(newNote.dateCreated, newNote.title);

        notesPosition = notes.size();

        noteViewModel.insertNote(newNote);
        noteViewModel.inertNoteInfo(newNoteInfo);
    }

    private void saveEditNote(String titleText, String descriptionText, String contentText) {
        Note note = new Note(titleText, descriptionText, contentText);
        NoteInfo noteInfo = notes.get(notesPosition);
        noteInfo.dateModified = Calendar.getInstance().getTime();

        note.dateCreated = noteInfo.dateCreated;
        noteViewModel.updateNote(note);
        noteViewModel.updateNoteInfo(noteInfo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST) {
            if (resultCode == RESULT_OK) {
                assert data != null;

                String titleText = data.getStringExtra(EditNoteActivity.REPLY_TITLE);
                String descriptionText = data.getStringExtra(EditNoteActivity.REPLY_DESCRIPTION);
                String contentText = data.getStringExtra(EditNoteActivity.REPLY_CONTENT);

                if (data.getBooleanExtra(EditNoteActivity.REPLY_EDITING, false)) {
                    saveEditNote(titleText, descriptionText, contentText);
                } else {
                    saveAddNote(titleText, descriptionText, contentText);
                }
            }
        }
    }
}
