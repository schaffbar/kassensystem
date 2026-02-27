-- create random data for testing
INSERT INTO schaffbar.customer
    (id, first_name, last_name, date_of_birth, club_member, email, phone, address_line1, address_line2, postal_code, city, country, created_at, updated_at)
VALUES
    ('46417d27-ff9a-4cc8-a765-8ac55fc8b793', 'Max', 'Mustermann', '1985-03-12', TRUE, 'max.mustermann@example.com', '+4915112345678', 'Musterstraße 1', 'EG', '10115', 'Berlin', 'Deutschland', NOW(), NOW()),
    ('015e98c1-8a6e-40c7-88ae-af635644a85f', 'Erika', 'Musterfrau', '1990-07-25', FALSE, 'erika.musterfrau@example.com', '+4915223456789', 'Beispielweg 23', NULL, '80331', 'München', 'Deutschland', NOW(), NOW()),
    ('57c717c3-cbec-4b33-a413-dff0c07082f0', 'Hans', 'Schmidt', '1978-11-03', TRUE, 'hans.schmidt@example.com', '+4915334567890', 'Hauptstraße 45', NULL, '50667', 'Köln', 'Deutschland', NOW(), NOW()),
    ('57363bdf-2853-46bf-8477-3e288ee289fb', 'Julia', 'Schneider', '1995-05-18', FALSE, 'julia.schneider@example.com', '+4915445678901', 'Bahnhofstraße 12', NULL, '20095', 'Hamburg', 'Deutschland', NOW(), NOW()),
    ('3c0825ad-6be3-426b-903c-463762c5c2e5', 'Peter', 'Fischer', '1982-09-30', TRUE, 'peter.fischer@example.com', '+4915556789012', 'Lindenweg 7', NULL, '04109', 'Leipzig', 'Deutschland', NOW(), NOW()),
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
INSERT INTO schaffbar.tool (id,name,description,rfid_reader_id,ip_address,http_start_command,on_command,off_command,created_at,updated_at) VALUES
	 (gen_random_uuid(),'Schleifmaschine','Elektrische Schleifmaschine',NULL,NULL,NULL,NULL,NULL,'2026-02-11 17:44:31.4393','2026-02-21 18:08:26.922025'),
	 (gen_random_uuid(),'Hobel','Elektrische Hobel',NULL,NULL,NULL,NULL,NULL,'2026-02-11 17:44:31.4393','2026-02-21 18:10:00.810274'),
	 (gen_random_uuid(),'Fräse','Elektrische Fräse',NULL,NULL,NULL,NULL,NULL,'2026-02-11 17:44:31.4393','2026-02-21 18:10:38.20927'),
	 (gen_random_uuid(),'Kreissäge','Elektrische Kreissäge',NULL,NULL,NULL,NULL,NULL,'2026-02-11 17:44:31.4393','2026-02-21 18:11:25.058038');

-- Insert RFID readers
INSERT INTO schaffbar.rfid_reader (id,mac_address,name,socket_name,"type",created_at,updated_at) VALUES
	 (gen_random_uuid(),'34:CD:B0:D3:8F:0C','Werkstatt Eingang',NULL,NULL,'2025-11-08 15:16:28.069472','2026-02-11 18:06:34.245723'),
	 (gen_random_uuid(),'34:CD:B0:D3:A9:30','Switch Fräse','Socket 6',NULL,'2025-11-08 15:16:34.195646','2026-02-15 23:04:26.155055'),
	 (gen_random_uuid(),'34:CD:B0:D3:95:C0','Switch Kreissäge','Socket 48',NULL,'2025-11-08 15:22:52.748111','2026-02-16 21:00:29.464559');

-- Insert RFID tags
INSERT INTO schaffbar.rfid_tag (id,active,created_at,updated_at) VALUES
	 ('2373C001',true,'2025-11-08 15:24:48.497148','2025-11-08 15:24:48.497704'),
	 ('C385A62C',true,'2025-11-08 15:25:02.783311','2025-11-08 15:25:02.783622');

-- Open workshop sessions for active users
INSERT INTO schaffbar.workshop_session (id, customer_id, start_time, close_time, status, updated_at) VALUES
--    ('275701f9-2fbb-4b15-a370-5a89260d0f2a', '46417d27-ff9a-4cc8-a765-8ac55fc8b793', NOW() - INTERVAL '2 hours',   NULL, 'OPEN', NOW()),  -- seit 2 Stunden
--    ('0c0d3258-772a-48a9-a564-6fc850787513', '015e98c1-8a6e-40c7-88ae-af635644a85f', NOW() - INTERVAL '45 minutes', NULL, 'OPEN', NOW()),  -- seit 45 Min
--    ('b2c5c409-281f-482f-9f3d-58a52295f201', '57c717c3-cbec-4b33-a413-dff0c07082f0', NOW() - INTERVAL '3 hours',   NULL, 'OPEN', NOW()),  -- seit 3 Stunden
--    ('fe72c9b6-e225-4e82-af69-0a48befb863a', '57363bdf-2853-46bf-8477-3e288ee289fb', NOW() - INTERVAL '5 hours',   NOW() - INTERVAL '1 hour', 'PAID', NOW()),  -- bereits bezahlt und weg
    ('57a43da8-2c4e-49cf-b61c-0fe2f40c5cbc', '3c0825ad-6be3-426b-903c-463762c5c2e5', NOW() - INTERVAL '6 hours',   NULL, 'OPEN', NOW())  -- seit 6 Stunden
ON CONFLICT (id) DO NOTHING;

-- Active workshop usages (exit_time IS NULL = currently in workshop)
INSERT INTO schaffbar.workshop_usage (id, customer_id, workshop_session_id, entry_time, exit_time, updated_at) VALUES
    -- entered 2 hours ago, still working
--    (gen_random_uuid(), '46417d27-ff9a-4cc8-a765-8ac55fc8b793', '275701f9-2fbb-4b15-a370-5a89260d0f2a', NOW() - INTERVAL '2 hours',   NULL, NOW()),
    -- entered 45 minutes ago, still working
--    (gen_random_uuid(), '015e98c1-8a6e-40c7-88ae-af635644a85f', '0c0d3258-772a-48a9-a564-6fc850787513', NOW() - INTERVAL '45 minutes', NULL, NOW()),
    -- first visit earlier today (already left), then came back 30 min ago
--    (gen_random_uuid(), '57c717c3-cbec-4b33-a413-dff0c07082f0', 'b2c5c409-281f-482f-9f3d-58a52295f201', NOW() - INTERVAL '3 hours',   NOW() - INTERVAL '1 hour', NOW()),  -- left after 2h
--    (gen_random_uuid(), '57c717c3-cbec-4b33-a413-dff0c07082f0', 'b2c5c409-281f-482f-9f3d-58a52295f201', NOW() - INTERVAL '30 minutes', NULL, NOW()),  -- came back, still here
    -- was in workshop but already left (closed session)
--    (gen_random_uuid(), '57363bdf-2853-46bf-8477-3e288ee289fb', 'fe72c9b6-e225-4e82-af69-0a48befb863a', NOW() - INTERVAL '5 hours',   NOW() - INTERVAL '1 hour', NOW()),
    -- was in workshop for 6 hours, still here
    (gen_random_uuid(), '3c0825ad-6be3-426b-903c-463762c5c2e5', '57a43da8-2c4e-49cf-b61c-0fe2f40c5cbc', NOW() - INTERVAL '6 hours',   NOW() - INTERVAL '5 hours', NOW()),
    (gen_random_uuid(), '3c0825ad-6be3-426b-903c-463762c5c2e5', '57a43da8-2c4e-49cf-b61c-0fe2f40c5cbc', NOW() - INTERVAL '2 hours',   NULL, NOW())
ON CONFLICT (id) DO NOTHING;
