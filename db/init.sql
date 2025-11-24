CREATE DATABASE IF NOT EXISTS notesdb CHARACTER SET utf8;
USE notesdb;

CREATE TABLE IF NOT EXISTS notes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- More notes for better testing
INSERT INTO notes (title, content, created_at) 
VALUES ('First Note', 'This is the content of the first note.', NOW());
INSERT INTO notes (title, content, created_at) 
VALUES ('Second Note', 'This is a note about Java.', NOW());
INSERT INTO notes (title, content, created_at) 
VALUES ('Meeting Ideas', 'Discuss the new project architecture.', NOW());
INSERT INTO notes (title, content, created_at) 
VALUES ('Shopping List', 'Milk, bread, and Java coffee.', NOW());