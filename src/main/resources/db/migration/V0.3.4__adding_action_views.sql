create view extended_actions as
select actions.id,
       done_time,
       repair,
       operation_type,
       employee_id,
       product_id,
       active,
       batch_id,
       kit_id
from actions
         left outer join products p on actions.product_id = p.id
         left outer join batches b on p.batch_id = b.id;

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
       kit_id
from control_actions
         left outer join products p on control_actions.product_id = p.id
         left outer join batches b on p.batch_id = b.id;

