INSERT INTO users (id, first_name, last_name, username, password_hash, team_id, role)
VALUES (0, 'Admin', 'Admin', 'admin', '$2a$10$btgrXn5reBv5nblarOU3leWnfZuyJAuKFwhliXL8S9S5yspfdIzka', null, 'ROLE_ADMIN');
ALTER TABLE users ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM users);