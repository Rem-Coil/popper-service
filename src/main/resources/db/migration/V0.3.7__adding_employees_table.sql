create table if not exists employees
(
    id          bigint generated always as identity primary key,
    first_name  varchar(32) not null,
    last_name     varchar(32) not null,
    phone       varchar(16) not null,
    password    varchar(32),
    active      boolean              default true,
    role        varchar(32) not null default 'operator'
);

insert into employees (first_name, last_name, phone, password, active, role)
values ('RemCoil', 'Admin', '2006', 'admin', true, 'ADMIN');

drop view if exists extended_actions;
drop view if exists extended_control_actions;


alter table actions
    drop column employee_id;
alter table control_actions
    drop column employee_id;

alter table actions
    add column employee_id bigint;
alter table actions
    add constraint fk_action_employee foreign key (employee_id)
        references employees (id);

alter table control_actions
    add column employee_id bigint;
alter table control_actions
    add constraint fk_control_action_employee foreign key (employee_id)
        references employees (id);

alter table control_actions
    drop constraint fk_action_operation_type;
alter table control_actions
    drop constraint fk_action_product;

alter table control_actions
    add constraint fk_control_action_product foreign key (product_id)
        references products (id) on delete cascade;
alter table control_actions
    add constraint fk_control_action_operation_type foreign key (operation_type)
        references operation_types (id) on delete cascade;


create view extended_actions as
select actions.id,
       done_time,
       repair,
       operation_type,
       employee_id,
       product_id,
       active,
       batch_id,
       kit_id,
       specification_id
from actions
         left outer join products p on actions.product_id = p.id
         left outer join batches b on p.batch_id = b.id
         left outer join kits k on k.id = b.kit_id;

create view extended_control_actions as
select control_actions.id,
       done_time,
       successful,
       control_type,
       comment,
       operation_type,
       employee_id,
       product_id,
       active,
       batch_id,
       kit_id,
       specification_id
from control_actions
         left outer join products p on control_actions.product_id = p.id
         left outer join batches b on p.batch_id = b.id
         left outer join kits k on k.id = b.kit_id;