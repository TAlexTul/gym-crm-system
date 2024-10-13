create table user_statuses
(
    id          int primary key,
    description text not null
);

insert into user_statuses(id, description)
values (0, 'ACTIVE');
insert into user_statuses(id, description)
values (1, 'SUSPEND');

create table users
(
    id         bigint primary key,
    first_name text not null,
    last_name  text not null,
    username   text not null,
    password   text not null,
    status     int  not null references user_statuses (id)
);

create table trainees
(
    id            bigint primary key,
    date_of_birth timestamptz,
    address       text,
    user_id       bigint,
    constraint trainees_user_fk foreign key (user_id)
        references users (id) on delete cascade
);

create table trainers
(
    id             bigint primary key,
    specialization int not null,
    user_id        bigint,
    constraint trainers_user_fk foreign key (user_id)
        references users (id) on delete cascade
);

create table training_types
(
    id   int primary key,
    type text not null
);

insert into training_types (id, type)
values (0, 'STRENGTH_TRAINING');
insert into training_types (id, type)
values (1, 'CARDIO_WORKOUT');
insert into training_types (id, type)
values (2, 'FUNCTIONAL_TRAINING');
insert into training_types (id, type)
values (3, 'CROSSFIT_WORKOUT');
insert into training_types (id, type)
values (4, 'PILATES_SESSION');
insert into training_types (id, type)
values (5, 'BODYBUILDING_PROGRAM');
insert into training_types (id, type)
values (6, 'MARTIAL_ARTS_TRAINING');
insert into training_types (id, type)
values (7, 'SWIMMING_SESSION');
insert into training_types (id, type)
values (8, 'GROUP_FITNESS_CLASS');
insert into training_types (id, type)
values (9, 'FITNESS_AEROBICS');
insert into training_types (id, type)
values (10, 'REHABILITATION_WORKOUT');
insert into training_types (id, type)
values (11, 'NUTRITION_AND_DIET_PLAN');
insert into training_types (id, type)
values (12, 'CYCLING_WORKOUT');
insert into training_types (id, type)
values (13, 'GYMNASTICS_TRAINING');
insert into training_types (id, type)
values (14, 'TRX_TRAINING');
insert into training_types (id, type)
values (15, 'SPECIAL_NEEDS_TRAINING');
insert into training_types (id, type)
values (16, 'STRETCHING_SESSION');
insert into training_types (id, type)
values (17, 'BOOTCAMP_WORKOUT');

create table trainings
(
    id                bigint primary key,
    training_name     text        not null,
    training_types_id int         not null,
    training_date     timestamptz not null,
    training_duration bigint      not null,
    constraint trainings_training_type_fk foreign key (training_types_id)
        references training_types (id) on delete cascade
);

create table trainings_trainees
(
    training_id bigint,
    trainee_id  bigint,
    primary key (training_id, trainee_id),
    constraint trainings_trainees_training_fk foreign key (training_id)
        references trainings (id),
    constraint trainings_trainees_trainee_fk foreign key (trainee_id)
        references trainees (id)
);

create table trainings_trainers
(
    training_id bigint,
    trainer_id  bigint,
    primary key (training_id, trainer_id),
    constraint trainings_trainers_training_fk foreign key (training_id)
        references trainings (id),
    constraint trainings_trainers_trainee_fk foreign key (trainer_id)
        references trainers (id)
);