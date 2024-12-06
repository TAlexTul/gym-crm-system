create table user_statuses
(
    id          int primary key,
    description text not null
);

insert into user_statuses(id, description)
values (0, 'ACTIVE'),
       (1, 'SUSPENDED');

create table users
(
    id         bigserial primary key,
    first_name text not null,
    last_name  text not null,
    username   text not null unique,
    status     int  not null references user_statuses (id)
);

create table trainings
(
    id                bigserial primary key,
    user_id           bigint,
    training_date     timestamp with time zone not null,
    training_duration bigint                   not null,
    constraint trainings_user_fk foreign key (user_id)
        references users (id) on delete cascade
);