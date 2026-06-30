create table customers (
    id uuid primary key,
    email varchar (255) not null,
    name varchar (255) not null,
    created_at timestamp not null default now()
);

create index idx_customers_email on customers(email);

alter table orders
    add constraint fk_orders_customer_id foreign key (customer_id) references customers(id);
