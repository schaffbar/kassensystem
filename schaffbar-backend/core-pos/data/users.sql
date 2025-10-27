-- create random data for testing
INSERT INTO schaffbar.customer
    (id, first_name, last_name, date_of_birth, club_member, email, phone, address_line1, address_line2, postal_code, city, country, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Max', 'Mustermann', '1985-03-12', TRUE, 'max.mustermann@example.com', '+4915112345678', 'Musterstraße 1', 'EG', '10115', 'Berlin', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Erika', 'Musterfrau', '1990-07-25', FALSE, 'erika.musterfrau@example.com', '+4915223456789', 'Beispielweg 23', NULL, '80331', 'München', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Hans', 'Schmidt', '1978-11-03', TRUE, 'hans.schmidt@example.com', '+4915334567890', 'Hauptstraße 45', NULL, '50667', 'Köln', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Julia', 'Schneider', '1995-05-18', FALSE, 'julia.schneider@example.com', '+4915445678901', 'Bahnhofstraße 12', NULL, '20095', 'Hamburg', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Peter', 'Fischer', '1982-09-30', TRUE, 'peter.fischer@example.com', '+4915556789012', 'Lindenweg 7', NULL, '04109', 'Leipzig', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Anna', 'Weber', '1988-02-14', FALSE, 'anna.weber@example.com', '+4915667890123', 'Gartenstraße 3', NULL, '70173', 'Stuttgart', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Lukas', 'Meyer', '1992-12-05', TRUE, 'lukas.meyer@example.com', '+4915778901234', 'Kirchplatz 9', NULL, '90402', 'Nürnberg', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Sophie', 'Wagner', '1986-06-21', FALSE, 'sophie.wagner@example.com', '+4915889012345', 'Schulstraße 5', NULL, '01067', 'Dresden', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Paul', 'Becker', '1993-08-09', TRUE, 'paul.becker@example.com', '+4915990123456', 'Ringstraße 8', NULL, '28195', 'Bremen', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Laura', 'Hoffmann', '1981-04-27', FALSE, 'laura.hoffmann@example.com', '+4916012345678', 'Marktplatz 2', NULL, '99084', 'Erfurt', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Tim', 'Keller', '1989-10-16', TRUE, 'tim.keller@example.com', '+4916112345678', 'Seestraße 11', NULL, '24103', 'Kiel', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Mia', 'Krüger', '1996-01-22', FALSE, 'mia.krueger@example.com', '+4916212345678', 'Parkallee 17', NULL, '28209', 'Bremen', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Felix', 'Zimmermann', '1984-07-13', TRUE, 'felix.zimmermann@example.com', '+4916312345678', 'Waldweg 4', NULL, '37073', 'Göttingen', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Lea', 'Hartmann', '1991-03-29', FALSE, 'lea.hartmann@example.com', '+4916412345678', 'Feldstraße 22', NULL, '34117', 'Kassel', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Jonas', 'Schulz', '1987-12-19', TRUE, 'jonas.schulz@example.com', '+4916512345678', 'Bergstraße 6', NULL, '55116', 'Mainz', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Marie', 'Wolf', '1994-05-02', FALSE, 'marie.wolf@example.com', '+4916612345678', 'Rosenweg 13', NULL, '89073', 'Ulm', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'David', 'Neumann', '1983-09-07', TRUE, 'david.neumann@example.com', '+4916712345678', 'Am Markt 10', NULL, '99084', 'Erfurt', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Lena', 'Schmitt', '1997-11-11', FALSE, 'lena.schmitt@example.com', '+4916812345678', 'Dorfstraße 8', NULL, '93047', 'Regensburg', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Jan', 'Bauer', '1980-06-04', TRUE, 'jan.bauer@example.com', '+4916912345678', 'Industriestraße 15', NULL, '66111', 'Saarbrücken', 'Deutschland', NOW(), NOW()),
    (gen_random_uuid(), 'Carolin', 'Bergmann', '1991-06-17', FALSE, 'carolin.bergmann@example.com', '+4918012345678', 'Wilhelmstraße 17', NULL, '34117', 'Kassel', 'Deutschland', NOW(), NOW());

-- Insert tools
INSERT INTO schaffbar.tool --
    (id, name, description, ip_address, http_start_command, on_command, off_command, created_at, updated_at) --
VALUES --
    (gen_random_uuid(), 'Kreissäge', 'Elektrische Kreissäge', '192.168.33.1', 'http://bohrmaschine/start', 'bohrmaschine_on', 'bohrmaschine_off', NOW(), NOW()), --
    (gen_random_uuid(), 'Hobel', 'Elektrische Hobel', '192.168.33.2', 'http://schleifmaschine/start', 'schleifmaschine_on', 'schleifmaschine_off', NOW(), NOW()), --
    (gen_random_uuid(), 'Schleifmaschine', 'Elektrische Schleifmaschine', '192.168.33.3', 'http://schleifmaschine/start', 'schleifmaschine_on', 'schleifmaschine_off', NOW(), NOW()), --
    (gen_random_uuid(), 'Bohrmaschine', 'Elektrische Bohrmaschine', '192.168.33.4', 'http://bohrmaschine/start', 'bohrmaschine_on', 'bohrmaschine_off', NOW(), NOW()), --
    (gen_random_uuid(), 'Fräse', 'Elektrische Fräse', '192.168.33.5', 'http://fraese/start', 'fraese_on', 'fraese_off', NOW(), NOW());

-- Insert RFID readers
INSERT INTO schaffbar.rfid_reader --
    (id, mac_address, type, created_at, updated_at) --
VALUES --
    (gen_random_uuid(), '00:1A:2B:3C:4D:5E', 'SWITCH_BOX', NOW(), NOW()), --
    (gen_random_uuid(), '00:1A:2B:3C:4D:5F', 'GATE_KEEPER', NOW(), NOW()), --
    (gen_random_uuid(), '00:1A:2B:3C:4D:61', 'COUNTER', NOW(), NOW());
