-- ============================================================
-- Smart Emergency Evacuation Planner (SEEP) - Database Schema
-- ============================================================

DROP DATABASE IF EXISTS seep_db;
CREATE DATABASE seep_db;
USE seep_db;

-- ---------------------------------------------------------------
-- Users table (stores both citizens and administrators)
-- ---------------------------------------------------------------
CREATE TABLE users (
    user_id      INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(255) NOT NULL,
    full_name    VARCHAR(100) NOT NULL,
    role         VARCHAR(10)  NOT NULL DEFAULT 'USER',   -- 'USER' or 'ADMIN'
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- Shelters table
-- ---------------------------------------------------------------
CREATE TABLE shelters (
    shelter_id   INT AUTO_INCREMENT PRIMARY KEY,
    name         VARCHAR(100) NOT NULL,
    location     VARCHAR(100) NOT NULL,   -- also used as a graph node name
    capacity     INT NOT NULL,
    occupied     INT NOT NULL DEFAULT 0
);

-- ---------------------------------------------------------------
-- Roads table (edges of the evacuation graph)
-- ---------------------------------------------------------------
CREATE TABLE roads (
    road_id       INT AUTO_INCREMENT PRIMARY KEY,
    source        VARCHAR(100) NOT NULL,
    destination   VARCHAR(100) NOT NULL,
    distance_km   DOUBLE NOT NULL,
    status        VARCHAR(15) NOT NULL DEFAULT 'CLEAR',   -- CLEAR, CONGESTED, BLOCKED
    hazard_level  VARCHAR(10) NOT NULL DEFAULT 'LOW'      -- LOW, MEDIUM, HIGH
);

-- ---------------------------------------------------------------
-- Alerts table
-- ---------------------------------------------------------------
CREATE TABLE alerts (
    alert_id     INT AUTO_INCREMENT PRIMARY KEY,
    message      VARCHAR(255) NOT NULL,
    severity     VARCHAR(10) NOT NULL DEFAULT 'MEDIUM',   -- LOW, MEDIUM, HIGH
    created_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ---------------------------------------------------------------
-- Seed data
-- ---------------------------------------------------------------

-- Default admin login -> username: admin / password: admin123
INSERT INTO users (username, password, full_name, role) VALUES
('admin', 'admin123', 'System Administrator', 'ADMIN'),
('citizen1', 'pass123', 'Nishanthini S', 'USER'),
('citizen2', 'pass123', 'Bhavya S', 'USER');

INSERT INTO shelters (name, location, capacity, occupied) VALUES
('City Community Hall', 'DowntownSquare', 200, 40),
('North Zone School', 'NorthGate', 150, 60),
('Riverside Sports Complex', 'Riverside', 300, 120),
('East Zone College', 'EastPark', 180, 20);

INSERT INTO roads (source, destination, distance_km, status, hazard_level) VALUES
('DowntownSquare', 'NorthGate', 4.5, 'CLEAR', 'LOW'),
('DowntownSquare', 'Riverside', 6.0, 'CONGESTED', 'MEDIUM'),
('DowntownSquare', 'EastPark', 3.2, 'CLEAR', 'LOW'),
('NorthGate', 'Riverside', 5.1, 'BLOCKED', 'HIGH'),
('NorthGate', 'EastPark', 7.0, 'CLEAR', 'LOW'),
('Riverside', 'EastPark', 2.8, 'CLEAR', 'MEDIUM'),
('EastPark', 'Riverside', 2.8, 'CLEAR', 'MEDIUM');

INSERT INTO alerts (message, severity) VALUES
('Flood warning issued for Riverside zone. Avoid low-lying roads.', 'HIGH'),
('Road near NorthGate blocked due to fallen debris.', 'MEDIUM');
