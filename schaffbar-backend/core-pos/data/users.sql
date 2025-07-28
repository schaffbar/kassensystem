-- create random data for testing
INSERT INTO schaffbar.customer --
    (id, first_name, last_name, email, phone, address_line1, address_line2, postal_code, city, country, created_at, updated_at) --
VALUES --
    (gen_random_uuid(), 'Max', 'Mustermann', 'max.mustermann@example.com', '+4915112345678', 'Musterstraße 1', 'EG', '10115', 'Berlin', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Erika', 'Musterfrau', 'erika.musterfrau@example.com', '+4915223456789', 'Beispielweg 23', NULL, '80331', 'München', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Hans', 'Schmidt', 'hans.schmidt@example.com', '+4915334567890', 'Hauptstraße 45', NULL, '50667', 'Köln', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Julia', 'Schneider', 'julia.schneider@example.com', '+4915445678901', 'Bahnhofstraße 12', NULL, '20095', 'Hamburg', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Peter', 'Fischer', 'peter.fischer@example.com', '+4915556789012', 'Lindenweg 7', NULL, '04109', 'Leipzig', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Anna', 'Weber', 'anna.weber@example.com', '+4915667890123', 'Gartenstraße 3', NULL, '70173', 'Stuttgart', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Lukas', 'Meyer', 'lukas.meyer@example.com', '+4915778901234', 'Kirchplatz 9', NULL, '90402', 'Nürnberg', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Sophie', 'Wagner', 'sophie.wagner@example.com', '+4915889012345', 'Schulstraße 5', NULL, '01067', 'Dresden', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Paul', 'Becker', 'paul.becker@example.com', '+4915990123456', 'Ringstraße 8', NULL, '28195', 'Bremen', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Laura', 'Hoffmann', 'laura.hoffmann@example.com', '+4916012345678', 'Marktplatz 2', NULL, '99084', 'Erfurt', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Tim', 'Keller', 'tim.keller@example.com', '+4916112345678', 'Seestraße 11', NULL, '24103', 'Kiel', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Mia', 'Krüger', 'mia.krueger@example.com', '+4916212345678', 'Parkallee 17', NULL, '28209', 'Bremen', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Felix', 'Zimmermann', 'felix.zimmermann@example.com', '+4916312345678', 'Waldweg 4', NULL, '37073', 'Göttingen', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Lea', 'Hartmann', 'lea.hartmann@example.com', '+4916412345678', 'Feldstraße 22', NULL, '34117', 'Kassel', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Jonas', 'Schulz', 'jonas.schulz@example.com', '+4916512345678', 'Bergstraße 6', NULL, '55116', 'Mainz', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Marie', 'Wolf', 'marie.wolf@example.com', '+4916612345678', 'Rosenweg 13', NULL, '89073', 'Ulm', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'David', 'Neumann', 'david.neumann@example.com', '+4916712345678', 'Am Markt 10', NULL, '99084', 'Erfurt', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Lena', 'Schmitt', 'lena.schmitt@example.com', '+4916812345678', 'Dorfstraße 8', NULL, '93047', 'Regensburg', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Jan', 'Bauer', 'jan.bauer@example.com', '+4916912345678', 'Industriestraße 15', NULL, '66111', 'Saarbrücken', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Clara', 'Richter', 'clara.richter@example.com', '+4917012345678', 'Allee 19', NULL, '39104', 'Magdeburg', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Simon', 'König', 'simon.koenig@example.com', '+4917112345678', 'Brückenstraße 21', NULL, '18055', 'Rostock', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Nina', 'Lorenz', 'nina.lorenz@example.com', '+4917212345678', 'Weinbergweg 2', NULL, '55116', 'Mainz', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Moritz', 'Graf', 'moritz.graf@example.com', '+4917312345678', 'Schillerstraße 14', NULL, '80336', 'München', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Katharina', 'Brandt', 'katharina.brandt@example.com', '+4917412345678', 'Goethestraße 8', NULL, '04109', 'Leipzig', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Sebastian', 'Arnold', 'sebastian.arnold@example.com', '+4917512345678', 'Mozartstraße 3', NULL, '70173', 'Stuttgart', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Vanessa', 'Schuster', 'vanessa.schuster@example.com', '+4917612345678', 'Lessingweg 5', NULL, '90402', 'Nürnberg', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Tobias', 'Krause', 'tobias.krause@example.com', '+4917712345678', 'Uhlandstraße 11', NULL, '01067', 'Dresden', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Melanie', 'Peters', 'melanie.peters@example.com', '+4917812345678', 'Friedrichstraße 6', NULL, '28195', 'Bremen', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Fabian', 'Jung', 'fabian.jung@example.com', '+4917912345678', 'Königsallee 9', NULL, '40212', 'Düsseldorf', 'Deutschland', NOW(), NOW()), --
    (gen_random_uuid(), 'Carolin', 'Bergmann', 'carolin.bergmann@example.com', '+4918012345678', 'Wilhelmstraße 17', NULL, '34117', 'Kassel', 'Deutschland', NOW(), NOW());

INSERT INTO schaffbar.tool --
    (id, name, description, ip_address, http_start_command, on_command, off_command, created_at, updated_at) --
VALUES --
    (gen_random_uuid(), 'Kreissäge', 'Elektrische Kreissäge', '192.168.33.1', 'http://bohrmaschine/start', 'bohrmaschine_on', 'bohrmaschine_off', NOW(), NOW()), --
    (gen_random_uuid(), 'Hobel', 'Elektrische Hobel', '192.168.33.2', 'http://schleifmaschine/start', 'schleifmaschine_on', 'schleifmaschine_off', NOW(), NOW()); --
