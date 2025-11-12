package com.example.notes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

public class NoteServlet extends HttpServlet {

    // === GET ===
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        resp.setContentType("text/plain");

        String action = req.getParameter("action");

        if ("list".equals(action)) {
            Iterator it = NoteManager.getAllNotes().iterator();
            while (it.hasNext()) {
                Note n = (Note) it.next();
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
        // NEW: Search endpoint
        else if ("search".equals(action)) {
            String query = req.getParameter("query");
            if (query == null || query.trim().length() == 0) {
                out.println("Error: search query required.");
                return;
            }
            out.println("Search Results for: '" + query + "'");
            Iterator it = NoteManager.searchNotes(query).iterator();
            while(it.hasNext()){
                Note n = (Note) it.next();
                 out.println("ID: " + n.getId() + ", Title: " + n.getTitle());
            }
        }
        // NEW: Stats endpoint
        else if ("stats".equals(action)) {
            int count = NoteManager.getNoteCount();
            out.println("Total notes: " + count);
        }
        else {
            out.println("Legacy Notes DB App");
            out.println("?action=list");
            out.println("?action=get&id=1");
            out.println("?action=search&query=java");
            out.println("?action=stats");
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
        // NEW: Bulk delete endpoint
        else if ("bulk_delete".equals(action)) {
            String idsParam = req.getParameter("ids"); // e.g., "1,3,4"
            if (idsParam == null || idsParam.trim().length() == 0) {
                out.println("Error: 'ids' parameter is required.");
                return;
            }
            try {
                String[] ids = idsParam.split(",");
                NoteManager.deleteMultipleNotes(ids);
                out.println("Success: Notes with IDs " + idsParam + " deleted.");
            } catch (Exception e) {
                 out.println("Error: Invalid IDs provided.");
            }
        }
        else {
            out.println("Error: Unknown POST action");
        }
    }
}