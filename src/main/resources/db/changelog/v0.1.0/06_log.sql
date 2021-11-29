CREATE TABLE IF NOT EXISTS financial_log_record_entity
(
    order_id uuid NOT NULL,
    amount integer NOT NULL,
    payment_transaction_id uuid,
    "timestamp" bigint NOT NULL,
    type integer,
    CONSTRAINT financial_log_record_entity_pkey PRIMARY KEY (order_id)
)
ENTER

CREATE TABLE IF NOT EXISTS notification_user
(
    name character varying(255) NOT NULL,
    CONSTRAINT notification_user_pkey PRIMARY KEY (name)
)
ENTER