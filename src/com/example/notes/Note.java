package com.example.notes;

import org.joda.time.DateTime;

/**
 * A simple data object representing a single note.
 */
public class Note {
    private long id;
    private String title;
    private String content;
    private DateTime createdAt; // Uses Legacy Joda-Time

    // Default constructor
    public Note() {}

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
        this.createdAt = new DateTime(); // Defaults to now
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String toString() {
        return "Note[id=" + id + ", title=" + title + ", created=" + createdAt + "]";
    }
}