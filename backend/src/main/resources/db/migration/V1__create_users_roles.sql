CREATE EXTENSION IF NOT EXISTS pgcrypto;

create table users (
    id uuid primary key default gen_random_uuid(),
    email varchar(255) not null unique,
    password_hash varchar(255) not null,
    active boolean not null default false,
    created_at timestamp not null default now()
);

create table roles (
    id uuid primary key default gen_random_uuid(),
    name varchar(50) not null unique
);

create table user_roles (
    user_id uuid not null references users(id),
    role_id uuid not null references roles(id),
    primary key (user_id, role_id)
);

insert into roles(name) values ('ADMIN'), ('OPERATOR');