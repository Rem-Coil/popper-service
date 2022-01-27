alter table action
    drop constraint fk_operator_id;

alter table action
    add constraint fk_operator_id foreign key (operator_id) references operator on delete CASCADE;

alter table operator
    add column active boolean default true;