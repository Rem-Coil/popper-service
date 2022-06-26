create table batch
(
    id           bigserial primary key,
    batch_number varchar(64),
    task_id      int,
    constraint fk_task_id foreign key (task_id) references task (id) on delete cascade
);

alter table operator
    add column role varchar(32) default 'operator';

alter table bobbin
    drop constraint fk_task_id;
alter table bobbin
    drop column task_id;
alter table bobbin
    alter column bobbin_number type varchar(128);
alter table bobbin
    add column batch_id bigint
        constraint fk_batch_id references batch (id) on delete cascade;

alter table task
    drop column quantity;
alter table task
    drop column winding;
alter table task
    drop column output;
alter table task
    drop column isolation;
alter table task
    drop column molding;
alter table task
    drop column crimping;
alter table task
    drop column quality;
alter table task
    drop column testing;

alter sequence action_id_seq as bigint;
alter table action
    alter column id type bigint;

alter sequence bobbin_id_seq as bigint;
alter table bobbin
    alter column id type bigint;

create view full_action as
select task.id     as task_id,
       task.task_name,
       task.task_number,
       batch.id    as batch_id,
       batch.batch_number,
       bobbin.id   as bobbin_id,
       bobbin.bobbin_number,
       action.id   as action_id,
       action.action_type,
       action.done_time,
       action.successful,
       operator.id as operator_id,
       operator.first_name,
       operator.second_name,
       operator.surname
from task
         join batch on task.id = batch.task_id
         join bobbin on batch.id = bobbin.batch_id
         join action on bobbin.id = action.bobbin_id
         join operator on operator.id = action.operator_id;