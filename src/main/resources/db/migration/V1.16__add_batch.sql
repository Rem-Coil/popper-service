create table batch (
    id bigserial primary key,
    task_id int,
    foreign key (task_id) references task(id) on delete cascade
);

alter table bobbin drop constraint fk_task_id;
alter table bobbin drop column task_id;

alter table bobbin add column batch_id bigint references batch(id) on delete cascade;

alter table task drop column quantity;