package com.example.notes;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;
import com.thoughtworks.xstream.XStream; // Legacy XML serialization

public class NoteServlet extends HttpServlet {

    // === GET ===
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        PrintWriter out = resp.getWriter();
        String action = req.getParameter("action");

        if ("list".equals(action)) {
            resp.setContentType("text/plain");
            Iterator it = NoteManager.getAllNotes().iterator();
            while (it.hasNext()) {
                Note n = (Note) it.next();
                out.println("ID: " + n.getId() + ", Title: " + n.getTitle() + ", Date: " + n.getCreatedAt());
            }
        } 
        else if ("get".equals(action)) {
            resp.setContentType("text/plain");
            try {
                long id = Long.parseLong(req.getParameter("id"));
                Note note = NoteManager.getNote(id);
                if (note != null) {
                    out.println("Title: " + note.getTitle());
                    out.println("Content: " + note.getContent());
                    out.println("Created: " + note.getCreatedAt());
                } else {
                    out.println("Error: Note not found.");
                }
            } catch (Exception e) {
                out.println("Error: Invalid ID.");
            }
        }
        else if ("search".equals(action)) {
            resp.setContentType("text/plain");
            String query = req.getParameter("query");
            Iterator it = NoteManager.searchNotes(query).iterator();
            while(it.hasNext()){
                Note n = (Note) it.next();
                 out.println("ID: " + n.getId() + ", Title: " + n.getTitle());
            }
        }
        else if ("stats".equals(action)) {
            resp.setContentType("text/plain");
            int count = NoteManager.getNoteCount();
            out.println("Total notes: " + count);
        }
        // === NEW: Legacy XStream usage ===
        else if ("export_xml".equals(action)) {
            resp.setContentType("application/xml");
            Collection notes = NoteManager.getAllNotes();
            
            // This should be migrated to Jackson XML by your tool
            XStream xstream = new XStream();
            xstream.alias("note", Note.class);
            String xml = xstream.toXML(notes);
            
            out.println(xml);
        }
        else {
            resp.setContentType("text/plain");
            out.println("Legacy Notes DB App");
            out.println("?action=list");
            out.println("?action=get&id=1");
            out.println("?action=export_xml (Test XStream)");
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
                if (note != null) {
                    if (title != null) note.setTitle(title);
                    if (content != null) note.setContent(content);
                    NoteManager.updateNote(note);
                    out.println("Success: Note " + id + " updated");
                }
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
        else if ("bulk_delete".equals(action)) {
            String idsParam = req.getParameter("ids");
            try {
                String[] ids = idsParam.split(",");
                NoteManager.deleteMultipleNotes(ids);
                out.println("Success: Notes deleted.");
            } catch (Exception e) {
                 out.println("Error: Invalid IDs provided.");
            }
        }
        else {
            out.println("Error: Unknown POST action");
        }
    }
}