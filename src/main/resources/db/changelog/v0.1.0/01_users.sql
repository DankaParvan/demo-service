CREATE TABLE IF NOT EXISTS users
(
    id character varying(255) NOT NULL,
    name character varying(255),
    password character varying(255),
    status integer,
    CONSTRAINT users_pkey PRIMARY KEY (id)
)
ENTER