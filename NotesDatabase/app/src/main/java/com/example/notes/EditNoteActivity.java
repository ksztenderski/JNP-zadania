package com.example.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class EditNoteActivity extends AppCompatActivity {
    private EditText title;
    private EditText description;
    private EditText content;
    private boolean editing;

    public static final String REPLY_TITLE = "com.example.notes.reply_title";
    public static final String REPLY_DESCRIPTION = "com.example.notes.reply_description";
    public static final String REPLY_CONTENT = "com.example.notes.reply_content";
    public static final String REPLY_EDITING = "com.example.notes.reply_editing";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        title = findViewById(R.id.edit_title);
        description = findViewById(R.id.edit_description);
        content = findViewById(R.id.edit_content);

        Intent intent = getIntent();
        title.setText(intent.getStringExtra(MainActivity.EDIT_ADD_TITLE));
        description.setText(intent.getStringExtra(MainActivity.EDIT_ADD_DESCRIPTION));
        content.setText(intent.getStringExtra(MainActivity.EDIT_ADD_CONTENT));
        editing = intent.getBooleanExtra(MainActivity.EDIT_ADD_EDITING, false);
    }

    public void saveNoteListener(View view) {
        Intent intent = new Intent();
        intent.putExtra(REPLY_TITLE, title.getText().toString());
        intent.putExtra(REPLY_DESCRIPTION, description.getText().toString());
        intent.putExtra(REPLY_CONTENT, content.getText().toString());
        intent.putExtra(REPLY_EDITING, editing);

        setResult(RESULT_OK, intent);
        finish();
    }
}