CREATE TABLE IF NOT EXISTS order_entity_items_map
(
    order_entity_uuid uuid NOT NULL,
    items_map integer,
    items_map_key uuid NOT NULL,
    CONSTRAINT order_entity_items_map_pkey PRIMARY KEY (order_entity_uuid, items_map_key),
    CONSTRAINT fk933f23blgus2rgs06aue0dttc FOREIGN KEY (order_entity_uuid)
        REFERENCES orders (uuid) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
ENTER