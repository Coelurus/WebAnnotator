INSERT INTO users (id, first_name, last_name, username, password_hash, team_id, role)
VALUES (1, 'Jane', 'Smith', 'user', '$2a$10$YWbD6Jr4lAOsBBUhNid.AuMok6d00GupeGv9DOAlgbPRJ27e0cHm.', 0, 'ROLE_USER'),
       (2, 'Alice', 'Johnson', 'user1', '$2a$10$EkVy/kM6x1uWWgpD4SiFN.9XmZXMJv37i.8RyExGL8AGblw7SuTFW', 0, 'ROLE_USER');
SELECT setval('users_id_seq', (SELECT MAX(id) + 1 FROM users), false);

INSERT INTO teams (id, name, leader_id)
VALUES (0, 'Fast', 1),
       (1, 'Newcomers', 0);
SELECT setval('teams_id_seq', (SELECT MAX(id) + 1 FROM teams), false);

INSERT INTO projects (id, project_name, log_file_name, deadline, priority, team_id)
VALUES (0, 'Demo', 'recording_20250522_230912', '2024-12-01', 'LOW', 1);
SELECT setval('projects_id_seq', (SELECT MAX(id) + 1 FROM projects), false);

INSERT INTO tags (id, name)
VALUES (0, 'wawe'),
       (1, 'spin');
SELECT setval('tags_id_seq', (SELECT MAX(id) + 1 FROM tags), false);

INSERT INTO labels (id, label, color)
VALUES (0, 'left right wawe', 'red'),
       (1, 'clock wise spin', 'blue');
SELECT setval('labels_id_seq', (SELECT MAX(id) + 1 FROM labels), false);

INSERT INTO project_tag (project_id, tag_id)
VALUES (1, 1),
       (2, 2),
       (1, 2);