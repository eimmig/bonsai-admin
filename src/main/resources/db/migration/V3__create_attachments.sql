create table attachments (
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null references orders(id),
    type varchar(30) not null,
    file_name varchar(255) not null,
    mime_type varchar(120) not null,
    size_bytes bigint not null,
    storage_key varchar(255) not null,
    created_at timestamp not null default now()
);

create index idx_attachments_order_id on attachments(order_id);