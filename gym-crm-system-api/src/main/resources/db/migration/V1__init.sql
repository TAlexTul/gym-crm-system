create table user_statuses
(
    id          int primary key,
    description text not null
);

insert into user_statuses(id, description)
values (0, 'ACTIVE');
insert into user_statuses(id, description)
values (1, 'SUSPENDED');

create table users
(
    id         bigserial primary key,
    first_name text not null,
    last_name  text not null,
    username   text not null unique,
    password   text not null,
    status     int  not null references user_statuses (id)
);

create table authorities
(
    id      int primary key,
    "value" text not null
);

insert into authorities (id, "value")
values (0, 'ROLE_ADMIN'),
       (1, 'ROLE_TRAINEE'),
       (2, 'ROLE_TRAINER');


create table user_authorities
(
    user_id      bigint not null,
    authority_id int    not null,
    primary key (user_id, authority_id),
    constraint user_authorities_user_fk foreign key (user_id)
        references users (id) on delete cascade,
    constraint user_authorities_authority_fk foreign key (authority_id)
        references authorities (id) on delete cascade
);

create table refresh_tokens
(
    "value"   uuid                     not null primary key,
    user_id   bigint                   not null,
    issued_at timestamp with time zone not null,
    expire_at timestamp with time zone not null,
    next      uuid,
    constraint refresh_tokens_user_fk foreign key (user_id)
        references users (id) on delete cascade,
    constraint refresh_tokens_next_fk foreign key (next)
        references refresh_tokens ("value") on delete cascade
);

create table trainees
(
    id            bigserial primary key,
    date_of_birth timestamp with time zone,
    address       text,
    user_id       bigint,
    constraint trainees_user_fk foreign key (user_id)
        references users (id) on delete cascade
);

create table specializations
(
    id                  int primary key,
    specialization_type text not null unique
);

insert into specializations (id, specialization_type)
values (0, 'PERSONAL_TRAINER'),
       (1, 'FITNESS_AND_WELLNESS_TRAINER'),
       (2, 'STRENGTH_TRAINING_COACH'),
       (3, 'FUNCTIONAL_TRAINING_COACH'),
       (4, 'CROSSFIT_COACH'),
       (5, 'PILATES_INSTRUCTOR'),
       (6, 'BODYBUILDING_COACH'),
       (7, 'MARTIAL_ARTS_COACH'),
       (8, 'CARDIO_TRAINING_COACH'),
       (9, 'STRETCHING_COACH'),
       (10, 'SWIMMING_COACH'),
       (11, 'GROUP_TRAINING_INSTRUCTOR'),
       (12, 'FITNESS_AEROBICS_COACH'),
       (13, 'REHABILITATION_TRAINING_COACH'),
       (14, 'SPORTS_DIET_COACH'),
       (15, 'CYCLING_COACH'),
       (16, 'GYMNASTICS_COACH'),
       (17, 'TRX_TRAINING_COACH'),
       (18, 'SPECIAL_NEEDS_TRAINING_COACH');

create table trainers
(
    id                bigserial primary key,
    specialization_id bigint not null,
    user_id           bigint not null,
    constraint trainers_specialization_fk foreign key (specialization_id)
        references specializations (id) on delete cascade,
    constraint trainers_user_fk foreign key (user_id)
        references users (id) on delete cascade
);

create table trainees_trainers
(
    trainee_id bigint,
    trainer_id bigint,
    primary key (trainee_id, trainer_id),
    constraint trainees_trainers_trainee_fk foreign key (trainee_id)
        references trainees (id) on delete cascade,
    constraint trainees_trainers_trainer_fk foreign key (trainer_id)
        references trainers (id) on delete cascade
);

create table training_types
(
    id   int primary key,
    type text not null unique
);

insert into training_types (id, type)
values (0, 'STRENGTH_TRAINING'),
       (1, 'CARDIO_WORKOUT'),
       (2, 'FUNCTIONAL_TRAINING'),
       (3, 'CROSSFIT_WORKOUT'),
       (4, 'PILATES_SESSION'),
       (5, 'BODYBUILDING_PROGRAM'),
       (6, 'MARTIAL_ARTS_TRAINING'),
       (7, 'SWIMMING_SESSION'),
       (8, 'GROUP_FITNESS_CLASS'),
       (9, 'FITNESS_AEROBICS'),
       (10, 'REHABILITATION_WORKOUT'),
       (11, 'NUTRITION_AND_DIET_PLAN'),
       (12, 'CYCLING_WORKOUT'),
       (13, 'GYMNASTICS_TRAINING'),
       (14, 'TRX_TRAINING'),
       (15, 'SPECIAL_NEEDS_TRAINING'),
       (16, 'STRETCHING_SESSION'),
       (17, 'BOOTCAMP_WORKOUT');

create table trainings
(
    id                bigserial primary key,
    training_name     text                     not null,
    training_date     timestamp with time zone not null,
    training_duration bigint                   not null
);

create table training_training_types
(
    training_id      bigint,
    training_type_id int,
    primary key (training_id, training_type_id),
    constraint training_training_types_training_fk foreign key (training_id)
        references trainings (id) on delete cascade,
    constraint training_training_types_type_fk foreign key (training_type_id)
        references training_types (id) on delete cascade
);

create table trainings_trainees
(
    training_id bigint,
    trainee_id  bigint,
    primary key (training_id, trainee_id),
    constraint trainings_trainees_training_fk foreign key (training_id)
        references trainings (id) on delete cascade,
    constraint trainings_trainees_trainee_fk foreign key (trainee_id)
        references trainees (id) on delete cascade
);

create table trainings_trainers
(
    training_id bigint,
    trainer_id  bigint,
    primary key (training_id, trainer_id),
    constraint trainings_trainers_training_fk foreign key (training_id)
        references trainings (id) on delete cascade,
    constraint trainings_trainers_trainer_fk foreign key (trainer_id)
        references trainers (id) on delete cascade
);

create table attempts
(
    id            bigserial primary key,
    username      text not null unique,
    attempts      int  not null            default 0,
    last_modified timestamp with time zone default now()
);