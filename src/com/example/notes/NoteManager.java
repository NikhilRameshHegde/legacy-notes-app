// com-example-notes-NoteManager.java
package com.example.notes;

import java.sql.*;
import java.util.*;

public class NoteManager {
    private static final String URL = "jdbc:mysql://localhost:3306/notesdb";
    private static final String USER = "root";
    private static final String PASS = "your_password"; // CHANGE ME

    static {
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
        } catch (Exception e) {
            System.err.println("MySQL Driver not found: " + e);
            throw new RuntimeException("Driver load failed", e);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // === CREATE ===
    public static synchronized void addNote(Note note) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "INSERT INTO notes (title, content) VALUES (?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                note.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Create failed", e);
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
    }

    // === READ ONE ===
    public static Note getNote(long id) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT * FROM notes WHERE id = ?");
            ps.setLong(1, id);
            rs = ps.executeQuery();
            if (rs.next()) {
                Note n = new Note();
                n.setId(rs.getLong("id"));
                n.setTitle(rs.getString("title"));
                n.setContent(rs.getString("content"));
                return n;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Get failed", e);
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
        return null;
    }

    // === READ ALL ===
    public static Collection getAllNotes() {
        List list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT * FROM notes ORDER BY id");
            rs = ps.executeQuery();
            while (rs.next()) {
                Note n = new Note();
                n.setId(rs.getLong("id"));
                n.setTitle(rs.getString("title"));
                n.setContent(rs.getString("content"));
                list.add(n);
            }
        } catch (SQLException e) {
            throw new RuntimeException("List failed", e);
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
        return list;
    }
    
    // === UPDATE ===
    public static synchronized void updateNote(Note note) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("UPDATE notes SET title = ?, content = ? WHERE id = ?");
            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            ps.setLong(3, note.getId());
            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Note not found: " + note.getId());
            }
        } catch (SQLException e) {
            throw new RuntimeException("Update failed", e);
        } finally {
            close(ps);
            close(conn);
        }
    }
    
    // === DELETE ===
    public static synchronized void deleteNote(long id) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("DELETE FROM notes WHERE id = ?");
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Delete failed", e);
        } finally {
            close(ps);
            close(conn);
        }
    }

    // === NEW: SEARCH NOTES ===
    public static Collection searchNotes(String query) {
        List list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "SELECT * FROM notes WHERE title LIKE ? OR content LIKE ?";
            ps = conn.prepareStatement(sql);
            String searchPattern = "%" + query + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            rs = ps.executeQuery();
            while (rs.next()) {
                Note n = new Note();
                n.setId(rs.getLong("id"));
                n.setTitle(rs.getString("title"));
                n.setContent(rs.getString("content"));
                list.add(n);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Search failed", e);
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
        return list;
    }

    // === NEW: GET NOTE COUNT ===
    public static int getNoteCount() {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            ps = conn.prepareStatement("SELECT COUNT(*) FROM notes");
            rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Count failed", e);
        } finally {
            close(rs);
            close(ps);
            close(conn);
        }
        return 0;
    }

    // === NEW: BULK DELETE ===
    public static synchronized void deleteMultipleNotes(String[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        Connection conn = null;
        Statement stmt = null;
        try {
            conn = getConnection();
            // A common but unsafe legacy pattern your tool will need to handle
            StringBuffer sb = new StringBuffer("DELETE FROM notes WHERE id IN (");
            for (int i = 0; i < ids.length; i++) {
                sb.append("?");
                if (i < ids.length - 1) {
                    sb.append(",");
                }
            }
            sb.append(")");
            
            PreparedStatement ps = conn.prepareStatement(sb.toString());
            for (int i = 0; i < ids.length; i++) {
                ps.setLong(i + 1, Long.parseLong(ids[i]));
            }
            ps.executeUpdate();
            close(ps);
        } catch (Exception e) {
            throw new RuntimeException("Bulk delete failed", e);
        } finally {
            close(conn);
        }
    }
    
    // === HELPER: Safe close ===
    private static void close(Connection c) {
        if (c != null) try { c.close(); } catch (Exception ignore) {}
    }
    private static void close(Statement s) {
        if (s != null) try { s.close(); } catch (Exception ignore) {}
    }
    private static void close(ResultSet rs) {
        if (rs != null) try { rs.close(); } catch (Exception ignore) {}
    }
}