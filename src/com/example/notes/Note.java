package com.example.notes;

/**
 * A simple data object representing a single note.
 */
public class Note {
    private long id;
    private String title;
    private String content;

    // Default constructor
    public Note() {}

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
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

    public String toString() {
        return "Note[id=" + id + ", title=" + title + "]";
    }
}