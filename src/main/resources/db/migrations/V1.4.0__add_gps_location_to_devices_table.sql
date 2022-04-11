CREATE TABLE latest_locations
(
    device_id VARCHAR(30) PRIMARY KEY,
    time      TIMESTAMPTZ NOT NULL,
    latitude  float       NOT NULL,
    longitude float       NOT NULL
);

ALTER TABLE latest_locations
    ADD CONSTRAINT LATEST_LOCATIONS_DEVICE_ID_DEVICES_DEVICE_ID_FK
        FOREIGN KEY (device_id) REFERENCES devices (device_id);