create table if not exists operation_types
(
    id               bigint generated always as identity primary key,
    type             varchar(64) not null,
    sequence_number  integer     not null,
    specification_id bigint,
    constraint fk_specification_action_specification foreign key (specification_id)
        references specifications (id) on delete cascade
);

drop table if exists actions;

create table if not exists actions
(
    id             bigint generated always as identity primary key,
    done_time      timestamp,
    repair         boolean default false,
    operation_type bigint not null,
    employee_id    bigint not null,
    product_id     bigint not null,
    constraint fk_action_employee foreign key (employee_id)
        references operator (id),
    constraint fk_action_product foreign key (product_id)
        references products (id) on delete cascade,
    constraint fk_action_operation_type foreign key (operation_type)
        references operation_types (id) on delete cascade
);

create table if not exists control_actions
(
    id             bigint generated always as identity primary key,
    done_time      timestamp,
    successful     boolean default true,
    control_type   varchar(64),
    comment        text,
    operation_type bigint not null,
    employee_id    bigint not null,
    product_id     bigint not null,
    constraint fk_action_employee foreign key (employee_id)
        references operator (id),
    constraint fk_action_product foreign key (product_id)
        references products (id) on delete cascade,
    constraint fk_action_operation_type foreign key (operation_type)
        references operation_types (id) on delete cascade
);