create table task
(
    id serial primary key,
    task_name varchar (32) not null,
    task_number varchar(32) not null,
    quantity integer not null,
    winding integer,
    output integer,
    isolation integer,
    molding integer,
    crimping integer,
    quality integer,
    testing integer
);

create table operator
(
    id serial primary key,
    first_name varchar(32) not null,
    second_name varchar (32),
    surname varchar (32) not null,
    phone varchar(16) not null,
    password varchar (32)
);

create table bobbin
(
    id serial primary key,
    task_id integer,
    bobbin_number varchar (32),
    constraint fk_task_id foreign key (task_id) references task
);

create table action
(
    id serial primary key,
    operator_id integer not null,
    bobbin_id integer not null,
    action_type varchar (50),
    done_time timestamp,
    constraint fk_operator_id foreign key (operator_id) references operator,
    constraint fk_bobbin_id foreign key (bobbin_id) references bobbin
);


