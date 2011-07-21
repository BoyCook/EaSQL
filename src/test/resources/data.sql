INSERT INTO Person (id, name, email, phone) VALUES (1, 'Craig', 'craig@cook.com', '07345123456');
INSERT INTO Person (id, name, email, phone) VALUES (2, 'Bob', 'bob@cook.com', '07345123457');
INSERT INTO Dog (id, name, person_id) VALUES (1, 'Lassie', (SELECT id FROM Person WHERE name = 'Craig'));
INSERT INTO Cat (id, name, person_id) VALUES (1, 'Bagpuss', (SELECT id FROM Person WHERE name = 'Craig'));
INSERT INTO Cat (id, name, person_id) VALUES (2, 'Daisy', (SELECT id FROM Person WHERE name = 'Bob'));