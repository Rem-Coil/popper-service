insert into actions (done_time, operation_type, product_id, employee_id) values ('2022-12-25T14:30:01', 1, 1, 2);
insert into actions (done_time, operation_type, product_id, employee_id) values ('2022-12-25T14:30:02', 1, 2, 2);
insert into actions (done_time, operation_type, product_id, employee_id) values ('2022-12-25T14:30:03', 2, 1, 2);

insert into control_actions (done_time, control_type, comment, operation_type, product_id, employee_id) values ('2022-12-25T14:30:03', 'OTK', 'Good', 1, 1, 3);
insert into control_actions (done_time, control_type, comment, operation_type, product_id, employee_id) values ('2022-12-25T14:30:03', 'OTK', 'Good', 1, 2, 3);

update products set active = false where id = 1;
insert into products (product_number, active, batch_id) values (1, true, 1);