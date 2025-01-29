--liquibase formatted sql
--changeset mva:2025-01-29 objectQuotingStrategy="QUOTE_ALL_OBJECTS" failOnError: true

INSERT INTO owners (first_name_owners, main_phone_owners, registration_date_owners)
values ('Иван', '89000000000000', '2024-08-23T12:09:45.0037547');