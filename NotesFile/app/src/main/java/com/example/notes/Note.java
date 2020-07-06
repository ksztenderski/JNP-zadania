package com.example.notes;

import java.util.Calendar;
import java.util.Date;

class Note {
    Date dateCreated;
    String title;
    String description;
    String content;

    Note(String title, String description, String content) {
        this.title = title;
        this.description = description;
        this.content = content;
        this.dateCreated = Calendar.getInstance().getTime();
    }
}
