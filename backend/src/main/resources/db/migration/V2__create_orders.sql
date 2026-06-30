create table orders (
    id uuid primary key default gen_random_uuid(),
    customer_id uuid not null references users(id),
    status varchar(30) not null,
    created_at timestamp not null default now(),
    updated_at timestamp not null default now()
);

create index idx_orders_status on orders(status);
create index idx_orders_created_at on orders(created_at);
create index idx_orders_customer_id on orders(customer_id);

create table order_items (
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null references orders(id),
    product_name varchar(255) not null,
    quantity int not null,
    unit_price numeric(12,2) not null
);