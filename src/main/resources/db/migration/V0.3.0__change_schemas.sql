drop view if exists full_action;
drop table if exists defect_comment;
drop table if exists action;
drop table if exists bobbin;
drop table if exists batch;
drop table if exists task;
drop table if exists operator;

create table specifications
(
    id                   bigint generated always as identity primary key,
    specification_title varchar(32) not null,
    product_type         varchar(32) not null,
    tested_percentage    integer     not null default 0
);

create table specification_actions
(
    id               bigint generated always as identity primary key,
    action_type      varchar(64) not null,
    sequence_number  integer     not null,
    specification_id bigint,
    constraint fk_specification_action_specification foreign key (specification_id)
        references specifications (id) on delete cascade
);

create table kits
(
    id               bigint generated always as identity primary key,
    kit_number       varchar(64) not null,
    batches_quantity integer     not null default 1,
    batch_size       integer     not null default 1,
    specification_id bigint,
    constraint fk_kit_specification foreign key (specification_id)
        references specifications (id) on delete cascade
);

create table batches
(
    id           bigint generated always as identity primary key,
    batch_number varchar(96) not null,
    kit_id       bigint,
    constraint fk_batch_kit foreign key (kit_id)
        references kits (id) on delete cascade
);

create table products
(
    id             bigint generated always as identity primary key,
    product_number varchar(128) not null,
    active         boolean      not null default true,
    batch_id       bigint,
    constraint fk_product_batch foreign key (batch_id)
        references batches on delete CASCADE
);

create table operators
(
    id          bigint generated always as identity primary key,
    first_name  varchar(32) not null,
    second_name varchar(32),
    surname     varchar(32) not null,
    phone       varchar(16) not null,
    password    varchar(32),
    active      boolean              default true,
    role        varchar(32) not null default 'operator'
);

insert into operators (first_name, second_name, surname, phone, password, active, role)
values ('RemCoil', '', 'Admin', 2006, 'admin', true, 'admin');

create table actions
(
    id             bigint generated always as identity primary key,
    operator_id    bigint not null,
    product_id     bigint not null,
    action_type_id bigint,
    done_time      timestamp,
    successful     boolean default true,
    constraint fk_action_operator foreign key (operator_id)
        references operators (id),
    constraint fk_action_product foreign key (product_id)
        references products (id) on delete cascade,
    constraint fk_action_specification_action foreign key (action_type_id)
        references specification_actions (id) on delete cascade
);

create table comments
(
    action_id bigint primary key
        constraint fk_comment_action references actions (id) on delete cascade,
    comment   text
);
create view full_actions as
select specifications.id        as specification_id,
       specifications.specification_title,
       specifications.product_type,

       kits.id                  as kit_id,
       kits.kit_number,

       batches.id               as batch_id,
       batches.batch_number,

       products.id              as product_id,
       products.product_number,
       products.active,

       actions.id               as action_id,
       actions.done_time,
       actions.successful,

       specification_actions.id as specification_action_id,
       specification_actions.action_type,
       specification_actions.sequence_number,

       comments.comment,

       operators.id             as operator_id,
       operators.first_name,
       operators.second_name,
       operators.surname,
       operators.role
from specifications
         join kits on specifications.id = kits.specification_id
         join batches on kits.id = batches.kit_id
         join products on batches.id = products.batch_id
         join actions on products.id = actions.product_id
         join specification_actions on specification_actions.id = actions.action_type_id
         left join comments on actions.id = comments.action_id
         join operators on operators.id = actions.operator_id;
