package com.example.notes;

import java.util.Date;

public class NoteInfo {
    Date dateCreated;
    Date dateModified;
    String title;

    public NoteInfo(Date dateCreated, String title) {
        this.dateCreated = dateCreated;
        this.dateModified = dateCreated;
        this.title = title;
    }
}
