USE seep_db;

-- New locations: WestEnd, Harbor, Hilltop, OldTown
INSERT INTO roads (source, destination, distance_km, status, hazard_level) VALUES
('DowntownSquare', 'WestEnd', 3.8, 'CLEAR', 'LOW'),
('WestEnd', 'Harbor', 5.5, 'CLEAR', 'LOW'),
('WestEnd', 'OldTown', 2.9, 'CONGESTED', 'MEDIUM'),
('OldTown', 'NorthGate', 4.0, 'CLEAR', 'LOW'),
('Harbor', 'Hilltop', 6.2, 'CLEAR', 'MEDIUM'),
('Hilltop', 'EastPark', 7.5, 'CLEAR', 'LOW'),
('OldTown', 'Riverside', 3.3, 'BLOCKED', 'HIGH');

INSERT INTO shelters (name, location, capacity, occupied) VALUES
('Westside Relief Center', 'WestEnd', 220, 30),
('Harbor View Shelter', 'Harbor', 160, 50),
('Hilltop Emergency Camp', 'Hilltop', 140, 10);