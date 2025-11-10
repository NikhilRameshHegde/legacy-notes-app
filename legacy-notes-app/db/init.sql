CREATE DATABASE IF NOT EXISTS notesdb CHARACTER SET utf8;
USE notesdb;

CREATE TABLE IF NOT EXISTS notes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    content TEXT
);

INSERT INTO notes (title, content) 
VALUES ('First Note', 'This is the content of the first note.');