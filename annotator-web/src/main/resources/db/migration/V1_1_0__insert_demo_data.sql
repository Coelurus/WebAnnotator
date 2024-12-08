INSERT INTO users (id, first_name, last_name, username, password_hash, team_id)
VALUES (0, 'John', 'Doe', 'johndoe', 'hashedpassword1', 1),
       (1, 'Jane', 'Smith', 'janesmith', 'hashedpassword2', 0),
       (2, 'Alice', 'Johnson', 'alicej', 'hashedpassword3', 0);
ALTER TABLE users ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM users);

INSERT INTO teams (id, name, leader_id)
VALUES (0, 'Fast', 1),
       (1, 'Newcomers', 2);
ALTER TABLE teams ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM teams);

INSERT INTO projects (id, project_name, log_file_name, deadline, priority, team_id)
VALUES (0, 'Project Alpha', 'gestic_20240925_083111', '2024-12-01', 1, 1),
       (1, 'Project Beta', 'gestic_20240925_084203', '2024-11-15', 2, 0),
       (2, 'Project Gamma', 'gestic_20240925_093532', '2024-10-30', 1, 0);
ALTER TABLE projects ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM projects);

INSERT INTO tags (id, name)
VALUES (0, 'wawe'),
       (1, 'spin');
ALTER TABLE tags ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM tags);

INSERT INTO labels (id, label, color)
VALUES (0, 'left right wawe', 'red'),
       (1, 'clock wise spin', 'blue');
ALTER TABLE labels ALTER COLUMN id RESTART WITH (SELECT MAX(id) + 1 FROM labels);

INSERT INTO project_tag (project_id, tag_id)
VALUES (1, 1),
       (2, 2),
       (1, 2);