alter table batches alter column batch_number type int USING batch_number::integer;
alter table products alter column product_number type int USING product_number::integer;