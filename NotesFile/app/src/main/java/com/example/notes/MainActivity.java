package com.example.notes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private LinkedList<NoteInfo> notes;
    private TextView title;
    private TextView description;
    private TextView content;
    private int notesPosition = 0;
    private Gson gson;

    Type notesListType = new TypeToken<LinkedList<NoteInfo>>() {
    }.getType();

    public static final int REQUEST = 1;
    public static final String EDIT_ADD_TITLE = "com.example.notes.edit_add_title";
    public static final String EDIT_ADD_DESCRIPTION = "com.example.notes.edit_add_description";
    public static final String EDIT_ADD_CONTENT = "com.example.notes.edit_add_content";
    public static final String EDIT_ADD_EDITING = "com.example.notes.edit_add_editing";

    private void writeToFile(String filename, String data, Context context) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile(String filename, Context context) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            return ret;
        } catch (IOException e) {
            Log.e("Note", "Cannot read file: " + e.toString());
        }

        return ret;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gson = new Gson();
        String notesListString = readFromFile("notes_list.txt", this);

        notes = gson.fromJson(notesListString, notesListType);
        if (notes == null) {
            notes = new LinkedList<>();
        }

        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        content = findViewById(R.id.content);

        if (!notes.isEmpty()) {
            updateNote(notes.getFirst());
        }
    }

    private void updateNote(NoteInfo noteInfo) {
        String noteJSON = readFromFile("notes_" + noteInfo.dateCreated.toString() + ".txt", this);
        Note note = gson.fromJson(noteJSON, Note.class);
        title.setText(note.title);
        description.setText(note.description);
        content.setText(note.content);
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
        if (notes.isEmpty()) {
            notes.add(newNoteInfo);
        } else {
            notes.add(++notesPosition, newNoteInfo);
        }

        writeToFile("notes_" + newNoteInfo.dateCreated.toString() + ".txt", gson.toJson(newNote, Note.class), this);
        writeToFile("notes_list.txt", gson.toJson(notes, notesListType), this);

        updateNote(notes.get(notesPosition));
    }

    private void saveEditNote(String titleText, String descriptionText, String contentText) {
        Note newNote = new Note(titleText, descriptionText, contentText);
        NoteInfo noteInfo = notes.get(notesPosition);
        noteInfo.dateModified = Calendar.getInstance().getTime();

        writeToFile("notes_" + noteInfo.dateCreated.toString() + ".txt", gson.toJson(newNote, Note.class), this);
        writeToFile("notes_list.txt", gson.toJson(notes, notesListType), this);

        updateNote(notes.get(notesPosition));
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
