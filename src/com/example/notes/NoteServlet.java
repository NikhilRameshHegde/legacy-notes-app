package com.example.notes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

public class NoteServlet extends HttpServlet {

    // === GET ===
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");

        String action = req.getParameter("action");

        if ("list".equals(action)) {
            for (Note n : NoteManager.getAllNotes()) {
                out.println("ID: " + n.getId() + ", Title: " + n.getTitle());
            }
        } 
        else if ("get".equals(action)) {
            try {
                long id = Long.parseLong(req.getParameter("id"));
                Note note = NoteManager.getNote(id);
                if (note != null) {
                    out.println("Title: " + note.getTitle());
                    out.println("Content: " + note.getContent());
                } else {
                    out.println("Error: Note not found.");
                }
            } catch (Exception e) {
                out.println("Error: Invalid ID.");
            }
        }
        else {
            out.println("Legacy Notes DB App");
            out.println("?action=list");
            out.println("?action=get&id=1");
        }
    }

    // === POST ===
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");

        String action = req.getParameter("action");

        if ("create".equals(action)) {
            String title = req.getParameter("title");
            String content = req.getParameter("content");
            if (title == null || content == null) {
                out.println("Error: title and content required");
                return;
            }
            Note n = new Note(title, content);
            NoteManager.addNote(n);
            out.println("Success: Note created with ID " + n.getId());
        }
        else if ("update".equals(action)) {
            try {
                long id = Long.parseLong(req.getParameter("id"));
                String title = req.getParameter("title");
                String content = req.getParameter("content");

                Note note = NoteManager.getNote(id);
                if (note == null) {
                    out.println("Error: Note not found");
                    return;
                }
                if (title != null && title.trim().length() > 0) note.setTitle(title);
                if (content != null && content.trim().length() > 0) note.setContent(content);

                NoteManager.updateNote(note);
                out.println("Success: Note " + id + " updated");
            } catch (Exception e) {
                out.println("Error: Invalid input");
            }
        }
        else if ("delete".equals(action)) {
            try {
                long id = Long.parseLong(req.getParameter("id"));
                NoteManager.deleteNote(id);
                out.println("Success: Note " + id + " deleted");
            } catch (Exception e) {
                out.println("Error: Invalid ID");
            }
        }
        else {
            out.println("Error: Unknown action");
        }
    }
}