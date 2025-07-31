-- Schema initialization for integration tests
-- This creates the basic structure that Hibernate would create, 
-- but ensures it exists before tests run

CREATE TABLE IF NOT EXISTS Course (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL UNIQUE,
    category VARCHAR(10) NOT NULL,
    status VARCHAR(8) NOT NULL DEFAULT 'Active'
);

CREATE TABLE IF NOT EXISTS Lesson (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    youtube_url VARCHAR(11) NOT NULL,
    course_id BIGINT NOT NULL,
    CONSTRAINT FK_lesson_course FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
);
