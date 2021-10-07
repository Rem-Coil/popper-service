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
    firstname varchar(32) not null,
    second_name varchar (32),
    surname varchar (32) not null,
    phone varchar(16) not null,
    password varchar (32)
);

create table bobbin
(
    id serial primary key,
    taskId integer,
    bobbinNumber varchar (32),
    constraint fk_task_id foreign key (taskId) references task
);

create table action
(
    id serial primary key,
    operatorId integer not null,
    bobbinId integer not null,
    actionType varchar (50),
    doneTime timestamp,
    isDone boolean,
    constraint fk_operator_id foreign key (operatorId) references operator,
    constraint fk_bobbin_id foreign key (bobbinId) references bobbin
);


