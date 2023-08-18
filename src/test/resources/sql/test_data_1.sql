insert into specifications (specification_title, product_type, tested_percentage) values ('TAG', 'BOBBIN', 10);
insert into kits (kit_number, batches_quantity, batch_size, specification_id) values ('01-01', 1, 2, 1);
insert into batches (batch_number, kit_id) values (1, 1);
insert into products (product_number, active, locked, batch_id) values (1, true, false, 1);
insert into products (product_number, active, locked, batch_id) values (2, true, false, 1);
insert into operation_types (type, sequence_number, specification_id) values ('first', 1, 1);
insert into operation_types (type, sequence_number, specification_id) values ('second', 2, 1);
insert into employees (first_name, last_name, phone, password, active, role) values ('First', 'Last', '2', 'pass', true, 'OPERATOR');
insert into employees (first_name, last_name, phone, password, active, role) values ('First', 'Last', '3', 'pass', true, 'QUALITY_ENGINEER');