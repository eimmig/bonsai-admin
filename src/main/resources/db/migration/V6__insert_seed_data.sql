INSERT INTO users (email, password_hash, active)
VALUES ('admin@utfpr.edu.br', '$2a$10$5rESaZ8s7x/2ws4KeeU/7.Fh6Mz2MwwLhhJ6e/mtFRsHrW1o1Uq3G', true); -- Senha: admin1

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r 
WHERE u.email = 'admin@utfpr.edu.br' AND r.name = 'ADMIN';

INSERT INTO users (email, password_hash, active)
VALUES ('cliente@utfpr.edu.br', '$2a$10$5rESaZ8s7x/2ws4KeeU/7.Fh6Mz2MwwLhhJ6e/mtFRsHrW1o1Uq3G', false);

INSERT INTO orders (customer_id, status)
SELECT id, 'AGUARDANDO_PAGAMENTO' FROM users WHERE email = 'cliente@utfpr.edu.br';

INSERT INTO orders (customer_id, status)
SELECT id, 'AGUARDANDO_PAGAMENTO' FROM users WHERE email = 'cliente@utfpr.edu.br';

INSERT INTO order_items (order_id, product_name, quantity, unit_price)
SELECT id, 'Teclado Mecânico', 1, 350.00 FROM orders LIMIT 1;