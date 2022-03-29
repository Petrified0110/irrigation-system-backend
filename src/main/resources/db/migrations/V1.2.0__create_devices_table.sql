CREATE TABLE devices
(
    device_id    VARCHAR(30) PRIMARY KEY,
    tenant_id    uuid        NOT NULL,
    nrf_token    VARCHAR(50) NOT NULL,
    owner        int         NOT NULL,
    created_time TIMESTAMPTZ NOT NULL,
    last_poll    TIMESTAMPTZ,
    device_name  VARCHAR(50)
);

ALTER TABLE devices
    ADD CONSTRAINT DEVICES_OWNER_ACCOUNTS_ID_FK
        FOREIGN KEY (owner) REFERENCES accounts (id);