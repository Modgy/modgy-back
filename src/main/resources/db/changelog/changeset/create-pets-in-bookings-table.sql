--liquibase formatted sql
--changeset dka:2025-01-29 objectQuotingStrategy="QUOTE_ALL_OBJECTS" failOnError: true

CREATE TABLE IF NOT EXISTS pets_in_bookings
(
    id_bookings BIGINT,
    id_pets     BIGINT,
    PRIMARY KEY (id_bookings, id_pets),
    CONSTRAINT fk_id_bookings FOREIGN KEY (id_bookings) REFERENCES bookings (id_bookings),
    CONSTRAINT fk_id_pets FOREIGN KEY (id_pets) REFERENCES pets (id_pets)
);