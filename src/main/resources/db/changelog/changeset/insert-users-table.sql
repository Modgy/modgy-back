--liquibase formatted sql
--changeset dka:2025-01-29 objectQuotingStrategy="QUOTE_ALL_OBJECTS" failOnError: true

INSERT INTO users (first_name_users, email_users, password_users, role_users)
values ('boss', 'boss@mail.ru', 'boss_pwd', 'ROLE_BOSS');