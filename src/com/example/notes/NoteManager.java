package com.example.notes;

import java.sql.*;
import java.util.*;
import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.dbcp.BasicDataSource; // Legacy Connection Pooling
import org.joda.time.DateTime;

public class NoteManager {
    // Legacy Log4j 1.x
    private static final Logger logger = Logger.getLogger(NoteManager.class);
    
    // Legacy DBCP Data Source
    private static BasicDataSource dataSource;

    static {
        try {
            dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl("jdbc:mysql://localhost:3306/notesdb");
            dataSource.setUsername("root");
            dataSource.setPassword("your_password"); // CHANGE ME
            dataSource.setInitialSize(5);
            
            logger.info("Database connection pool initialized.");
        } catch (Exception e) {
            logger.error("Failed to initialize DBCP pool", e);
            throw new RuntimeException("Init failed", e);
        }
    }

    private static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    // === CREATE ===
    public static synchronized void addNote(Note note) {
        // Legacy Commons Lang check
        if (StringUtils.isEmpty(note.getTitle())) {
            throw new IllegalArgumentException("Title cannot be empty");
        }

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            String sql = "INSERT INTO notes (title, content, created_at) VALUES (?, ?, ?)";
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            
            // Convert Joda DateTime to SQL Timestamp
            if (note.getCreatedAt() != null) {
                ps.setTimestamp(3, new Timestamp(note.getCreatedAt().getMillis()));
            } else {
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
            }
            
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                note.setId(rs.getLong(1));
            }
            logger.info("Note created: " + note.getId());
        } catch (SQLException e) {
            logger.error("Create failed", e);
            throw new RuntimeException("Create failed", e);
        } finally {
            close(rs); close(ps); close(conn);
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
                
                // Convert SQL Timestamp back to Joda DateTime
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    n.setCreatedAt(new DateTime(ts.getTime()));
                }
                return n;
            }
        } catch (SQLException e) {
            logger.error("Get failed for ID: " + id, e);
            throw new RuntimeException("Get failed", e);
        } finally {
            close(rs); close(ps); close(conn);
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
                
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    n.setCreatedAt(new DateTime(ts.getTime()));
                }
                list.add(n);
            }
        } catch (SQLException e) {
            logger.error("List failed", e);
            throw new RuntimeException("List failed", e);
        } finally {
            close(rs); close(ps); close(conn);
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
                logger.warn("Attempted to update non-existent note: " + note.getId());
                throw new RuntimeException("Note not found: " + note.getId());
            }
        } catch (SQLException e) {
            logger.error("Update failed", e);
            throw new RuntimeException("Update failed", e);
        } finally {
            close(ps); close(conn);
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
            logger.info("Deleted note: " + id);
        } catch (SQLException e) {
            logger.error("Delete failed", e);
            throw new RuntimeException("Delete failed", e);
        } finally {
            close(ps); close(conn);
        }
    }

    // === SEARCH NOTES ===
    public static Collection searchNotes(String query) {
        List list = new ArrayList();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = getConnection();
            // Using Commons Lang to check query
            if (StringUtils.isBlank(query)) {
                return list;
            }
            
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
                
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) n.setCreatedAt(new DateTime(ts.getTime()));
                
                list.add(n);
            }
        } catch (SQLException e) {
            logger.error("Search failed", e);
            throw new RuntimeException("Search failed", e);
        } finally {
            close(rs); close(ps); close(conn);
        }
        return list;
    }

    // === GET NOTE COUNT ===
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
            logger.error("Count failed", e);
            throw new RuntimeException("Count failed", e);
        } finally {
            close(rs); close(ps); close(conn);
        }
        return 0;
    }

    // === BULK DELETE ===
    public static synchronized void deleteMultipleNotes(String[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        Connection conn = null;
        try {
            conn = getConnection();
            // Legacy StringBuffer usage
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
            logger.info("Bulk deleted " + ids.length + " notes.");
        } catch (Exception e) {
            logger.error("Bulk delete failed", e);
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