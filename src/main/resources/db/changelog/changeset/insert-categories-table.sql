--liquibase formatted sql
--changeset dka:2025-01-29 objectQuotingStrategy="QUOTE_ALL_OBJECTS" failOnError: true

INSERT INTO categories (name_categories)
VALUES ('Room');