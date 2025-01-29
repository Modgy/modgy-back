--liquibase formatted sql
--changeset mva:2025-01-29 objectQuotingStrategy="QUOTE_ALL_OBJECTS" failOnError: true

INSERT INTO pets (owner_id_pets, birth_date_pets, breed_pets, name_pets, sex_pets, type_pets)
VALUES (1, '2023-06-09', 'Спаниель', 'Барбос', 'FEMALE', 'DOG');