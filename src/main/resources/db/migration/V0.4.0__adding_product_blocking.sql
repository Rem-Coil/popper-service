alter table products add column locked bool default false;
alter table control_actions add column need_repair bool default false;

drop view if exists extended_control_actions;

create view extended_control_actions as
select control_actions.id,
       done_time,
       successful,
       need_repair,
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


