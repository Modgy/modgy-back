--liquibase formatted sql
--changeset mva:2025-01-29 objectQuotingStrategy="QUOTE_ALL_OBJECTS" failOnError: true

INSERT INTO public.pets_in_bookings (id_bookings, id_pets)
VALUES (1, 1);