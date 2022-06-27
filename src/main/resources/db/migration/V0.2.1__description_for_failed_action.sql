create table defects_comment (
    action_id bigint primary key constraint fk_action_id references action(id),
    comment text
);