// com.example.notes.NoteManager.java
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