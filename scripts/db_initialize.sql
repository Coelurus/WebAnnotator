CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    user_name VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    team_id INT NOT NULL
);

CREATE TABLE teams (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    leader_id INT NOT NULL
);

CREATE TABLE projects (
    project_id SERIAL PRIMARY KEY,
    project_name VARCHAR(255) NOT NULL,
    log_file_name VARCHAR(255) NOT NULL,
    deadline DATE,
    priority INT,
    team_id INT NOT NULL
);

CREATE TABLE projecttag (
    project_id INT NOT NULL,
    tag_id INT NOT NULL
);

CREATE TABLE tags (
    tag_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE annotations (
    id  BIGSERIAL PRIMARY KEY,
    project_id BIGINT NOT NULL,
    frame_id BIGINT NOT NULL,
    label VARCHAR(255) NOT NULL
);



INSERT INTO users (first_name, last_name, user_name, password_hash, team_id) VALUES
('John', 'Doe', 'johndoe', 'hashedpassword1', 1),
('Jane', 'Smith', 'janesmith', 'hashedpassword2', 2),
('Alice', 'Johnson', 'alicej', 'hashedpassword3', 2);

INSERT INTO teams (name, leader_id) VALUES
('Fast', 1),
('Newcomers', 2);

INSERT INTO projects (project_name, log_file_name, deadline, priority, team_id) VALUES
('Project Alpha', 'alpha.log', '2024-12-01', 1, 1),
('Project Beta', 'beta.log', '2024-11-15', 2, 2),
('Project Gamma', 'gamma.log', '2024-10-30', 1, 2);

INSERT INTO tags (name) VALUES
('wawe'),
('spin'); 

INSERT INTO projecttag (project_id, tag_id) VALUES
(1, 1),
(2, 2),
(1, 2);