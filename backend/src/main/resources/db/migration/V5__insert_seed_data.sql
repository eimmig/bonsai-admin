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
