create table order_status_history (
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null references orders(id),
    from_status varchar(30) not null,
    to_status varchar(30) not null,
    changed_by varchar(255) not null,
    changed_at timestamp not null default now()
);

create index idx_order_status_history_order_id on order_status_history(order_id);