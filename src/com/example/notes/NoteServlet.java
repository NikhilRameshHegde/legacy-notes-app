package com.example.notes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

public class NoteServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");

        String action = request.getParameter("action");

        if ("list".equals(action)) {
            // List all notes
            Iterator it = NoteManager.getAllNotes().iterator();
            while (it.hasNext()) {
                Note note = (Note) it.next();
                out.println("ID: " + note.getId() + ", Title: " + note.getTitle());
            }
        } else if ("get".equals(action)) {
            // Get a single note by ID
            try {
                long id = Long.parseLong(request.getParameter("id"));
                Note note = NoteManager.getNote(id);
                if (note != null) {
                    out.println("Title: " + note.getTitle());
                    out.println("Content: " + note.getContent());
                } else {
                    out.println("Error: Note not found.");
                }
            } catch (Exception e) {
                out.println("Error: Invalid ID format.");
            }
        } else {
            out.println("Welcome to the Legacy Notes App! Use ?action=list or ?action=get&id=1");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        response.setContentType("text/plain");
        
        String action = request.getParameter("action");

        if ("create".equals(action)) {
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            Note newNote = new Note(title, content);
            NoteManager.addNote(newNote);
            out.println("Success: Note created with ID " + newNote.getId());
        } else if ("delete".equals(action)) {
            try {
                long id = Long.parseLong(request.getParameter("id"));
                NoteManager.deleteNote(id);
                out.println("Success: Note with ID " + id + " deleted.");
            } catch (Exception e) {
                out.println("Error: Invalid ID format.");
            }
        } else {
            out.println("Error: Invalid POST action. Use 'create' or 'delete'.");
        }
    }
}