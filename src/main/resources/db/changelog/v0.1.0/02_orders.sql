CREATE TABLE IF NOT EXISTS orders
(
    uuid uuid NOT NULL,
    delivery_info timestamp without time zone,
    status integer,
    time_created timestamp without time zone,
    CONSTRAINT orders_pkey PRIMARY KEY (uuid)
)
ENTER

CREATE TABLE IF NOT EXISTS order_item_entity
(
    uuid uuid NOT NULL,
    amount integer,
    catalog_item_id uuid,
    CONSTRAINT order_item_entity_pkey PRIMARY KEY (uuid)
)
ENTER

CREATE TABLE IF NOT EXISTS orders_order_items
(
    order_entity_uuid uuid NOT NULL,
    order_items_uuid uuid NOT NULL,
    CONSTRAINT uk_hsoxy0jmqjnbljhiib92muad UNIQUE (order_items_uuid),
    CONSTRAINT fkbsvxkduf01apb8oy2yv0tlgad FOREIGN KEY (order_items_uuid)
        REFERENCES order_item_entity (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkp85rfysejwsbyf3l5j055pcq1 FOREIGN KEY (order_entity_uuid)
        REFERENCES orders (uuid) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
ENTER