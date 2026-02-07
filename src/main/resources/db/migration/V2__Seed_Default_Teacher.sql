-- Insert default teacher account
-- Password: teacher123 (BCrypt hashed)
INSERT INTO users (email, password, role, created_at)
VALUES ('teacher@algomentor.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'TEACHER', CURRENT_TIMESTAMP)
ON CONFLICT (email) DO NOTHING;
