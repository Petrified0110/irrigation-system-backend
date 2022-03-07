CREATE TABLE data_table
(
    id        SERIAL PRIMARY KEY,
    data_type varchar(20)  NOT NULL,
    data      VARCHAR(255) NOT NULL,
    time      TIMESTAMPTZ  NOT NULL,
    device_id VARCHAR(30)  NOT NULL,
    tenant_id uuid         NOT NULL
);