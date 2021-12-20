ALTER TABLE orders ALTER COLUMN delivery_info TYPE int4
    USING cast(to_char(delivery_info,'yyyymmddhhmiss') as int4);
ENTER

ALTER TABLE orders ALTER COLUMN time_created TYPE int8
    USING cast(to_char(time_created,'yyyymmddhhmiss') as int8);
ENTER