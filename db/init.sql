CREATE DATABASE IF NOT EXISTS notesdb CHARACTER SET utf8;
USE notesdb;

CREATE TABLE IF NOT EXISTS notes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT
);

-- More notes for better testing
INSERT INTO notes (title, content) 
VALUES ('First Note', 'This is the content of the first note.');
INSERT INTO notes (title, content) 
VALUES ('Second Note', 'This is a note about Java.');
INSERT INTO notes (title, content) 
VALUES ('Meeting Ideas', 'Discuss the new project architecture.');
INSERT INTO notes (title, content) 
VALUES ('Shopping List', 'Milk, bread, and Java coffee.');