package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private LinkedList<Note> notes;
    private EditText titleEditText;
    private EditText descriptionEditText;
    private EditText contentEditText;
    private int notesPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        notes = new LinkedList<>();
        populateNotes();

        titleEditText = findViewById(R.id.edit_title);
        descriptionEditText = findViewById(R.id.edit_description);
        contentEditText = findViewById(R.id.edit_content);

        titleEditText.setText(notes.getFirst().title);
        descriptionEditText.setText(notes.getFirst().description);
        contentEditText.setText(notes.getFirst().content);

        final Button previousButton = findViewById(R.id.previous_button);
        final Button nextButton = findViewById(R.id.next_button);

        previousButton.setOnClickListener(view -> {
            saveNote();

            if (notesPosition == 0) {
                notes.addFirst(new Note());
                setEmptyNote();
            } else {
                updateNote(notes.get(--notesPosition));
            }
        });

        nextButton.setOnClickListener(view -> {
            if (!saveNote()) {
                if (notesPosition > 0) {
                    --notesPosition;
                }
            }

            if (notes.size() == 0 || notesPosition == notes.size() - 1) {
                notes.addLast(new Note());
                ++notesPosition;
                setEmptyNote();
            } else {
                updateNote(notes.get(++notesPosition));
            }
        });
    }

    private void populateNotes() {
        for (int i = 0; i < 5; ++i) {
            notes.add(new Note("Title " + i, "Description " + i, "Content " + i));
        }
    }

    private void updateNote(Note note) {
        titleEditText.setText(note.title);
        descriptionEditText.setText(note.description);
        contentEditText.setText(note.content);
    }

    private void setEmptyNote() {
        titleEditText.setText("");
        descriptionEditText.setText("");
        contentEditText.setText("");
    }

    private boolean saveNote() {
        if (titleEditText.getText().toString().equals("") &&
                descriptionEditText.getText().toString().equals("") &&
                contentEditText.getText().toString().equals("")) {
            notes.remove(notesPosition);

            return false;
        }

        notes.get(notesPosition).title = titleEditText.getText().toString();
        notes.get(notesPosition).description = descriptionEditText.getText().toString();
        notes.get(notesPosition).content = contentEditText.getText().toString();

        return true;
    }
}
