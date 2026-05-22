create table audit_logs (
    id uuid primary key default gen_random_uuid(),
    entity_type varchar(50) not null,
    entity_id uuid not null,
    action varchar(50) not null,
    details text,
    created_at timestamp not null default now()
);

create index idx_audit_logs_entity on audit_logs(entity_type, entity_id);

create table email_logs (
    id uuid primary key default gen_random_uuid(),
    order_id uuid not null references orders(id),
    to_email varchar(255) not null,
    subject varchar(255) not null,
    status varchar(20) not null,
    error_message text,
    created_at timestamp not null default now()
);

create index idx_email_logs_order_id on email_logs(order_id);