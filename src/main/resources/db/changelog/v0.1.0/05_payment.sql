CREATE TABLE IF NOT EXISTS payment
(
    id character varying(255) NOT NULL,
    order_id character varying(255),
    status integer,
    CONSTRAINT payment_pkey PRIMARY KEY (id)
)
ENTER
