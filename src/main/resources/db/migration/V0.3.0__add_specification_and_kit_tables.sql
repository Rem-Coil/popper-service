create table if not exists specifications
(
    id                  bigint generated always as identity primary key,
    specification_title varchar(32) not null,
    product_type        varchar(32) not null,
    tested_percentage   integer     not null default 0
);

create table if not exists kits
(
    id               bigint generated always as identity primary key,
    kit_number       varchar(64) not null,
    batches_quantity integer     not null default 1,
    batch_size       integer     not null default 1,
    specification_id bigint,
    constraint fk_kit_specification foreign key (specification_id)
        references specifications (id) on delete cascade
);

create table if not exists batches
(
    id           bigint generated always as identity primary key,
    batch_number varchar(96) not null,
    kit_id       bigint,
    constraint fk_batch_kit foreign key (kit_id)
        references kits (id) on delete cascade
);

create table if not exists products
(
    id             bigint generated always as identity primary key,
    product_number varchar(128) not null,
    active         boolean      not null default true,
    batch_id       bigint,
    constraint fk_product_batch foreign key (batch_id)
        references batches on delete CASCADE
);

create table if not exists actions
(
    id          bigint generated always as identity primary key,
    employee_id bigint not null,
    product_id  bigint not null,
    action_type varchar(64),
    done_time   timestamp,
    successful  boolean default true,
    constraint fk_action_employee foreign key (employee_id)
        references operator(id),
    constraint fk_action_product foreign key (product_id)
        references products (id) on delete cascade
);

create view extended_specifications as
select specifications.id,
       specifications.specification_title,
       specifications.product_type,
       specifications.tested_percentage,
       count(k.id) as kit_quantity
from specifications
         left join kits k on specifications.id = k.specification_id
group by specifications.id;

create view extended_kits as
select kits.id,
       concat_ws(' / ', specification_title, kit_number) as kit_number,
       batches_quantity,
       batch_size,
       specification_id
from kits
         join specifications s on s.id = kits.specification_id;

create view extended_batches as
select batches.id,
       concat_ws(' / ', specification_title, kit_number, batch_number) as batch_number,
       kit_id
from batches
         join kits k on k.id = batches.kit_id
         join specifications s on s.id = k.specification_id;

create view extended_products as
select products.id,
       concat_ws(' / ', specification_title, kit_number, batch_number, product_number) as product_number,
       active,
       batch_id
from products
         join batches b on b.id = products.batch_id
         join kits k on k.id = b.kit_id
         join specifications s on s.id = k.specification_id;