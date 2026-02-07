-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create students table
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    roll_number VARCHAR(255) NOT NULL UNIQUE,
    section VARCHAR(255) NOT NULL,
    hackerrank_profile VARCHAR(255),
    leetcode_profile VARCHAR(255),
    problem_count INTEGER DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Create problems table
CREATE TABLE problems (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    platform VARCHAR(100) NOT NULL,
    difficulty VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    solved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    student_id BIGINT NOT NULL,
    FOREIGN KEY (student_id) REFERENCES students(id) ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_roll_number ON students(roll_number);
CREATE INDEX idx_students_section ON students(section);
CREATE INDEX idx_problems_student_id ON problems(student_id);
CREATE INDEX idx_problems_platform ON problems(platform);
CREATE INDEX idx_problems_difficulty ON problems(difficulty);
CREATE INDEX idx_problems_status ON problems(status);
CREATE INDEX idx_users_email ON users(email);
