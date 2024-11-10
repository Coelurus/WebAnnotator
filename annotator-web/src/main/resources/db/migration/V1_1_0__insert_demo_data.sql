INSERT INTO users (id, first_name, last_name, username, password_hash, team_id)
VALUES (0, 'John', 'Doe', 'johndoe', 'hashedpassword1', 1),
       (1, 'Jane', 'Smith', 'janesmith', 'hashedpassword2', 0),
       (2, 'Alice', 'Johnson', 'alicej', 'hashedpassword3', 0);

INSERT INTO teams (id, name, leader_id)
VALUES (0, 'Fast', 1),
       (1, 'Newcomers', 2);

INSERT INTO projects (id, project_name, log_file_name, deadline, priority, team_id)
VALUES (0, 'Project Alpha', 'alpha.log', '2024-12-01', 1, 1),
       (1, 'Project Beta', 'beta.log', '2024-11-15', 2, 0),
       (2, 'Project Gamma', 'gamma.log', '2024-10-30', 1, 0);

INSERT INTO tags (id, name)
VALUES (0, 'wawe'),
       (1, 'spin');

INSERT INTO labels (id, label, color)
VALUES (0, 'left right wawe', 'red'),
       (1, 'clock wise spin', 'blue');

INSERT INTO project_tag (project_id, tag_id)
VALUES (1, 1),
       (2, 2),
       (1, 2);