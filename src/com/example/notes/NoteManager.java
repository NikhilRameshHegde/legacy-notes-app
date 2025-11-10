package com.example.notes;

import java.util.Hashtable;
import java.util.Collection;

/**
 * Manages the collection of notes in memory.
 * This is a singleton-style class.
 */
public class NoteManager {
    private static final Hashtable notes = new Hashtable();
    private static long nextId = 1;

    // Static initializer to add some dummy data
    static {
        Note note1 = new Note("First Note", "This is the content of the first note.");
        addNote(note1);
    }

    public static synchronized void addNote(Note note) {
        note.setId(nextId++);
        notes.put(new Long(note.getId()), note);
    }

    public static Note getNote(long id) {
        return (Note) notes.get(new Long(id));
    }
    
    public static Collection getAllNotes() {
        return notes.values();
    }
    
    public static synchronized void deleteNote(long id) {
        notes.remove(new Long(id));
    }
}