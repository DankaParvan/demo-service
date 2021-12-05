CREATE TABLE IF NOT EXISTS delivery
(
    id uuid NOT NULL,
    address character varying(255),
    courier_company character varying(255),
    preferred_delivery_time timestamp without time zone,
    type integer,
    user_id character varying(255),
    warehouse integer,
    CONSTRAINT delivery_pkey PRIMARY KEY (id)
)
ENTER

CREATE TABLE IF NOT EXISTS slots
(
    slots_date character varying(255) NOT NULL,
    CONSTRAINT slots_pkey PRIMARY KEY (slots_date)
)
ENTER

CREATE TABLE IF NOT EXISTS slots_delivery_men
(
    slots_slots_date character varying(255) NOT NULL,
    delivery_men integer,
    CONSTRAINT fkptnm2ncxbcbl479aln7ayt3gt FOREIGN KEY (slots_slots_date)
        REFERENCES slots (slots_date) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
ENTER

CREATE TABLE IF NOT EXISTS slots_time_slots
(
    slots_slots_date character varying(255) NOT NULL,
    time_slots character varying(255),
    CONSTRAINT fk31obbyqjtihvwgxpgo45y3jvh FOREIGN KEY (slots_slots_date)
        REFERENCES slots (slots_date) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
ENTER