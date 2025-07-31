-- Initialize test database schema
-- This script creates the basic tables needed for integration tests

CREATE TABLE IF NOT EXISTS Course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    category VARCHAR(10) NOT NULL,
    status VARCHAR(8) NOT NULL DEFAULT 'Active',
    UNIQUE KEY unique_course_name (name)
);

CREATE TABLE IF NOT EXISTS Lesson (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    youtube_url VARCHAR(11) NOT NULL,
    course_id BIGINT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
);
