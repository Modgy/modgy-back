--liquibase formatted sql
--changeset dka:2025-01-29 objectQuotingStrategy="QUOTE_ALL_OBJECTS" failOnError: true

INSERT INTO rooms (number_rooms, area_rooms, category_id_rooms, description_rooms, visible_rooms)
VALUES ('1F', 10, 1, 'Номер с отдельным входом', true);