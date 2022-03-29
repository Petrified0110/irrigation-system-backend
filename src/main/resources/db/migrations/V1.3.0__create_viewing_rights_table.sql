CREATE TABLE viewing_rights
(
    device_id  VARCHAR(30) NOT NULL,
    account_id int         NOT NULL
);

ALTER TABLE viewing_rights
    ADD CONSTRAINT VIEWING_RIGHTS_DEVICE_ID_DEVICES_ID_FK
        FOREIGN KEY (device_id) REFERENCES devices (device_id);

ALTER TABLE viewing_rights
    ADD CONSTRAINT VIEWING_RIGHTS_ACCOUNT_ID_ACCOUNTS_ID_FK
        FOREIGN KEY (account_id) REFERENCES accounts (id);