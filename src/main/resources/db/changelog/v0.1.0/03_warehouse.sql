CREATE TABLE IF NOT EXISTS catalog_items
(
    id uuid NOT NULL,
    description character varying(255) NOT NULL,
    price integer NOT NULL,
    title character varying(255) NOT NULL,
    CONSTRAINT catalog_items_pkey PRIMARY KEY (id),
    CONSTRAINT catalog_items_price_check CHECK (price >= 1)
)
ENTER

CREATE TABLE IF NOT EXISTS warehouse_items
(
    id uuid NOT NULL,
    amount integer,
    booked integer,
    CONSTRAINT warehouse_items_pkey PRIMARY KEY (id),
    CONSTRAINT fkqa0ugspmrm398lienxrkww2ou FOREIGN KEY (id)
        REFERENCES catalog_items (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
ENTER