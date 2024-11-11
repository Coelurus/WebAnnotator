CREATE TABLE IF NOT EXISTS projects
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_name  VARCHAR(255)                                       NOT NULL,
    log_file_name VARCHAR(255)                                       NOT NULL,
    deadline      DATE,
    priority      ENUM ('NONE', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL') NOT NULL,
    team_id       INT                                                NOT NULL
);

CREATE TABLE IF NOT EXISTS tags
(
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS project_tag
(
    project_id BIGINT NOT NULL,
    tag_id     BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS annotations
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT       NOT NULL,
    frame_id   BIGINT       NOT NULL,
    label      VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS labels
(
    id    BIGINT AUTO_INCREMENT PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    color VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name    VARCHAR(255) NOT NULL,
    last_name     VARCHAR(255) NOT NULL,
    username      VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    team_id       BIGINT       NOT NULL
);

CREATE TABLE IF NOT EXISTS teams
(
    id        BIGINT AUTO_INCREMENT PRIMARY KEY,
    name      VARCHAR(255) NOT NULL UNIQUE,
    leader_id BIGINT       NOT NULL
);
