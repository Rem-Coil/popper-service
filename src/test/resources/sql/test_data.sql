insert into specifications (specification_title, product_type, tested_percentage) values ('TAG', 'BOBBIN', 10);
insert into kits (kit_number, batches_quantity, batch_size, specification_id) values ('01-01', 1, 2, 1);
insert into batches (batch_number, kit_id) values (1, 1);
insert into products (product_number, active, batch_id) values (1, true, 1);
insert into products (product_number, active, batch_id) values (2, true, 1);
insert into operation_types (type, sequence_number, specification_id) values ('first', 1, 1);
insert into operation_types (type, sequence_number, specification_id) values ('second', 2, 1);
insert into employees (first_name, last_name, phone, password, active, role) values ('First', 'Last', '2', 'pass', true, 'OPERATOR');
insert into employees (first_name, last_name, phone, password, active, role) values ('First', 'Last', '3', 'pass', true, 'QUALITY_ENGINEER');

insert into actions (done_time, operation_type, product_id, employee_id) values ('2022-12-25T14:30:01', 1, 1, 2);
insert into actions (done_time, operation_type, product_id, employee_id) values ('2022-12-25T14:30:02', 1, 2, 2);
insert into actions (done_time, operation_type, product_id, employee_id) values ('2022-12-25T14:30:03', 2, 1, 2);

insert into control_actions (done_time, control_type, comment, operation_type, product_id, employee_id) values ('2022-12-25T14:30:03', 'OTK', 'Good', 1, 1, 3);
insert into control_actions (done_time, control_type, comment, operation_type, product_id, employee_id) values ('2022-12-25T14:30:03', 'OTK', 'Good', 1, 2, 3);