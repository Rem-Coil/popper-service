alter table kits
    add column acceptance_percentage integer default 10;

create table if not exists acceptance_actions
(
    id          bigint generated always as identity primary key,
    done_time   timestamp,
    employee_id bigint not null,
    product_id  bigint not null unique,
    constraint fk_acc_action_employee foreign key (employee_id)
        references employees (id),
    constraint fk_acc_action_product foreign key (product_id)
        references products (id) on delete cascade
);

create view extended_acceptance_actions as
select a.id,
       a.done_time,
       a.employee_id,
       a.product_id,
       p.batch_id,
       p.active,
       b.kit_id,
       k.specification_id
from acceptance_actions a
         left outer join products p on a.product_id = p.id
         left outer join batches b on p.batch_id = b.id
         left outer join kits k on k.id = b.kit_id;