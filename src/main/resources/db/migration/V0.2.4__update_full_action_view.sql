drop view full_action;

create view full_action as
select task.id     as task_id,
       task.task_name,
       task.task_number,
       batch.id    as batch_id,
       batch.batch_number,
       bobbin.id   as bobbin_id,
       bobbin.bobbin_number,
       bobbin.active,
       action.id   as action_id,
       action.action_type,
       action.done_time,
       action.successful,
       defect_comment.comment,
       operator.id as operator_id,
       operator.first_name,
       operator.second_name,
       operator.surname
from task
         join batch on task.id = batch.task_id
         join bobbin on batch.id = bobbin.batch_id
         join action on bobbin.id = action.bobbin_id
         left join defect_comment on action.id = defect_comment.action_id
         join operator on operator.id = action.operator_id;