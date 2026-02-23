-- REVPLAY DATABASE SCHEMA (ORACLE)

-- 1. DROP TABLES (If they exist)
-- DROP TABLE listening_history;
-- DROP TABLE playlist_songs;
-- DROP TABLE playlists;
-- DROP TABLE favorites;
-- DROP TABLE songs;
-- DROP TABLE albums;
-- DROP TABLE artists;
-- DROP TABLE users;

-- 2. CREATE TABLES
CREATE TABLE users (
    user_id NUMBER PRIMARY KEY,
    email VARCHAR2(150) UNIQUE NOT NULL,
    username VARCHAR2(100) UNIQUE NOT NULL,
    password VARCHAR2(255) NOT NULL,
    role VARCHAR2(20) CHECK (role IN ('USER','ARTIST','ADMIN')),
    display_name VARCHAR2(150),
    bio VARCHAR2(500),
    profile_image BLOB,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE artists (
    artist_id NUMBER PRIMARY KEY,
    user_id NUMBER UNIQUE,
    artist_name VARCHAR2(150) NOT NULL,
    genre VARCHAR2(100),
    banner_image BLOB,
    instagram VARCHAR2(200),
    twitter VARCHAR2(200),
    youtube VARCHAR2(200),
    website VARCHAR2(200),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

CREATE TABLE albums (
    album_id NUMBER PRIMARY KEY,
    artist_id NUMBER,
    album_name VARCHAR2(150) NOT NULL,
    description VARCHAR2(500),
    release_date DATE,
    cover_image BLOB,
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id) ON DELETE CASCADE
);

CREATE TABLE songs (
    song_id NUMBER PRIMARY KEY,
    artist_id NUMBER,
    album_id NUMBER,
    title VARCHAR2(150) NOT NULL,
    genre VARCHAR2(100),
    duration NUMBER,
    audio_file BLOB,
    play_count NUMBER DEFAULT 0,
    visibility VARCHAR2(20) CHECK (visibility IN ('PUBLIC','UNLISTED')),
    release_date DATE,
    FOREIGN KEY (artist_id) REFERENCES artists(artist_id),
    FOREIGN KEY (album_id) REFERENCES albums(album_id)
);

CREATE TABLE favorites (
    favorite_id NUMBER PRIMARY KEY,
    user_id NUMBER,
    song_id NUMBER,
    added_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (song_id) REFERENCES songs(song_id)
);

CREATE TABLE playlists (
    playlist_id NUMBER PRIMARY KEY,
    user_id NUMBER,
    name VARCHAR2(150),
    description VARCHAR2(500),
    privacy VARCHAR2(20) CHECK (privacy IN ('PUBLIC','PRIVATE')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE playlist_songs (
    id NUMBER PRIMARY KEY,
    playlist_id NUMBER,
    song_id NUMBER,
    position NUMBER,
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id),
    FOREIGN KEY (song_id) REFERENCES songs(song_id)
);

CREATE TABLE listening_history (
    history_id NUMBER PRIMARY KEY,
    user_id NUMBER,
    song_id NUMBER,
    listened_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    FOREIGN KEY (song_id) REFERENCES songs(song_id)
);

-- 3. CREATE SEQUENCES
CREATE SEQUENCE user_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE artist_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE album_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE song_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE favorites_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE playlist_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE playlist_songs_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE history_seq START WITH 1 INCREMENT BY 1;

-- 4. CREATE TRIGGERS (Auto-Increment Simulation)
CREATE OR REPLACE TRIGGER user_bir 
BEFORE INSERT ON users 
FOR EACH ROW 
WHEN (NEW.user_id IS NULL)
BEGIN 
  SELECT user_seq.NEXTVAL INTO :NEW.user_id FROM DUAL; 
END;
/

CREATE OR REPLACE TRIGGER artist_bir 
BEFORE INSERT ON artists 
FOR EACH ROW 
WHEN (NEW.artist_id IS NULL)
BEGIN 
  SELECT artist_seq.NEXTVAL INTO :NEW.artist_id FROM DUAL; 
END;
/

CREATE OR REPLACE TRIGGER album_bir 
BEFORE INSERT ON albums 
FOR EACH ROW 
WHEN (NEW.album_id IS NULL)
BEGIN 
  SELECT album_seq.NEXTVAL INTO :NEW.album_id FROM DUAL; 
END;
/

CREATE OR REPLACE TRIGGER song_bir 
BEFORE INSERT ON songs 
FOR EACH ROW 
WHEN (NEW.song_id IS NULL)
BEGIN 
  SELECT song_seq.NEXTVAL INTO :NEW.song_id FROM DUAL; 
END;
/

CREATE OR REPLACE TRIGGER favorites_bir 
BEFORE INSERT ON favorites 
FOR EACH ROW 
WHEN (NEW.favorite_id IS NULL)
BEGIN 
  SELECT favorites_seq.NEXTVAL INTO :NEW.favorite_id FROM DUAL; 
END;
/

CREATE OR REPLACE TRIGGER playlist_bir 
BEFORE INSERT ON playlists 
FOR EACH ROW 
WHEN (NEW.playlist_id IS NULL)
BEGIN 
  SELECT playlist_seq.NEXTVAL INTO :NEW.playlist_id FROM DUAL; 
END;
/

CREATE OR REPLACE TRIGGER playlist_songs_bir 
BEFORE INSERT ON playlist_songs 
FOR EACH ROW 
WHEN (NEW.id IS NULL)
BEGIN 
  SELECT playlist_songs_seq.NEXTVAL INTO :NEW.id FROM DUAL; 
END;
/

CREATE OR REPLACE TRIGGER history_bir 
BEFORE INSERT ON listening_history 
FOR EACH ROW 
WHEN (NEW.history_id IS NULL)
BEGIN 
  SELECT history_seq.NEXTVAL INTO :NEW.history_id FROM DUAL; 
END;
/

-- 5. PL/SQL CONCEPTS
-- Procedure to increment play count
CREATE OR REPLACE PROCEDURE increment_play_count(p_song_id IN NUMBER) AS
BEGIN
    UPDATE songs SET play_count = play_count + 1 WHERE song_id = p_song_id;
    COMMIT;
END;
/

-- Function to calculate artist total plays
CREATE OR REPLACE FUNCTION get_artist_total_plays(p_artist_id IN NUMBER) RETURN NUMBER AS
    v_total NUMBER;
BEGIN
    SELECT SUM(play_count) INTO v_total FROM songs WHERE artist_id = p_artist_id;
    RETURN NVL(v_total, 0);
END;
/

-- 6. DUMMY DATA FOR TESTING
-- (Optional: Add common dummy data)
INSERT INTO users (email, username, password, role, display_name) VALUES ('admin@revplay.com', 'admin', 'password', 'ADMIN', 'System Admin');
INSERT INTO users (email, username, password, role, display_name) VALUES ('artist1@revplay.com', 'artist1', 'password', 'ARTIST', 'John Doe');
INSERT INTO users (email, username, password, role, display_name) VALUES ('user1@revplay.com', 'user1', 'password', 'USER', 'Jane Smith');

INSERT INTO artists (user_id, artist_name, genre) VALUES (2, 'John Doe', 'Pop');

INSERT INTO albums (artist_id, album_name, release_date) VALUES (1, 'First Tracks', SYSDATE);

INSERT INTO songs (artist_id, album_id, title, genre, duration, play_count, visibility, release_date) 
VALUES (1, 1, 'Melody 1', 'Pop', 180, 10, 'PUBLIC', SYSDATE);

COMMIT;
