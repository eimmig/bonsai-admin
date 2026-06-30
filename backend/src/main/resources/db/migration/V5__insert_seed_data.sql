INSERT INTO users (email, password_hash, active)
VALUES ('admin@utfpr.edu.br', '$2a$10$5rESaZ8s7x/2ws4KeeU/7.Fh6Mz2MwwLhhJ6e/mtFRsHrW1o1Uq3G', true); -- Senha: admin1

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@utfpr.edu.br' AND r.name = 'ADMIN';

INSERT INTO users (email, password_hash, active)
VALUES ('operador@utfpr.edu.br', '$2a$10$5rESaZ8s7x/2ws4KeeU/7.Fh6Mz2MwwLhhJ6e/mtFRsHrW1o1Uq3G', true); -- Senha: admin1

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'operador@utfpr.edu.br' AND r.name = 'OPERATOR';

INSERT INTO customers (id, email, name)
VALUES (gen_random_uuid(), 'joao.silva@email.com', 'João Silva');

INSERT INTO customers (id, email, name)
VALUES (gen_random_uuid(), 'maria.santos@email.com', 'Maria Santos');

INSERT INTO orders (customer_id, status)
SELECT id, 'AGUARDANDO_PAGAMENTO' FROM customers WHERE email = 'joao.silva@email.com';

INSERT INTO orders (customer_id, status)
SELECT id, 'AGUARDANDO_PAGAMENTO' FROM customers WHERE email = 'maria.santos@email.com';

INSERT INTO order_items (order_id, product_name, quantity, unit_price)
SELECT id, 'Teclado Mecânico', 1, 350.00 FROM orders ORDER BY created_at ASC LIMIT 1;

INSERT INTO order_items (order_id, product_name, quantity, unit_price)
SELECT id, 'Mouse Sem Fio', 2, 120.00 FROM orders ORDER BY created_at DESC LIMIT 1;

-- ─────────────────────────────────────────────────────────────────────────────
-- Additional seed data
-- ─────────────────────────────────────────────────────────────────────────────

INSERT INTO customers (id, email, name) VALUES
  ('a1b2c3d4-0000-0000-0000-000000000001', 'alice.green@email.com',   'Alice Green'),
  ('a1b2c3d4-0000-0000-0000-000000000002', 'bob.martins@email.com',   'Bob Martins'),
  ('a1b2c3d4-0000-0000-0000-000000000003', 'carol.white@email.com',   'Carol White'),
  ('a1b2c3d4-0000-0000-0000-000000000004', 'david.lee@email.com',     'David Lee'),
  ('a1b2c3d4-0000-0000-0000-000000000005', 'emma.jones@email.com',    'Emma Jones');

INSERT INTO orders (id, customer_id, status, created_at, updated_at) VALUES
  ('b0000001-0000-0000-0000-000000000001', 'a1b2c3d4-0000-0000-0000-000000000001', 'ENTREGUE',            '2025-01-10 09:15:00', '2025-01-18 14:00:00'),
  ('b0000001-0000-0000-0000-000000000002', 'a1b2c3d4-0000-0000-0000-000000000001', 'PAGO',                '2025-02-05 11:30:00', '2025-02-05 11:30:00'),
  ('b0000001-0000-0000-0000-000000000003', 'a1b2c3d4-0000-0000-0000-000000000002', 'EM_TRANSPORTE',       '2025-02-20 08:45:00', '2025-02-22 10:00:00'),
  ('b0000001-0000-0000-0000-000000000004', 'a1b2c3d4-0000-0000-0000-000000000002', 'CANCELADO',           '2025-03-01 16:00:00', '2025-03-02 09:30:00'),
  ('b0000001-0000-0000-0000-000000000005', 'a1b2c3d4-0000-0000-0000-000000000003', 'PAGO',                '2025-03-12 10:00:00', '2025-03-12 10:00:00'),
  ('b0000001-0000-0000-0000-000000000006', 'a1b2c3d4-0000-0000-0000-000000000003', 'ENTREGUE',            '2025-04-02 13:20:00', '2025-04-10 17:00:00'),
  ('b0000001-0000-0000-0000-000000000007', 'a1b2c3d4-0000-0000-0000-000000000003', 'AGUARDANDO_PAGAMENTO','2025-05-18 09:00:00', '2025-05-18 09:00:00'),
  ('b0000001-0000-0000-0000-000000000008', 'a1b2c3d4-0000-0000-0000-000000000004', 'EM_TRANSPORTE',       '2025-05-25 14:10:00', '2025-05-27 08:00:00'),
  ('b0000001-0000-0000-0000-000000000009', 'a1b2c3d4-0000-0000-0000-000000000004', 'ENTREGUE',            '2025-06-03 07:55:00', '2025-06-11 16:30:00'),
  ('b0000001-0000-0000-0000-000000000010', 'a1b2c3d4-0000-0000-0000-000000000005', 'AGUARDANDO_PAGAMENTO','2025-06-15 11:00:00', '2025-06-15 11:00:00'),
  ('b0000001-0000-0000-0000-000000000011', 'a1b2c3d4-0000-0000-0000-000000000005', 'PAGO',                '2025-06-20 15:45:00', '2025-06-20 15:45:00'),
  ('b0000001-0000-0000-0000-000000000012', 'a1b2c3d4-0000-0000-0000-000000000005', 'CANCELADO',           '2025-06-28 10:30:00', '2025-06-28 14:00:00');

INSERT INTO order_items (order_id, product_name, quantity, unit_price) VALUES
  -- order 1 — Alice, ENTREGUE
  ('b0000001-0000-0000-0000-000000000001', 'Bonsai Juniperus',              1,  280.00),
  ('b0000001-0000-0000-0000-000000000001', 'Vaso Cerâmica Premium',         1,   95.00),
  ('b0000001-0000-0000-0000-000000000001', 'Substrato Akadama 2L',          2,   45.00),

  -- order 2 — Alice, PAGO
  ('b0000001-0000-0000-0000-000000000002', 'Kit Ferramentas Bonsai',        1,  189.00),
  ('b0000001-0000-0000-0000-000000000002', 'Arame de Alumínio 2mm',         3,   18.00),

  -- order 3 — Bob, EM_TRANSPORTE
  ('b0000001-0000-0000-0000-000000000003', 'Bonsai Ficus Retusa',           1,  350.00),
  ('b0000001-0000-0000-0000-000000000003', 'Bandeja Anti-Evaporação',       1,   38.00),
  ('b0000001-0000-0000-0000-000000000003', 'Fertilizante Osmocote 500g',    2,   32.00),

  -- order 4 — Bob, CANCELADO
  ('b0000001-0000-0000-0000-000000000004', 'Bonsai Azaléia',                1,  420.00),

  -- order 5 — Carol, PAGO
  ('b0000001-0000-0000-0000-000000000005', 'Substrato Kiryu 1L',            3,   55.00),
  ('b0000001-0000-0000-0000-000000000005', 'Pedras Decorativas 500g',       2,   22.00),

  -- order 6 — Carol, ENTREGUE
  ('b0000001-0000-0000-0000-000000000006', 'Bonsai Carmona',                1,  310.00),
  ('b0000001-0000-0000-0000-000000000006', 'Vaso Cerâmica Premium',         2,   95.00),
  ('b0000001-0000-0000-0000-000000000006', 'Tesoura de Poda Profissional',  1,  145.00),

  -- order 7 — Carol, AGUARDANDO_PAGAMENTO
  ('b0000001-0000-0000-0000-000000000007', 'Livro Técnicas de Bonsai',      1,   79.00),
  ('b0000001-0000-0000-0000-000000000007', 'Arame de Alumínio 3mm',         2,   22.00),

  -- order 8 — David, EM_TRANSPORTE
  ('b0000001-0000-0000-0000-000000000008', 'Bonsai Ficus Ginseng',          1,  265.00),
  ('b0000001-0000-0000-0000-000000000008', 'Substrato Akadama 2L',          2,   45.00),
  ('b0000001-0000-0000-0000-000000000008', 'Fertilizante Líquido 250ml',    1,   28.00),

  -- order 9 — David, ENTREGUE
  ('b0000001-0000-0000-0000-000000000009', 'Kit Ferramentas Bonsai',        1,  189.00),
  ('b0000001-0000-0000-0000-000000000009', 'Bandeja Anti-Evaporação',       2,   38.00),

  -- order 10 — Emma, AGUARDANDO_PAGAMENTO
  ('b0000001-0000-0000-0000-000000000010', 'Bonsai Juniperus',              2,  280.00),
  ('b0000001-0000-0000-0000-000000000010', 'Vaso Cerâmica Premium',         2,   95.00),
  ('b0000001-0000-0000-0000-000000000010', 'Substrato Kiryu 1L',            1,   55.00),

  -- order 11 — Emma, PAGO
  ('b0000001-0000-0000-0000-000000000011', 'Tesoura de Poda Profissional',  1,  145.00),
  ('b0000001-0000-0000-0000-000000000011', 'Arame de Alumínio 2mm',         5,   18.00),
  ('b0000001-0000-0000-0000-000000000011', 'Pedras Decorativas 500g',       3,   22.00),

  -- order 12 — Emma, CANCELADO
  ('b0000001-0000-0000-0000-000000000012', 'Bonsai Azaléia',                1,  420.00),
  ('b0000001-0000-0000-0000-000000000012', 'Livro Técnicas de Bonsai',      1,   79.00);
